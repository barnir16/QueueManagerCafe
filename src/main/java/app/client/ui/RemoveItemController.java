package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class RemoveItemController {

    @FXML private ComboBox<String> itemComboBox;

    // No Button fields needed if we only have onAction in FXML
    private ClientUIController mainController;

    public void setMainController(ClientUIController mainController) {
        this.mainController = mainController;
    }

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
