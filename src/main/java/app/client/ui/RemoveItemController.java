package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class RemoveItemController {

    @FXML private ComboBox<String> itemComboBox;
    // maybe a remove button, cancel button

    @FXML
    private void initialize() {
        // Populate comboBox if desired
        itemComboBox.getItems().addAll("Cappuccino", "Espresso", "Sandwich");
    }

    @FXML
    private void handleRemove(ActionEvent event) {
        String selectedItem = itemComboBox.getValue();
        System.out.println("Removing item: " + selectedItem);
        // do logic...
        closeWindow(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
