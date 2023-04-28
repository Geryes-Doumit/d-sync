package src.syncing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class Server extends Network{
    
    public Server(int port) {
        this.port = port;
        isServer = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for connection...");
            serverSocket.setSoTimeout(30000);
            socket = serverSocket.accept(); // attendra au maximum 30 secondes
            System.out.println("Client connected.");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
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

    public static void main(String[] args) {
        Server server = new Server(117);
        System.out.println(server.connect);
        server.close();
    }
}
