package app.client.ui;

import algorithm.TimeWeightedAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for SetTimeValues.fxml
 * Allows the user to see/edit the "low" and "mid" thresholds
 * used by TimeWeightedAlgorithm (or subclass).
 */
public class SetTimeValuesController {

    @FXML private TextField timeLowField;
    @FXML private TextField timeMidField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // REPLACED: private MainCafeController mainController;
    private ClientUIController mainController;

    public void setMainController(ClientUIController mc) {
        this.mainController = mc;
    }

    /**
     * Called by ClientUIController to initialize the text fields
     * with the current thresholds (e.g. [5, 10]).
     */
    public void initValues(int[] thresholds) {
        timeLowField.setText(String.valueOf(thresholds[0]));
        timeMidField.setText(String.valueOf(thresholds[1]));
    }

    @FXML
    private void handleSave() {
        if (mainController == null) {
            close();
            return;
        }
        try {
            int low = Integer.parseInt(timeLowField.getText().trim());
            int mid = Integer.parseInt(timeMidField.getText().trim());

            // 1) Store them in the controller's "storedTimeThresholds"
            mainController.setStoredTimeThresholds(low, mid);

            // 2) If current algorithm is TWA, apply them
            if (mainController.getCurrentAlgorithm() instanceof TimeWeightedAlgorithm twa) {
                twa.setTimeThresholds(low, mid);
                System.out.println("Updated TWA thresholds to: low=" + low + ", mid=" + mid);
            } else {
                System.out.println("No TWA active, stored thresholds for later use.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid time thresholds: " + e.getMessage());
        }
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage st = (Stage) timeLowField.getScene().getWindow();
        st.close();
    }
}
