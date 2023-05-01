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
        listServer.clear();
        listClient.clear();
        List <DateAndName> listClient = listFiles(path, path);

        sendMessage(listClient);

        try{
            List<DateAndName> listServer = receiveFilesList();

            resetConnection();

            for (DateAndName fileClient : listClient){
                if (fileClient.getType().equals("File")){
                    DateAndName fileServer = listServer.stream().filter(o -> o.getName().equals(fileClient.getName()) && o.getType().equals("File")).findFirst().orElse(null);
                    if (fileServer != null){
                        System.out.println("File " + fileClient.getName() + " is on the server and client.");
                        if (fileServer.getDate() > fileClient.getDate()) {
                            System.out.println("File " + fileServer.getName() + " has been modified on the server.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileServer);
                        }
                    } else {
                        System.out.println("File " + fileClient.getName() + " is on the client but not on the server.");
                        if (lasteState.contains(fileClient)){
                            System.out.println("File " + fileClient.getName() + " has been deleted on the server.");
                            System.out.println("I will delete mine.");
                            // deleteFile(fileClient);
                        } else {
                            System.out.println("File " + fileClient.getName() + " has been added on the client.");
                            System.out.println("I will send it to the server.");
                            // sendFile(fileClient);
                        }
                    }
                }
            }

            for (DateAndName fileServer : listServer){
                if (fileServer.getType().equals("File")){
                    DateAndName fileClient = listClient.stream().filter(o -> o.getName().equals(fileServer.getName()) && o.getType().equals("File")).findFirst().orElse(null);
                    if (fileClient != null){
                        System.out.println("File " + fileServer.getName() + " is on the server and client.");
                        if (fileClient.getDate() > fileServer.getDate()) {
                            System.out.println("File " + fileClient.getName() + " has been modified on the client.");
                            System.out.println("I will send it to the server.");
                            // sendFile(fileClient);
                        }
                    } else {
                        System.out.println("File " + fileServer.getName() + " is on the server but not on the client.");
                        if (lasteState.contains(fileServer)){
                            System.out.println("File " + fileServer.getName() + " has been deleted on the client.");
                        } else {
                            System.out.println("File " + fileServer.getName() + " has been added on the server.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileServer);
                        }
                    }
                }
            }

            // for (DateAndName fileServer : listServer) {
            //     if (fileServer.getType().equals("File")) {
            //         Boolean contains = false;

            //         for (DateAndName fileClient : listClient){
            //             if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
            //                 contains = true;
            //                 if (fileServer.getDate() > fileClient.getDate()) {
            //                     System.out.println("File " + fileServer.getName() + " has been modified on the server.");
            //                     System.out.println("I will receive the file.");
            //                     // receiveFile(fileServer);
            //                 }
            //             }
            //         }
            //         if (!contains) {
            //             if (lasteState.contains(fileServer)){
            //                 System.out.println("File " + fileServer.getName() + " has been deleted on the server.");
            //                 System.out.println("I will delete it.");
            //                 // deleteFile(fileServer);
            //             } else {
            //                 System.out.println("File " + fileServer.getName() + " has been added on the server.");
            //                 System.out.println("I will receive the file.");
            //                 // receiveFile(fileServer);
            //             }
            //         }
            //     }
            // }

            // for (DateAndName fileClient : listClient) {
            //     if (fileClient.getType().equals("File")) {
            //         Boolean contains = false;

            //         for (DateAndName fileServer : listServer){
            //             if (fileClient.getName().equals(fileServer.getName()) && fileServer.getType().equals("File")) {
            //                 contains = true;
            //                 if (fileClient.getDate() > fileServer.getDate()) {
            //                     System.out.println("File " + fileClient.getName() + " has been modified on the client.");
            //                     System.out.println("I will send the file.");
            //                     // sendFile(fileClient);
            //                 }
            //             }
            //         }
            //         if (!contains) {
            //             if (lasteState.contains(fileClient)){
            //                 System.out.println("File " + fileClient.getName() + " has been deleted on the client.");
            //             } else {
            //                 System.out.println("File " + fileClient.getName() + " has been added on the client.");
            //                 System.out.println("I will send the file.");
            //                 // sendFile(fileClient);
            //             }
            //         }
            //     }
            // }
            lasteState.clear();
            lasteState = listFiles(path, path);
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("192.168.1.55", 117, "C:/Users/skyec/Desktop/test");

        client.connect();
        client.firstSync();
        // client.syncAndDelete();
        client.close();
    }
}
