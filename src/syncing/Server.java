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
        List <DateAndName> listServer = listFiles(path, path);

        try{
            List<DateAndName> listClient = receiveFilesList();

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
