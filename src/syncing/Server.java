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
        listServer = listFiles(path, path);

        try{
            listClient = receiveFilesList();

            sendMessage(listServer);

            resetConnection();

            for (DateAndName fileServer : listServer) {
                Boolean contains = false;

                for (DateAndName fileClient : listClient){
                    if (fileServer.getName().equals(fileClient.getName()) && fileClient.getType().equals("File")) {
                        contains = true;
                        System.out.println(fileServer.getType()+" " + fileServer.getPath() + " is on the server and client.");
                        if (fileServer.getDate() > fileClient.getDate()) {
                            System.out.println("File " + fileServer.getPath() + " has been modified on the server.");
                            System.out.println("I will send it to the client.");
                            sendFile(fileServer);
                        }
                    }
                }
                if (!contains) {
                    System.out.println(fileServer.getType()+" " + fileServer.getPath() + " is on the server but not on the client.");
                    if (fileServer.getType().equals("File")) {
                        System.out.println("I will send it to the client.");
                        sendFile(fileServer);
                    }
                }
            }

            for (DateAndName fileClient : listClient) {
                Boolean contains = false;

                for (DateAndName fileServer : listServer){
                    if (fileServer.getName().equals(fileClient.getName()) && fileServer.getType().equals("File")) {
                        contains = true;
                        System.out.println(fileClient.getType()+" " + fileClient.getPath() + " is on the server and client.");
                        if (fileClient.getDate() > fileServer.getDate()) {
                            System.out.println("File " + fileClient.getPath() + " has been modified on the client.");
                            System.out.println("I will receive the file.");
                            receiveFile(fileClient);
                        }
                    }
                }
                if (!contains) {
                    System.out.println(fileClient.getType()+" "  + fileClient.getPath() + " is on the client but not on the server.");
                    if (fileClient.getType().equals("File")) {
                        System.out.println("I will receive the file.");
                        receiveFile(fileClient);
                    }
                    else {
                        System.out.println("I will create the folder.");
                        File folder = new File(path + "/" + fileClient.getPath());
                        folder.mkdirs();
                    }
                }
            }
            lastState = listFiles(path, path);
            System.out.println("Done.");
            firstSync = false;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }

    public void syncAndDelete() throws IOException{
        isChange = false;
        List <DateAndName> listServer = listFiles(path, path);

        try {
            List<DateAndName> listClient = receiveFilesList();

            sendMessage(listServer);

            resetConnection();

            for (DateAndName fileClient : listClient){
                DateAndName fileServer = listServer.stream().filter(o -> o.getPath().equals(fileClient.getPath()) && o.getType().equals(o.getType())).findFirst().orElse(null);
                if (fileServer != null){
                    // System.out.println(fileClient.getType()+ " " + fileClient.getPath() + " is on the server and client.");
                    if (fileServer.getDate() > fileClient.getDate() && fileClient.getType().equals("File")) {
                        // Changer la conditions si file pour prendre en compte le changement de métadonnées du fichier
                        // System.out.println(fileClient.getType()+ " " + fileServer.getPath() + " has been modified on the server.");
                        // System.out.println("I will send it to the client.");
                        sendFile(fileServer);
                        isChange = true;
                    }
                } else {
                    isChange = true;
                    // System.out.println(fileClient.getType()+ " " + fileClient.getPath() + " is on the client but not on the server.");
                    if (lastState.stream().anyMatch(o -> o.getType().equals(fileClient.getType()) && o.getPath().equals(fileClient.getPath()))){
                        // System.out.println(fileClient.getType()+ " " + fileClient.getPath() + " has been deleted on the server.");
                    } else {
                        // System.out.println(fileClient.getType()+ " " + fileClient.getPath() + " has been added on the client.");
                        if (fileClient.getType().equals("File")) {
                            // System.out.println("I will receive it.");
                            receiveFile(fileClient);
                        }
                        else{
                            // System.out.println("I will create the folder.");
                            // File folder = new File(path + "/" + fileClient.getPath());
                            // folder.mkdirs();
                            createDirectory(fileClient);
                        }
                    }
                }
            }

            for (DateAndName fileServer : listServer){
                DateAndName fileClient = listClient.stream().filter(o -> o.getPath().equals(fileServer.getPath()) && o.getType().equals(fileServer.getType())).findFirst().orElse(null);
                if (fileClient != null){
                    // System.out.println(fileServer.getType()+ " " + fileServer.getPath() + " is on the server and client.");
                    if (fileClient.getDate() > fileServer.getDate() && fileClient.getType().equals("File")) {
                        // Changer la conditions si file pour prendre en compte le changement de métadonnées du fichier
                        // System.out.println(fileServer.getType()+ " " + fileServer.getPath() + " has been modified on the client.");
                        // System.out.println("I will receive it.");
                        receiveFile(fileClient);
                        isChange = true;
                    }
                } else {
                    isChange = true;
                    // System.out.println(fileServer.getType()+ " " + fileServer.getPath() + " is on the server but not on the client.");
                    if (lastState.stream().anyMatch(o -> o.getType().equals(fileServer.getType()) && o.getPath().equals(fileServer.getPath()))){
                        // System.out.println(fileServer.getType()+ " " + fileServer.getPath() + " has been deleted on the client.");
                        // System.out.println("I will delete it mine.");
                        deleteFile(fileServer);
                    } else {
                        // System.out.println(fileServer.getType()+ " " + fileServer.getPath() + " has been added on the server.");
                        if (fileServer.getType().equals("File")) {
                            System.out.println("I will send it.");
                            sendFile(fileServer);
                        }
                    }
                }
            }

            lastState.clear();
            lastState = listFiles(path, path);
            if (isChange) {
                System.out.println("Done, with changes.");
            }
            else {
                System.out.println("No change.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error receiving files list: " + e.getMessage());
        }
    }


    // public static void main(String[] args) throws Exception{
    //     Server server = new Server(117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test_DSync/Server");
    //     Client client = new Client("192.168.1.55", 117, "/Users/marc/Library/CloudStorage/OneDrive-uha.fr/Cours/GitHub/Test_DSync/Client");
    //     server.sync = true;
    //     client.sync = true;

    //     server.start();
    //     client.start();
    // }
}
