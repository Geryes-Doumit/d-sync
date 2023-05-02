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
            List<DateAndName> listServer = receiveFilesList();

            resetConnection();

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
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
                        receiveFile(fileServer);
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
                                sendFile(fileClient);
                            }
                        }
                    }
                    if (!contains) {
                        sendFile(fileClient);
                    }
                }
            }
            lastState = listFiles(path, path);
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void syncAndDelete() throws IOException {
        List <DateAndName> listClient = listFiles(path, path);

        sendMessage(listClient);

        try{
            List<DateAndName> listServer = receiveFilesList();

            resetConnection();

            for (DateAndName fileClient : listClient){
                if (fileClient.getType().equals("File")){
                    DateAndName fileServer = listServer.stream().filter(o -> o.getPath().equals(fileClient.getPath()) && o.getType().equals("File")).findFirst().orElse(null);
                    if (fileServer != null){
                        // System.out.println("File " + fileClient.getPath() + " is on the server and client.");
                        if (fileServer.getDate() > fileClient.getDate()) {
                            System.out.println("File " + fileServer.getPath() + " has been modified on the server.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileServer);
                        }
                    } else {
                        // System.out.println("File " + fileClient.getPath() + " is on the client but not on the server.");
                        if (lastState.stream().anyMatch(o -> o.getType().equals(fileClient.getType()) && o.getPath().equals(fileClient.getPath()))){
                            System.out.println("File " + fileClient.getPath() + " has been deleted on the server.");
                            System.out.println("I will delete mine.");
                            // deleteFile(fileClient);
                        } else {
                            System.out.println("File " + fileClient.getPath() + " has been added on the client.");
                            System.out.println("I will send it to the server.");
                            // sendFile(fileClient);
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
                            System.out.println("I will send it to the server.");
                            // sendFile(fileClient);
                        }
                    } else {
                        // System.out.println("File " + fileServer.getPath() + " is on the server but not on the client.");
                        if (lastState.stream().anyMatch(o -> o.getType().equals(fileServer.getType()) && o.getPath().equals(fileServer.getPath()))){
                            System.out.println("File " + fileServer.getPath() + " has been deleted on the client.");
                        } else {
                            System.out.println("File " + fileServer.getPath() + " has been added on the server.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileServer);
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
        listClient = listFiles(path, path);
        sendMessage(listClient);
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("10.5.2.148", 117, "/Users/marc/OneDrive - uha.fr/Cours/GitHub/TestCli");

        client.connect();
        client.firstSync();
        try{
            Thread.sleep(30000);
        }
        catch(InterruptedException ie){
            System.out.println("Error while waiting");
        }
        client.syncAndDelete();
        // client.testList();
        client.close();
    }
}
