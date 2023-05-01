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

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileClient : listClient){
                        if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                            contains = true;
                            if (fileServer.getDate() > fileClient.getDate()) {
                                System.out.println("File " + fileServer.getName() + " has been modified on the server.");
                                System.out.println("I will receive the file.");
                                // receiveFile(fileServer);
                            }
                        }
                    }
                    if (!contains) {
                        if (lasteState.contains(fileServer)){
                            System.out.println("File " + fileServer.getName() + " has been deleted on the server.");
                            System.out.println("I will delete it.");
                            // deleteFile(fileServer);
                        } else {
                            System.out.println("File " + fileServer.getName() + " has been added on the server.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileServer);
                        }
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
                                System.out.println("File " + fileClient.getName() + " has been modified on the client.");
                                System.out.println("I will send the file.");
                                // sendFile(fileClient);
                            }
                        }
                    }
                    if (!contains) {
                        if (lasteState.contains(fileClient)){
                            System.out.println("File " + fileClient.getName() + " has been deleted on the client.");
                        } else {
                            System.out.println("File " + fileClient.getName() + " has been added on the client.");
                            System.out.println("I will send the file.");
                            // sendFile(fileClient);
                        }
                    }
                }
            }
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void testContains() throws IOException{
        listServer.clear();
        listClient.clear();
        List <DateAndName> listClient = listFiles(path, path);

        try {
            sendMessage(listClient);
            listServer = receiveFilesList();
            resetConnection();

            for (DateAndName fileClient : listClient){
                if (fileClient.getType().equals("File")){
                    if (listServer.contains(fileClient)){
                        System.out.println("File " + fileClient.getName() + " is on the server and client.");
                    } else {
                        System.out.println("File " + fileClient.getName() + " is not on the server.");
                    }
                }
            }

            for (DateAndName fileServer : listServer){
                if (fileServer.getType().equals("File")){
                    if (listClient.contains(fileServer)){
                        System.out.println("File " + fileServer.getName() + " is on the client and server.");
                    } else {
                        System.out.println("File " + fileServer.getName() + " is not on the client.");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("192.168.1.55", 117, "C:/Users/skyec/Desktop/test");

        client.connect();
        // client.firstSync();
        client.testContains();
        client.close();
    }
}
