package app.client.ui;

import algorithm.TimeWeightedAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Allows user to see/edit "low" and "mid" thresholds in TimeWeightedAlgorithm.
 */
public class SetTimeValuesController {

    @FXML private TextField timeLowField;
    @FXML private TextField timeMidField;

    // No Button fields needed if we just have onAction in FXML
    private ClientUIController mainController;

    public void setMainController(ClientUIController mainController) {
        this.mainController = mainController;
    }

    public void initValues(int[] thresholds) {
        // thresholds[0] = low, thresholds[1] = mid
        timeLowField.setText(String.valueOf(thresholds[0]));
        timeMidField.setText(String.valueOf(thresholds[1]));
    }

    @FXML
    private void handleSave() {
        if (mainController == null) {
            close();
            return;
        }
        if (mainController.getCurrentAlgorithm() instanceof TimeWeightedAlgorithm twa) {
            try {
                int low = Integer.parseInt(timeLowField.getText().trim());
                int mid = Integer.parseInt(timeMidField.getText().trim());
                twa.setTimeThresholds(low, mid);
                System.out.println("Updated time thresholds: low=" + low + ", mid=" + mid);
            } catch (Exception e) {
                System.err.println("Invalid time thresholds: " + e.getMessage());
            }
        }
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) timeLowField.getScene().getWindow();
        stage.close();
    }
}
