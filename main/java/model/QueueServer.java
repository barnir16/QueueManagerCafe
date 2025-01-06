package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class QueueServer {
    public static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());

                try (ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                     ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream())) {

                    Object received = input.readObject();
                    System.out.println("Received from client: " + received);

                    String response = "Order received successfully.";
                    output.writeObject(response);
                    output.flush();
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to read data from client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
