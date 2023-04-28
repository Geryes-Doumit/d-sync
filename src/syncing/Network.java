package src.syncing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Network {
    protected Socket socket;
    protected String ip;
    protected int port;
    protected ServerSocket serverSocket;
    protected PrintWriter out;
    protected BufferedReader in;
    protected Boolean connect;
    protected Boolean isServer;

    // // Constructeur Serveur
    // public Network(int port) {
    //     this.port = port;
    //     isServer = true;

    //     try {
    //         serverSocket = new ServerSocket(port);
    //         System.out.println("Server started. Waiting for connection...");
    //         serverSocket.setSoTimeout(30000);
    //         socket = serverSocket.accept(); // attendra au maximum 30 secondes
    //         System.out.println("Client connected.");

    //         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    //         out = new PrintWriter(socket.getOutputStream(), true);
    //         connect = true;

    //     } catch (IOException e) {
    //         System.err.println("Error setting up server: " + e.getMessage());
    //         connect = false;
    //     }
    // }

    // // Constructeur Client
    // public Network(String ip, int port) {
    //     this.ip = ip;
    //     this.port = port;
    //     isServer = false;

    //     try{
    //         socket = new Socket(ip, port);
    //         System.out.println("Connected to server.");

    //         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    //         out = new PrintWriter(socket.getOutputStream(), true);
    //         connect = true;

    //     } catch (IOException e) {
    //         System.err.println("Error setting up client: " + e.getMessage());
    //         connect = false;
    //     }
    // }

    // Getter
    public Boolean getConnect() {
        return connect;
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public abstract void close();

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