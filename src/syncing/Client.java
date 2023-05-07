package src.syncing;

import java.io.*;
import java.util.List;

public class Client extends Network {
    public Client(String ip, int port, String path) throws Exception{
        this.ip = ip;
        this.port = port;
        this.path = path;
        isServer = false;
    }

    public void firstSync() throws IOException {
        List <DateAndName> listClient = listFiles(path, path);

        sendMessage(listClient);

        try{
            List<DateAndName> listServer = (List<DateAndName>) receiveMessage();

            resetConnection();

            for (DateAndName fileServer : listServer) {
                Boolean contains = false;

                for (DateAndName fileClient : listClient){
                    if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                        contains = true;
                        if (fileServer.getDate() > fileClient.getDate()) {
                            receiveFile(fileServer);
                        }
                    }
                }
                if (!contains) {
                    if (fileServer.getType().equals("File")){
                        receiveFile(fileServer);
                    }
                    else{
                        File folder = new File(path + "/"+ fileServer.getPath());
                        folder.mkdirs();
                    }
                }
            }

            for (DateAndName fileClient : listClient) {
                Boolean contains = false;

                for (DateAndName fileServer : listServer){
                    if (fileClient.getName().equals(fileServer.getName()) && fileServer.getType().equals("File")) {
                        contains = true;
                        if (fileClient.getDate() > fileServer.getDate()) {
                            sendFile(fileClient);
                        }
                    }
                }
                if (!contains) {
                    if (fileClient.getType().equals("File")){
                        sendFile(fileClient);
                    }
                }
            }
            lastState = listFiles(path, path);
            addMessage("Folders combined.");
            firstSync = false;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void syncAndDelete() throws IOException {
        isChange = false;
        List <DateAndName> listClient = listFiles(path, path);

        sendMessage(listClient);

        try{
            List<DateAndName> listServer = (List<DateAndName>) receiveMessage();

            resetConnection();

            for (DateAndName fileClient : listClient){
                DateAndName fileServer = listServer.stream().filter(o -> o.getPath().equals(fileClient.getPath()) && o.getType().equals(o.getType())).findFirst().orElse(null);
                if (fileServer != null){
                    if (fileClient.getDate() > fileServer.getDate() && fileClient.getType().equals("File")) {
                        sendFile(fileClient);
                        isChange = true;
                    }
                } else {
                    isChange = true;
                    if (lastState.stream().anyMatch(o -> o.getType().equals(fileClient.getType()) && o.getPath().equals(fileClient.getPath()))){
                        deleteFile(fileClient);
                        addMessage("Deleted " + fileClient.getName() + ".");
                    } else {
                        if (fileClient.getType().equals("File")) {
                            sendFile(fileClient);
                        }
                    }
                }
            }

            for (DateAndName fileServer : listServer){
                DateAndName fileClient = listClient.stream().filter(o -> o.getPath().equals(fileServer.getPath()) && o.getType().equals(fileServer.getType())).findFirst().orElse(null);
                if (fileClient != null){
                    if (fileServer.getDate() > fileClient.getDate() && fileClient.getType().equals("File")) {
                        receiveFile(fileServer);
                        addMessage("Copied the modified file.");
                        isChange = true;
                    }
                } else {
                    isChange = true;
                    if (!lastState.stream().anyMatch(o -> o.getType().equals(fileServer.getType()) && o.getPath().equals(fileServer.getPath()))){
                        if (fileServer.getType().equals("File")) {
                            receiveFile(fileServer);
                            addMessage("Copied " + fileServer.getName() + ".");
                        }
                        else{
                            createDirectory(fileServer);
                            addMessage("Copied " + fileServer.getName() + ".");
                        }
                    }
                }
            }

            lastState.clear();
            lastState = listFiles(path, path);
            if (isChange) {
                addMessage("Folders combined.");
            }
            else {
                if (!messages.get(7).equals("No changes detected") && !messages.get(7).equals("No changes detected.") && !messages.get(7).equals("No changes detected..") && !messages.get(7).equals("No changes detected...") ) {
                    addMessage("No changes detected");
                }
                else if (messages.get(7).equals("No changes detected")) {
                    messages.set(7, "No changes detected.");
                }
                else if (messages.get(7).equals("No changes detected.")) {
                    messages.set(7, "No changes detected..");
                }
                else if (messages.get(7).equals("No changes detected..")) {
                    messages.set(7, "No changes detected...");
                }
                else if (messages.get(7).equals("No changes detected...")) {
                    messages.set(7, "No changes detected");
                }
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void run(){
        while(true){
            if(active){
                try{
                    connect();
                    while(connect){
                        File folder = new File(path);
                        sendMessage(folder.exists() && folder.isDirectory());
                        Boolean foldesrExist = folder.exists() && folder.isDirectory() && (Boolean) receiveMessage();

                        resetConnection();

                        sendMessage(syncCurrent);
                        Boolean sync = syncCurrent && (Boolean) receiveMessage();

                        resetConnection();

                        if (firstSync && sync && foldesrExist){
                            firstSync();
                        }
                        else if (sync && foldesrExist){
                            syncAndDelete();
                        }
                        else if(!foldesrExist && folder.exists()){
                            if (!messages.get(7).equals("A problem as occured on the server's side.")) {
                                addMessage("A problem as occured on the server's side.");
                            }
                        }
                        else if(!folder.exists()){
                            if (!messages.get(7).equals("The local folder path is'nt valid anymore.")) {
                                addMessage("The local folder path is'nt valid anymore.");
                            }
                        }
                        Thread.sleep(2000);
                    }

                }
                catch(Exception e){
                    addMessage("Connection lost.");
                    try{
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        System.out.println("Error while waiting: " + ie.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("192.168.1.55", 117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test_DSync/Client");
        client.syncCurrent = true;

        client.start();
    }
}
