package src.syncing;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client extends Network {
    public Client(String ip, int port, String path) {
        this.ip = ip;
        this.port = port;
        this.path = path;
        isServer = false;

        try{
            socket = new Socket(ip, port);
            System.out.println("Connected to server.");

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            is = socket.getInputStream();
            ois = new ObjectInputStream(socket.getInputStream());
            bis = new BufferedInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());
            connect = true;

        } catch (IOException e) {
            System.err.println("Error setting up client: " + e.getMessage());
            connect = false;
        }
    }

    public void close() {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public void firstSync() throws IOException {
        List <DateAndName> listClient = listFiles(path, path);

        sendMessage(listClient);
        System.out.println("Sent files list.");

        System.out.println("Waiting for files list...");
        try{
            List<DateAndName> listServer = receiveFilesList();
            System.out.println("Files list received.");

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileClient : listClient){
                        if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                            contains = true;
                            if (fileServer.getDate() > fileClient.getDate()) {
                                System.out.println("Server send " + fileServer.getName() + "...");
                                receiveFile(ois, bis);
                            }
                            else if(fileServer.getDate() < fileClient.getDate()) {
                                System.out.println("Server receive " + fileClient.getName() + "...");
                                // receiveFile();
                            }
                        }
                    }
                    if (!contains) {
                        System.out.println("Server send " + fileServer.getName() + "...");
                        receiveFile(ois, bis);
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
                                sendFile(fileClient, oos, bos);
                            }
                            else if(fileClient.getDate() < fileServer.getDate()) {
                                System.out.println("Client receive " + fileClient.getName() + "...");
                            }
                        }
                    }
                    if (!contains) {
                        System.out.println("Client send " + fileClient.getName() + "...");
                        sendFile(fileClient, oos, bos);
                    }
                }
            }
            System.out.println("Done.");
            
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException{
        Client client = new Client("192.168.1.55", 117, "C:/Users/skyec/Desktop/test");
        System.out.println(client.connect);
        client.firstSync();
        client.close();
    }
}
