package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for SetTimeWeights.fxml
 * Minimal placeholder for "time weights" (whatever your logic is).
 */
public class SetTimeWeightsController {

    @FXML private TextField lowField;
    @FXML private TextField midField;
    @FXML private TextField highField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private MainCafeController mainController;

    public void setMainController(MainCafeController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleOk() {
        String lowVal = lowField.getText().trim();
        String midVal = midField.getText().trim();
        String highVal = highField.getText().trim();
        System.out.println("SetTimeWeights: low=" + lowVal + ", mid=" + midVal + ", high=" + highVal);
        // You could call mainController.setTimeWeights(...) if you had such a method
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) lowField.getScene().getWindow();
        stage.close();
    }
}
