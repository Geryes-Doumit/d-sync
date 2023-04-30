package src.syncing;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        System.out.println("Files list sent.");

        System.out.println("Waiting for files list...");
        try{
            List<DateAndName> listServer = receiveFilesList();
            for (DateAndName file : listServer) {
                System.out.println(file.getName());
            }

            resetConnection();

            System.out.println("Files list received.");

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileClient : listClient){
                        if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                            contains = true;
                            if (fileServer.getDate() > fileClient.getDate()) {
                                System.out.println("Server send " + fileServer.getName() + "...");
                                receiveFile(fileServer);
                            }
                            // else if(fileServer.getDate() < fileClient.getDate()) {
                            //     System.out.println("Server receive " + fileClient.getName() + "...");
                            //     // receiveFile();
                            // }
                        }
                    }
                    if (!contains) {
                        System.out.println("Server send " + fileServer.getName() + "...");
                        receiveFile(fileServer);
                    }
                }
            }

            for (DateAndName fileClient : listClient) {
                if (fileClient.getType().equals("File")) {
                    // System.out.println("Receiving file " + fileClient.getName() + "...");
                    Boolean contains = false;

                    for (DateAndName fileServer : listServer){
                        if (fileClient.getName().equals(fileServer.getName()) && fileServer.getType().equals("File")) {
                            contains = true;
                            if (fileClient.getDate() > fileServer.getDate()) {
                                System.out.println("Client send " + fileClient.getName() + "...");
                                sendFile(fileClient);
                            }
                            // else if(fileClient.getDate() < fileServer.getDate()) {
                            //     System.out.println("Client receive " + fileClient.getName() + "...");
                            // }
                        }
                    }
                    if (!contains) {
                        System.out.println("Client send " + fileClient.getName() + "...");
                        sendFile(fileClient);
                    }
                }
            }
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void testRelatifPath(){
        List <DateAndName> listClient = listFiles(path, path);
        for (DateAndName file : listClient) {
            System.out.println("Gestion de " + file.getName());
            System.out.println("Path : " + file.getPath());
            String folderPath = file.getPath().substring(0, file.getPath().lastIndexOf("/"));
            System.out.println("Folder path : " + folderPath);
            System.out.println("............................................");
        }

        System.out.println("Waiting for files list...");
        try{
            List<DateAndName> listServer = receiveFilesList();
            System.out.println("Files list received.");

            System.out.println("Sending files list...");
            sendMessage(listClient);

            for (DateAndName file : listServer) {
                System.out.println("Gestion de " + file.getName());
                System.out.println("Path : " + file.getPath());
                String folderPath = file.getPath().lastIndexOf("/") != -1 ? file.getPath().substring(0, file.getPath().lastIndexOf("/")) : "";
                System.out.println("Folder path : " + folderPath);
                System.out.println("............................................");
            }

            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error sending files list: " + e.getMessage());
        }


    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("192.168.1.55", 117, "C:/Users/skyec/Desktop/test");

        client.connect();
        // client.firstSync();
        client.testRelatifPath();
        client.close();
    }
}
