package app.client.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientUIController {

    // If you still want these from your "previousClientUIController", keep them:
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label responseLabel;

    // Called when user clicks "Add New Item"
    @FXML
    private void handleOpenAddItem() {
        openNewWindow("/app/client/ui/AddNewItem.fxml", "Add New Item");
    }

    // Called when user clicks "Remove Item"
    @FXML
    private void handleRemoveItem() {
        openNewWindow("/app/client/ui/RemoveItem.fxml", "Remove Item");
    }

    // Called when user clicks "Change Item Weight"
    @FXML
    private void handleOpenChangeWeight() {
        openNewWindow("/app/client/ui/ChangeItemWeight.fxml", "Change Item Weight");
    }

    /**
     * Utility method to open a new Stage with the given FXML.
     */
    private void openNewWindow(String resourcePath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(resourcePath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            System.err.println("Failed to load FXML: " + resourcePath + " - " + ex.getMessage());
        }
    }

    // (Optional) If you want to handle "handleSaveUser" or "handleFindUser" from your old code:
    @FXML
    private void handleSaveUser() {
        // your logic (like sending JSON request)
        System.out.println("Saving user with username=" + (usernameField != null ? usernameField.getText() : "??"));
    }

    @FXML
    private void handleFindUser() {
        // your logic (like sending JSON request)
        System.out.println("Finding user with username=" + (usernameField != null ? usernameField.getText() : "??"));
    }
}
