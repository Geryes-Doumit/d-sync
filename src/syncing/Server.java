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
        File folder = new File(path);
        List<File> list1Temp = new ArrayList<File>(Arrays.asList(folder.listFiles())); 
        List<DateAndName> list1 = new ArrayList<DateAndName>();

        for (File file : list1Temp) {
            String type;
            if (file.isFile()) {type = "File";}
            else {type = "Directory";}
            list1.add(new DateAndName(file.getName(), file.lastModified(), type));
        }

        System.out.println("Waiting for files list...");
        try{
            List<DateAndName> list2 = receiveFilesList();
            System.out.println("Files list received.");

            for (DateAndName file2 : list2) {
                if (file2.getType().equals("File")) {
                    // System.out.println("Receiving file " + file2.getName() + "...");
                    Boolean contains = false;

                    for (DateAndName file1 : list1){
                        if (file2.getName().equals(file1.getName()) && file1.getType().equals("File")) {
                            contains = true;
                            if (file2.getDate() > file1.getDate()) {
                                System.out.println("Receiving file " + file2.getName() + "...");
                            }
                            else if(file2.getDate() < file1.getDate()) {
                                System.out.println("Sending file " + file1.getName() + "...");
                            }
                        }
                    }
                    if (!contains) {
                        System.out.println("Sending file " + file2.getName() + "...");
                    }
                }
            }
            System.out.println("Recevied list processed.");
            
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }

        System.out.println("Sending files list...");
        sendMessage(list1);
    }


    public static void main(String[] args) throws IOException{
        Server server = new Server(117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test");
        System.out.println(server.connect);
        server.firstSync();
        server.close();
    }
}
