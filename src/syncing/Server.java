package src.syncing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Server extends Network{
    
    public Server(int port, String path) throws Exception{
        this.port = port;
        this.path = path;
        isServer = true;
    }

    public void firstSync() throws IOException {
        List <DateAndName> listServer = listFiles(path, path);
    //     for (DateAndName file : listServer) {
    //         System.out.println(file.getName());
    //     }

        System.out.println("Waiting for files list...");
        try{
            List<DateAndName> listClient = receiveFilesList();
            System.out.println("Files list received.");

            System.out.println("Sending files list...");
            sendMessage(listServer);

            for (DateAndName file : listClient) {
                System.out.println(file.getName());
            }

            resetConnection();

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileClient : listClient){
                        if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                            contains = true;
                            if (fileServer.getDate() > fileClient.getDate()) {
                                System.out.println("Server send " + fileServer.getName() + "...");
                                sendFile(fileServer);
                            }
                            // else if(fileServer.getDate() < fileClient.getDate()) {
                            //     System.out.println("Server receive " + fileClient.getName() + "...");
                            //     // receiveFile();
                            // }
                        }
                    }
                    if (!contains) {
                        System.out.println("Server send " + fileServer.getName() + "...");
                        sendFile(fileServer);
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
                                receiveFile(fileClient);
                            }
                            // else if(fileClient.getDate() < fileServer.getDate()) {
                            //     System.out.println("Client receive " + fileClient.getName() + "...");
                            // }
                        }
                    }
                    if (!contains) {
                        System.out.println("Client send " + fileClient.getName() + "...");
                        receiveFile(fileClient);
                    }
                }
            }
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }

        
    }


    public static void main(String[] args) throws Exception{
        Server server = new Server(117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test");

        server.connect();
        server.firstSync();
        server.close();
    }
}
