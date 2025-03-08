package app.client;

import java.io.*;
import java.net.Socket;

/**
 * Simple client utility for sending JSON requests to the server.
 */
public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 34567; // Match ServerDriver's port

    public static String sendJsonRequest(String jsonRequest) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(jsonRequest);
            return in.readLine();

        } catch (IOException e) {
            return "ERROR: Unable to connect to server on port " + SERVER_PORT;
        }
    }
}
