package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class RemoveItemController {

    @FXML private ComboBox<String> itemComboBox;
    @FXML private Button removeButton;
    @FXML private Button cancelButton;

    // REPLACED: private MainCafeController mainController;
    private ClientUIController mainController;

    public void setMainController(ClientUIController mc) {
        this.mainController = mc;
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
        Stage st = (Stage) itemComboBox.getScene().getWindow();
        st.close();
    }
}
