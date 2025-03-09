package app.client.ui;

import app.MainApp;                 // <--- so we can call MainApp.showMainCafe()
import app.client.Client;
import app.server.service.Request;
import app.server.service.Response;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label responseLabel;

    private final Gson gson = new Gson();

    @FXML
    private void handleLogin() {
        // Prepare JSON request for "LOGIN"
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "LOGIN");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        // Convert to JSON
        String jsonRequest = gson.toJson(new Request(headers, body));

        // Send to server
        String serverResponseJson = Client.sendJsonRequest(jsonRequest);
        System.out.println("Server response: " + serverResponseJson);

        // If the server fails or returns something like "ERROR: ..."
        if (serverResponseJson.startsWith("ERROR:")) {
            responseLabel.setText("Login failed: " + serverResponseJson);
            return;
        }

        // Otherwise, parse the JSON into a Response
        Response resp = gson.fromJson(serverResponseJson, Response.class);

        // Suppose your server sets resp.getMessage() == "SUCCESS" for a correct login
        if ("SUCCESS".equals(resp.getMessage())) {
            responseLabel.setText("Login successful!");

            // Close the login window
            Stage loginStage = (Stage) responseLabel.getScene().getWindow();
            loginStage.close();

            // Switch to MainCafe
            MainApp.showMainCafe();

        } else {
            responseLabel.setText("Login failed: " + resp.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        // Prepare JSON request for "REGISTER"
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "REGISTER");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        String jsonRequest = gson.toJson(new Request(headers, body));
        String serverResponseJson = Client.sendJsonRequest(jsonRequest);
        System.out.println("Server response: " + serverResponseJson);

        if (serverResponseJson.startsWith("ERROR:")) {
            responseLabel.setText("Registration failed: " + serverResponseJson);
            return;
        }

        // Parse
        Response resp = gson.fromJson(serverResponseJson, Response.class);
        if ("REGISTERED".equals(resp.getMessage())) {
            responseLabel.setText("Registration successful!");
        } else {
            responseLabel.setText("Registration failed: " + resp.getMessage());
        }
    }
}
