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
    
    public Server(int port, String path) {
        this.port = port;
        this.path = path;
        isServer = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for connection...");
            serverSocket.setSoTimeout(30000);
            socket = serverSocket.accept(); // attendra au maximum 30 secondes
            System.out.println("Client connected.");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connect = true;

        } catch (IOException e) {
            System.err.println("Error setting up server: " + e.getMessage());
            connect = false;
        }
    }

    public void close() {
        try {
            socket.close();
            serverSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public void firstSync() throws IOException {
        System.out.println("Waiting for files list...");
        try{
            List<File> files = receiveFilesList();
            System.out.println("Files list received.");
            System.out.println("Files to receive: " + files.size());
            for(File file : files){
                System.out.println("Receiving file: " + file.getName());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException{
        Server server = new Server(117, "lol");
        System.out.println(server.connect);
        server.firstSync();
        server.close();
    }
}
