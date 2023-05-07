package src.syncing;

import java.io.*;
import java.util.List;

/**
 * Client class that extends Network. It is used to create a client that will be able to send and receive files.
 * See {@link src.syncing.Network} class for more information.
 * <br/>
 * Author: Marc Proux
 */
public class Client extends Network {

    /**
     * Constructor of the Client class.
     * 
     * @param ip The ip of the server.
     * @param port The port on which the server will be listening.
     * @param path The path of the folder that will be synchronized.
     * @throws Exception
     */
    public Client(String ip, int port, String path) throws Exception{
        this.ip = ip;
        this.port = port;
        this.path = path;
        isServer = false;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void run(){
        while(true){
            if(active){
                try{
                    connect();
                    while(connect){
                        File folder = new File(path);

                        Boolean state[] = {folder.exists() && folder.isDirectory(), syncCurrent, firstSync};
                        Boolean state2[] = (Boolean[]) receiveMessage();
                        sendMessage(state);

                        Boolean foldesrExist = state2[0] && state[0];
                        System.out.println("I will sync : "+state2[1]+" && "+state[1]);
                        sync = state2[1] && state[1];
                        Boolean firstSyncAll = state2[2] || state[2];

                        resetConnection();

                        if(!sync && syncCurrent){
                            if (!messages.get(7).equals("Waiting for server to resume syncing.")) {
                                addMessage("Waiting for server to resume syncing.");
                            }
                        }
                        else if(!syncCurrent){
                            if (!messages.get(7).equals("Syncing Paused.")) {
                                addMessage("Syncing Paused.");
                            }
                        }
                        else if (firstSyncAll && sync && foldesrExist){
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
                    if(!messages.get(6).equals("Connection lost.")){
                        addMessage("Connection lost.");
                    }
                    System.out.println("Error in run :"+e);
                    try{
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        System.out.println("Error while waiting: " + ie.getMessage());
                    }
                }
            }
        }
    }
}
