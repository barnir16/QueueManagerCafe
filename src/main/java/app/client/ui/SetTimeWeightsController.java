package app.client.ui;

import algorithm.TimeWeightedAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SetTimeWeightsController {

    @FXML private TextField lowField;
    @FXML private TextField midField;
    @FXML private TextField highField;

    private ClientUIController mainController;

    public void setMainController(ClientUIController mainController) {
        this.mainController = mainController;
    }

    public void initWeights(int[] weights) {
        // weights[0] = low, weights[1] = mid, weights[2] = high
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
        if (mainController.getCurrentAlgorithm() instanceof TimeWeightedAlgorithm twa) {
            try {
                int low = Integer.parseInt(lowField.getText().trim());
                int mid = Integer.parseInt(midField.getText().trim());
                int high = Integer.parseInt(highField.getText().trim());
                twa.setWeights(low, mid, high);
                System.out.println("Updated time weights: [" + low + ", " + mid + ", " + high + "]");
            } catch (Exception e) {
                System.err.println("Invalid time weights: " + e.getMessage());
            }
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
