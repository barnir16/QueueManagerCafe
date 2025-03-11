package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * Lets the user pick one of four algorithms:
 * Time Based, Batch Item, Member Priority, or Item Weight.
 */
public class SelectAlgController {

    @FXML private RadioButton timeBasedRadio;
    @FXML private RadioButton batchRadio;
    @FXML private RadioButton memberPriorityRadio;
    @FXML private RadioButton itemWeightRadio;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    // REPLACED: private MainCafeController mainController;
    private ClientUIController mainController;

    public void setMainController(ClientUIController mc) {
        this.mainController = mc;
    }

    @FXML
    private void initialize() {
        // Ensure only one radio is selected
        ToggleGroup group = new ToggleGroup();
        timeBasedRadio.setToggleGroup(group);
        batchRadio.setToggleGroup(group);
        memberPriorityRadio.setToggleGroup(group);
        itemWeightRadio.setToggleGroup(group);

        // Optionally set a default
        timeBasedRadio.setSelected(true);
    }

    @FXML
    private void handleOk() {
        String chosenAlg = "Time Based"; // default

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
        Stage st = (Stage) timeBasedRadio.getScene().getWindow();
        st.close();
    }
}
