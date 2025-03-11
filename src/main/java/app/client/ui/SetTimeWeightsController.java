package app.client.ui;

import algorithm.TimeWeightedAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for SetTimeWeights.fxml
 * Lets the user see/edit the 3 "weights" used by TimeWeightedAlgorithm
 * for low/mid/high priority levels.
 */
public class SetTimeWeightsController {

    @FXML private TextField lowField;
    @FXML private TextField midField;
    @FXML private TextField highField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    // REPLACED: private MainCafeController mainController;
    private ClientUIController mainController;

    public void setMainController(ClientUIController mc) {
        this.mainController = mc;
    }

    /**
     * Called by ClientUIController to initialize the text fields
     * with the current weights array: [low, mid, high].
     */
    public void initWeights(int[] weights) {
        lowField.setText(String.valueOf(weights[0]));
        midField.setText(String.valueOf(weights[1]));
        highField.setText(String.valueOf(weights[2]));
    }

    @FXML
    private void handleOk() {
        if (mainController == null) {
            close();
            return;
        }
        try {
            int low  = Integer.parseInt(lowField.getText().trim());
            int mid  = Integer.parseInt(midField.getText().trim());
            int high = Integer.parseInt(highField.getText().trim());

            // 1) Store them in the controller's "storedTimeWeights"
            mainController.setStoredTimeWeights(low, mid, high);

            // 2) If current algorithm is TWA, apply them
            if (mainController.getCurrentAlgorithm() instanceof TimeWeightedAlgorithm twa) {
                twa.setWeights(low, mid, high);
                System.out.println("Updated TWA weights to: [" + low + ", " + mid + ", " + high + "]");
            } else {
                System.out.println("No TWA active, stored weights for later use.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid time weights: " + e.getMessage());
        }
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage s = (Stage) lowField.getScene().getWindow();
        s.close();
    }
}
