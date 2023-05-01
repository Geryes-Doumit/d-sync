package src.syncing;

import java.io.*;
import java.util.List;

public class Server extends Network{
    
    public Server(int port, String path) throws Exception{
        this.port = port;
        this.path = path;
        isServer = true;
    }

    public void firstSync() throws IOException {
        listServer.clear();
        listClient.clear();
        lasteState.clear();

        listServer = listFiles(path, path);

        try{
            listClient = receiveFilesList();

            sendMessage(listServer);

            resetConnection();

            for (DateAndName fileServer : listServer) {
                if (fileServer.getType().equals("File")) {
                    Boolean contains = false;

                    for (DateAndName fileClient : listClient){
                        if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                            contains = true;
                            if (fileServer.getDate() > fileClient.getDate()) {
                                sendFile(fileServer);
                            }
                        }
                    }
                    if (!contains) {
                        sendFile(fileServer);
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
                                receiveFile(fileClient);
                            }
                        }
                    }
                    if (!contains) {
                        receiveFile(fileClient);
                    }
                }
            }
            lasteState = listFiles(path, path);
            System.out.println("Done.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }

        
    }

    public void syncAndDelete() throws IOException{
        listServer.clear();
        listClient.clear();
        List <DateAndName> listServer = listFiles(path, path);

        try {
            List<DateAndName> listClient = receiveFilesList();

            sendMessage(listServer);

            resetConnection();

            for (DateAndName fileClient : listClient){
                if (fileClient.getType().equals("File")){
                    DateAndName fileServer = listServer.stream().filter(o -> o.getName().equals(fileClient.getName()) && o.getType().equals("File")).findFirst().orElse(null);
                    if (fileServer != null){
                        System.out.println("File " + fileClient.getName() + " is on the server and client.");
                        if (fileServer.getDate() > fileClient.getDate()) {
                            System.out.println("File " + fileServer.getName() + " has been modified on the server.");
                            System.out.println("I will send it to the client.");
                            // sendFile(fileServer);
                        }
                    } else {
                        System.out.println("File " + fileClient.getName() + " is on the client but not on the server.");
                        if (lasteState.contains(fileClient)){
                            System.out.println("File " + fileClient.getName() + " has been deleted on the server.");
                        } else {
                            System.out.println("File " + fileClient.getName() + " has been added on the client.");
                            System.out.println("I will receive the file.");
                            // receiveFile(fileClient);
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
                            System.out.println("I will receive the file.");
                            // receiveFile(fileClient);
                        }
                    } else {
                        System.out.println("File " + fileServer.getName() + " is on the server but not on the client.");
                        if (lasteState.contains(fileServer)){
                            System.out.println("File " + fileServer.getName() + " has been deleted on the client.");
                            System.out.println("I will delete it mine.");
                            // deleteFile(fileServer);
                        } else {
                            System.out.println("File " + fileServer.getName() + " has been added on the server.");
                            System.out.println("I will send it to the client.");
                            // sendFile(fileServer);
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
            //                     System.out.println("I will send it to the client.");
            //                     // sendFile(fileServer);
            //                 }
            //             }
            //         }
            //         if (!contains) {
            //             if (lasteState.contains(fileServer)){
            //                 System.out.println("File " + fileServer.getName() + " has been deleted on the server.");
            //             } else {
            //                 System.out.println("File " + fileServer.getName() + " has been added on the server.");
            //                 System.out.println("I will send it to the client.");
            //                 // sendFile(fileServer);
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
            //                     System.out.println("I will receive the file.");
            //                     // receiveFile(fileClient);
            //                 }
            //             }
            //         }
            //         if (!contains) {
            //             if (lasteState.contains(fileClient)){
            //                 System.out.println("File " + fileClient.getName() + " has been deleted on the client.");
            //                 System.out.println("I will delete it on the server.");
            //                 // deleteFile(fileClient);
            //             } else {
            //                 System.out.println("File " + fileClient.getName() + " has been added on the client.");
            //                 System.out.println("I will receive the file.");
            //                 // receiveFile(fileClient);
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
        Server server = new Server(117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test");

        server.connect();
        server.firstSync();
        // server.syncAndDelete();
        server.close();
    }
}
