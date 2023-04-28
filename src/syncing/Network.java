package src.syncing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public abstract class Network {
    protected Socket socket;
    protected String ip;
    protected int port;
    protected ServerSocket serverSocket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected Boolean connect;
    protected Boolean isServer;
    protected String path;

    // Getter
    public Boolean getConnect() {
        return connect;
    }

    public List<File> receiveFilesList() throws IOException, ClassNotFoundException {
        return (List<File>) in.readObject();
    }

    public void sendMessage(List<File> files) throws IOException {
        out.writeObject(files);
        out.flush();
    }

    public abstract void close();
    public abstract void firstSync() throws IOException;


    // public static void main(String[] args){
    //     // Create server
    //     Network network = new Network(117);
    //     if(network.getConnect()){
    //         try {
    //             network.sendMessage("Hello World, I'm mac!");
    //             System.out.println(network.receiveMessage());
    //             network.close();
    //         } catch (IOException e) {
    //             System.err.println("Error: " + e.getMessage());
    //         }
    //     }

    //     // // Create client
    //     // Network network2 = new Network("192.168.1.55", 5000);
    //     // if(network2.getConnect()){
    //     //     try {
    //     //         System.out.println(network2.receiveMessage());
    //     //         network2.sendMessage("Hello World!");
    //     //         network2.close();
    //     //     } catch (IOException e) {
    //     //         System.err.println("Error: " + e.getMessage());
    //     //     }
    //     // }
    // }
}