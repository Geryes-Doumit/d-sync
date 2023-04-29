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
        List<File> list1 = new ArrayList<File>(Arrays.asList(folder.listFiles())); 
        System.out.println("Waiting for files list...");
        try{
            List<File> list2 = receiveFilesList();
            System.out.println("Files list received.");
            // Double for loop that will go through every element in the two lists
            for (File file2 : list2) {
                if (file2.isFile()) {

                    // For each file in list2, it checks if a file of list1 has the same name.
                    // If it does, then we keep the file with the latest modified date.
                    // If it doesn't, then we copy it in folder1.

                    // This function will copy everything from folder2 that isn't in folder1, but not vice-versa.
                    // That's why we call it twice later on to merge the two folders.

                    Boolean contains = false;
                    for (File file1 : list1) {
                        if(file2.getName().equals(file1.getName()) && file1.isFile()) {
                            contains = true;
                            try{
                                if(file2.lastModified() > file1.lastModified()) {
                                    System.out.println("Copied from client to server: " + file2.getName()+"due to last modified date");  
                                    // System.out.println("Copied from " + path2 + " to " + path1 + ".");
                                }
                                else if(file2.lastModified() < file1.lastModified()) {
                                    System.out.println("Copied from server to client: " + file1.getName()+"due to last modified date");
                                    // System.out.println("Copied from " + path1 + " to " + path2 + ".");
                                }
                            }
                            catch(IOException e) {
                                // e.getCause();
                            }
                        }
                    }
                    if (!contains) {
                        try{
                            System.out.println("Copied from client to server: " + file2.getName()+"due to not being in server");
                            // System.out.println("Created in " + path1 + " from " + path2 + ".");
                        }
                        catch(IOException e) {
                            // e.getCause();
                        }
                    }
                }
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
