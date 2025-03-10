package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

/**
 * Controller for SelectAlg.fxml
 * Minimal placeholder for selecting an algorithm.
 * We call mainController.updateAlgorithm(...) to change the label.
 */
public class SelectAlgController {

    @FXML private RadioButton timeBasedRadio;
    @FXML private RadioButton batchRadio;
    @FXML private RadioButton memberPriorityRadio;
    @FXML private RadioButton itemWeightRadio;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private MainCafeController mainController;

    public void setMainController(MainCafeController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleOk() {
        // Check which radio is selected and update the label in MainCafeController
        if (timeBasedRadio.isSelected()) {
            mainController.updateAlgorithm("Time Based");
        } else if (batchRadio.isSelected()) {
            mainController.updateAlgorithm("Batch Item");
        } else if (memberPriorityRadio.isSelected()) {
            mainController.updateAlgorithm("Member Priority");
        } else if (itemWeightRadio.isSelected()) {
            mainController.updateAlgorithm("Item Weight");
        }
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) timeBasedRadio.getScene().getWindow();
        stage.close();
    }
}
