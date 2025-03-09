package app.client;

import app.Server.service.Request;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic JavaFX controller for the client UI to save/find users.
 */
public class ClientUIController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label responseLabel;

    private final Gson gson = new Gson();

    @FXML
    private void handleSaveUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "saveUser");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        Request request = new Request(headers, body);
        String json = gson.toJson(request);

        String serverResponse = Client.sendJsonRequest(json);
        responseLabel.setText(serverResponse);
    }

    @FXML
    private void handleFindUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "findUser");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());

        Request request = new Request(headers, body);
        String json = gson.toJson(request);

        String serverResponse = Client.sendJsonRequest(json);
        responseLabel.setText(serverResponse);
    }
}
