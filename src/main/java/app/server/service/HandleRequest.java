package app.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles client requests using JSON.
 */
public class HandleRequest implements Runnable {
    private static final Logger logger = Logger.getLogger(HandleRequest.class.getName());
    private final Socket clientSocket;
    private final MainCafeService service; // Using MainCafeService now
    private final Gson gson = new Gson();

    public HandleRequest(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.service = new MainCafeService(); // Business logic
    }

    @Override
    public void run() {
        try (Scanner reader = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            while (reader.hasNextLine()) {
                String jsonRequest = reader.nextLine();
                logger.info("Received JSON request: " + jsonRequest);

                // Deserialize JSON into Request object
                Request request = gson.fromJson(jsonRequest, new TypeToken<Request>(){}.getType());

                // Process request
                Response response = processRequest(request);

                // Send response as JSON
                String jsonResponse = gson.toJson(response);
                writer.println(jsonResponse);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error handling client request", e);
        }
    }

    protected Response processRequest(Request request) {
        if (request == null || request.getHeaders() == null) {
            return new Response("ERROR: Invalid JSON format");
        }

        String action = request.getHeaders().get("action");
        if (action == null) {
            return new Response("ERROR: No action specified");
        }

        switch (action) {
            case "saveUser":
                return handleSave(request);
            case "findUser":
                return handleFind(request);
            default:
                return new Response("ERROR: Unknown action");
        }
    }

    private Response handleSave(Request request) {
        Map<String, String> body = request.getBody();
        if (body == null) {
            return new Response("ERROR: Body is null");
        }
        String username = body.get("username");
        String password = body.get("password");

        boolean success = service.saveUser(username, password);
        return new Response(success ? "SAVED" : "ERROR: Username already exists");
    }

    private Response handleFind(Request request) {
        Map<String, String> body = request.getBody();
        if (body == null) {
            return new Response("ERROR: Body is null");
        }
        String username = body.get("username");

        var user = service.findUser(username);
        if (user != null) {
            return new Response("FOUND: " + user.toString());
        } else {
            return new Response("NOT FOUND");
        }
    }
}
