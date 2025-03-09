package app.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Simple client utility for sending JSON requests to the server on localhost:34567.
 *
 * Usage Example:
 *   String jsonRequest = "{\"headers\":{\"action\":\"LOGIN\"},\"body\":{\"username\":\"Bob\",\"password\":\"123\"}}";
 *   String response = Client.sendJsonRequest(jsonRequest);
 *   System.out.println("Server said: " + response);
 */
public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 34567; // Must match the server's port

    /**
     * Sends a single-line JSON request to the server, and reads one line of response.
     * If unable to connect, returns "ERROR: Unable to connect...".
     * If server's response is null, returns "ERROR: Null response from server".
     */
    public static String sendJsonRequest(String jsonRequest) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send JSON request as one line
            out.println(jsonRequest);

            // Read one line of response from the server
            String response = in.readLine();
            if (response == null) {
                return "ERROR: Null response from server";
            }
            return response;

        } catch (IOException e) {
            return "ERROR: Unable to connect to server on port " + SERVER_PORT;
        }
    }
}
