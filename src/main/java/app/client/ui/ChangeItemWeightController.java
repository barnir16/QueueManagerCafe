package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller for ChangeItemWeight.fxml
 * Shows a combo of existing items, user enters new weight, calls mainController.setWeightForItem(...)
 */
public class ChangeItemWeightController {

    @FXML private ComboBox<String> itemCombo;
    @FXML private TextField weightField;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;

    private MainCafeController mainController;

    public void setMainController(MainCafeController mainController) {
        this.mainController = mainController;
    }

    /**
     * Called by MainCafeController to populate the combo with current items.
     */
    public void initItems(List<String> items) {
        itemCombo.getItems().setAll(items);
    }

    @FXML
    private void handleApply() {
        String selectedItem = itemCombo.getValue();
        if (selectedItem == null || selectedItem.isEmpty()) {
            System.out.println("No item selected");
            return;
        }
        try {
            int newWeight = Integer.parseInt(weightField.getText());
            if (newWeight <= 0) {
                System.out.println("Weight must be > 0");
                return;
            }
            if (mainController != null) {
                mainController.setWeightForItem(selectedItem, newWeight);
            }
            close();
        } catch (NumberFormatException ex) {
            System.out.println("Invalid weight");
        }
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) itemCombo.getScene().getWindow();
        stage.close();
    }
}
