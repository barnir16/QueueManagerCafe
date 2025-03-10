package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for SetTimeValues.fxml
 * Minimal placeholder: user enters some "time" value,
 * we can store it or just print "not implemented" if you prefer.
 */
public class SetTimeValuesController {

    @FXML private TextField timeField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private MainCafeController mainController;

    public void setMainController(MainCafeController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        // If you want to display an existing time value, do so here.
    }

    @FXML
    private void handleSave() {
        String val = timeField.getText().trim();
        // For now, just print or show an alert:
        System.out.println("SetTimeValues: user typed: " + val);
        // You could call mainController.setTimeValue(val) if you had such a method
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) timeField.getScene().getWindow();
        stage.close();
    }
}
