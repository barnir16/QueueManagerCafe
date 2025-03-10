package app.client.ui;

import app.MainApp;
import app.client.Client;
import app.server.service.Request;
import app.server.service.Response;
import com.google.gson.Gson;
import javafx.fxml.FXML;
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
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "LOGIN");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        String json = gson.toJson(new Request(headers, body));
        String serverResponse = Client.sendJsonRequest(json);
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.startsWith("ERROR:")) {
            responseLabel.setText("Login failed: " + serverResponse);
            return;
        }

        Response resp = gson.fromJson(serverResponse, Response.class);
        if ("SUCCESS".equals(resp.getMessage())) {
            responseLabel.setText("Login successful!");
            Stage stage = (Stage) responseLabel.getScene().getWindow();
            stage.close();

            MainApp.showMainCafe();
        } else {
            responseLabel.setText("Login failed: " + resp.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "REGISTER");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        String json = gson.toJson(new Request(headers, body));
        String serverResponse = Client.sendJsonRequest(json);
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.startsWith("ERROR:")) {
            responseLabel.setText("Registration failed: " + serverResponse);
            return;
        }

        Response resp = gson.fromJson(serverResponse, Response.class);
        if ("REGISTERED".equals(resp.getMessage())) {
            responseLabel.setText("Registration successful!");
        } else {
            responseLabel.setText("Registration failed: " + resp.getMessage());
        }
    }
}
