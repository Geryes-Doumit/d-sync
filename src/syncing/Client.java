package src.syncing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Network {
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        isServer = false;

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

    public void close() {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client server = new Client("192.168.1.55", 117);
        System.out.println(server.connect);
        server.close();
    }
}
