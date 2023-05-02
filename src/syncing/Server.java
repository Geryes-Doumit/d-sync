package src.syncing;

import java.io.*;
import java.util.List;

public class Server extends Network{
    
    public Server(int port, String path) throws Exception{
        this.port = port;
        this.path = path;
        isServer = true;
    }

    public void firstSync() throws IOException {
        // listServer.clear();
        // listClient.clear();
        // lastState.clear();

        listServer = listFiles(path, path);

        try{
            listClient = receiveFilesList();

            sendMessage(listServer);

            resetConnection();

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileClient : listClient){
                        if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                            contains = true;
                            if (fileServer.getDate() > fileClient.getDate()) {
                                sendFile(fileServer);
                            }
                        }
                    }
                    if (!contains) {
                        sendFile(fileServer);
                    }
                }
            }

            for (DateAndName fileClient : listClient) {
                if (fileClient.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileServer : listServer){
                        if (fileClient.getName().equals(fileServer.getName()) && fileServer.getType().equals("File")) {
                            contains = true;
                            if (fileClient.getDate() > fileServer.getDate()) {
                                receiveFile(fileClient);
                            }
                        }
                    }
                    if (!contains) {
                        receiveFile(fileClient);
                    }
                }
            }
            lastState = listFiles(path, path);
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }

        
    }

    public void syncAndDelete() throws IOException{
        List <DateAndName> listServer = listFiles(path, path);

        try {
            List<DateAndName> listClient = receiveFilesList();

            sendMessage(listServer);

            resetConnection();

            for (DateAndName fileClient : listClient){
                if (fileClient.getType().equals("File")){
                    DateAndName fileServer = listServer.stream().filter(o -> o.getPath().equals(fileClient.getPath()) && o.getType().equals("File")).findFirst().orElse(null);
                    if (fileServer != null){
                        // System.out.println("File " + fileClient.getPath() + " is on the server and client.");
                        if (fileServer.getDate() > fileClient.getDate()) {
                            System.out.println("File " + fileServer.getPath() + " has been modified on the server.");
                            System.out.println("I will send it to the client.");
                            // sendFile(fileServer);
                        }
                    } else {
                        // System.out.println("File " + fileClient.getPath() + " is on the client but not on the server.");
                        if (lastState.stream().anyMatch(o -> o.getType().equals(fileClient.getType()) && o.getPath().equals(fileClient.getPath()))){
                            System.out.println("File " + fileClient.getPath() + " has been deleted on the server.");
                        } else {
                            System.out.println("File " + fileClient.getPath() + " has been added on the client.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileClient);
                        }
                    }
                }
            }

            for (DateAndName fileServer : listServer){
                if (fileServer.getType().equals("File")){
                    DateAndName fileClient = listClient.stream().filter(o -> o.getPath().equals(fileServer.getPath()) && o.getType().equals("File")).findFirst().orElse(null);
                    if (fileClient != null){
                        // System.out.println("File " + fileServer.getPath() + " is on the server and client.");
                        if (fileClient.getDate() > fileServer.getDate()) {
                            System.out.println("File " + fileClient.getPath() + " has been modified on the client.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileClient);
                        }
                    } else {
                        // System.out.println("File " + fileServer.getPath() + " is on the server but not on the client.");
                        if (lastState.stream().anyMatch(o -> o.getType().equals(fileServer.getType()) && o.getPath().equals(fileServer.getPath()))){
                            System.out.println("File " + fileServer.getPath() + " has been deleted on the client.");
                            System.out.println("I will delete it mine.");
                            // deleteFile(fileServer);
                        } else {
                            System.out.println("File " + fileServer.getPath() + " has been added on the server.");
                            System.out.println("I will send it to the client.");
                            // sendFile(fileServer);
                        }
                    }
                }
            }

            lastState.clear();
            lastState = listFiles(path, path);
            System.out.println("Done.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void testList() throws IOException{
        System.out.println("Last state :");
        for (DateAndName file : lastState){
            System.out.println("Name : " + file.getName() + " Date : " + file.getDate() + "Type : "+ file.getType()+" Path : "+file.getPath());
            System.out.println(".....................");
        }

        listServer = listFiles(path, path);
        System.out.println("Current server state :");
        for (DateAndName file : listServer){
            System.out.println("Name : " + file.getName() + " Date : " + file.getDate() + "Type : "+ file.getType()+" Path : "+file.getPath());
            System.out.println("Was in last state : "+lastState.stream().anyMatch(o -> o.getName().equals(file.getName()) && o.getType().equals(file.getType()) && o.getPath().equals(file.getPath())));
            System.out.println(".....................");
        }
        try {
            listClient = receiveFilesList();
            System.out.println("Current client state :");
            for (DateAndName file : listClient){
                System.out.println("Name : " + file.getName() + " Date : " + file.getDate() + "Type : "+ file.getType()+" Path : "+file.getPath());
                System.out.println("Was in last state : "+lastState.stream().anyMatch(o -> o.getName().equals(file.getName()) && o.getType().equals(file.getType()) && o.getPath().equals(file.getPath())));
                System.out.println(".....................");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }


    public static void main(String[] args) throws Exception{
        Server server = new Server(117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test");

        server.connect();
        server.firstSync();
        try{
            Thread.sleep(30000);
        }
        catch(InterruptedException ie){
            System.out.println("Error while waiting");
        }
        server.syncAndDelete();
        // server.testList();
        server.close();
    }
}
