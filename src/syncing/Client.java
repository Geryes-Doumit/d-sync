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
        File folder = new File(path);
        List<File> filesTemp = new ArrayList<File>(Arrays.asList(folder.listFiles()));
        List<DateAndName> list1 = new ArrayList<DateAndName>();

        for (File file : filesTemp) {
            String type;
            if (file.isFile()) {type = "File";}
            else {type = "Directory";}
            list1.add(new DateAndName(file.getName(), file.lastModified(), type));
        }

        sendMessage(list1);
        System.out.println("Sent files list.");

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
