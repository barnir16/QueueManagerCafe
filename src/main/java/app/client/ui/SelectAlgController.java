package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * Lets the user pick one of four algorithms: Time Based, Batch Item,
 * Member Priority, or Item Weight.
 */
public class SelectAlgController {

    @FXML private RadioButton timeBasedRadio;
    @FXML private RadioButton batchRadio;
    @FXML private RadioButton memberPriorityRadio;
    @FXML private RadioButton itemWeightRadio;

    // We don't need private Button fields if we only do onAction in FXML
    private ClientUIController mainController;

    @FXML
    public void initialize() {
        // Force single selection
        ToggleGroup group = new ToggleGroup();
        timeBasedRadio.setToggleGroup(group);
        batchRadio.setToggleGroup(group);
        memberPriorityRadio.setToggleGroup(group);
        itemWeightRadio.setToggleGroup(group);

        timeBasedRadio.setSelected(true);
    }

    public void setMainController(ClientUIController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleOk() {
        String chosenAlg = "Time Based";
        if (timeBasedRadio.isSelected()) {
            chosenAlg = "Time Based";
        } else if (batchRadio.isSelected()) {
            chosenAlg = "Batch Item";
        } else if (memberPriorityRadio.isSelected()) {
            chosenAlg = "Member Priority";
        } else if (itemWeightRadio.isSelected()) {
            chosenAlg = "Item Weight";
        }

        if (mainController != null) {
            mainController.updateAlgorithm(chosenAlg);
        }
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) timeBasedRadio.getScene().getWindow();
        stage.close();
    }
}
