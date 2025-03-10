package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller for RemoveItem.fxml
 * Shows a ComboBox of existing items, user clicks "Remove" -> calls mainController.removeItemFromMenu(...)
 */
public class RemoveItemController {

    @FXML private ComboBox<String> itemComboBox;
    @FXML private Button removeButton;
    @FXML private Button cancelButton;

    private MainCafeController mainController;

    public void setMainController(MainCafeController mainController) {
        this.mainController = mainController;
    }

    /**
     * Called by MainCafeController to populate the comboBox with current items.
     */
    public void initComboBox(List<String> items) {
        itemComboBox.getItems().setAll(items);
    }

    @FXML
    private void handleRemove() {
        String selected = itemComboBox.getValue();
        if (selected != null && mainController != null) {
            mainController.removeItemFromMenu(selected);
        }
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) itemComboBox.getScene().getWindow();
        stage.close();
    }
}
