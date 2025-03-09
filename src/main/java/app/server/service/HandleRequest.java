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
 * Handles client requests using JSON over a Socket connection.
 */
public class HandleRequest implements Runnable {
    private static final Logger logger = Logger.getLogger(HandleRequest.class.getName());
    private final Socket clientSocket;
    private final MainCafeService service;  // For "saveUser"/"findUser" actions
    private final Gson gson = new Gson();

    // Default: uses MainCafeService() => "users.txt" for "saveUser"/"findUser"
    public HandleRequest(Socket clientSocket) {
        this(clientSocket, new MainCafeService());
    }

    // For testing or custom injection
    public HandleRequest(Socket clientSocket, MainCafeService injectedService) {
        this.clientSocket = clientSocket;
        this.service = injectedService;
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

    public Response processRequest(Request request) {
        if (request == null || request.getHeaders() == null) {
            return new Response("ERROR: Invalid JSON format");
        }

        String action = request.getHeaders().get("action");
        if (action == null) {
            return new Response("ERROR: No action specified");
        }

        switch (action) {
            // ----------------------------------------------------
            // 1) LOGIN / REGISTER (using a dedicated UserService)
            // ----------------------------------------------------
            case "LOGIN":
                return handleLogin(request);
            case "REGISTER":
                return handleRegister(request);

            // ----------------------------------------------------
            // 2) Save / Find user (using MainCafeService example)
            // ----------------------------------------------------
            case "saveUser":
                return handleSaveUser(request);
            case "findUser":
                return handleFindUser(request);

            default:
                return new Response("ERROR: Unknown action");
        }
    }

    private Response handleLogin(Request request) {
        Map<String, String> body = request.getBody();
        if (body == null) {
            return new Response("FAILED: Body is null");
        }
        String username = body.get("username");
        String password = body.get("password");

        // Use a dedicated service for user auth
        UserService userService = new UserService(); // "users.txt"
        boolean authenticated = userService.authenticate(username, password);
        return authenticated ? new Response("SUCCESS") : new Response("FAILED");
    }

    private Response handleRegister(Request request) {
        Map<String, String> body = request.getBody();
        if (body == null) {
            return new Response("ERROR: Body is null");
        }
        String username = body.get("username");
        String password = body.get("password");

        // If either field is empty, let's fail
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return new Response("ERROR: Username/password cannot be empty");
        }

        UserService userService = new UserService();
        boolean registered = userService.register(username, password);
        if (registered) {
            return new Response("REGISTERED");
        } else {
            return new Response("ERROR: Duplicate user or invalid");
        }
    }

    private Response handleSaveUser(Request request) {
        Map<String, String> body = request.getBody();
        if (body == null) {
            return new Response("ERROR: Body is null");
        }
        String username = body.get("username");
        String password = body.get("password");

        boolean success = service.saveUser(username, password);
        return new Response(success ? "SAVED" : "ERROR: Username already exists");
    }

    private Response handleFindUser(Request request) {
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
