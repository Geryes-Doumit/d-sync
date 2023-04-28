package src.Syncing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Network {
    private Socket socket;
    private String ip;
    private int port;
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Boolean connect;

    // Constructeur Serveur
    public Network(int port) {
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for connection...");
            serverSocket.setSoTimeout(5000);
            socket = serverSocket.accept(); // attendra au maximum 5 secondes
            System.out.println("Client connected.");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connect = true;

        } catch (IOException e) {
            System.err.println("Error setting up server: " + e.getMessage());
            connect = false;
        }
    }

    // Constructeur Client
    public Network(String ip, int port) {
        this.ip = ip;
        this.port = port;

        try{
            socket = new Socket(ip, port);
            System.out.println("Connected to server.");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connect = true;

        } catch (IOException e) {
            System.err.println("Error setting up client: " + e.getMessage());
            connect = false;
        }
    }

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

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }

    public void main(String[] args){
        // Create server
        Network network = new Network(5000);
        if(network.getConnect()){
            try {
                network.sendMessage("Hello World!");
                System.out.println(network.receiveMessage());
                network.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        // Create client
        Network network2 = new Network("192.168.1.55", 5000);
        if(network2.getConnect()){
            try {
                System.out.println(network2.receiveMessage());
                network2.sendMessage("Hello World!");
                network2.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}