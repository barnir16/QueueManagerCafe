package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewItemController {
    @FXML private TextField itemNameField;
    @FXML private TextField itemWeightField;

    @FXML
    private void handleAdd() {
        String name = itemNameField.getText().trim();
        String weightStr = itemWeightField.getText().trim();
        // do something: add item to a shared data structure, etc.
        // Then close:
        Stage stage = (Stage) itemNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) itemNameField.getScene().getWindow();
        stage.close();
    }
}
