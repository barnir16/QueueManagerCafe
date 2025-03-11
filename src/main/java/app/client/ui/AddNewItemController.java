package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewItemController {

    @FXML private TextField itemNameField;
    @FXML private TextField itemWeightField;

    // REPLACED: private MainCafeController mainController;
    private ClientUIController mainController;

    public void setMainController(ClientUIController mc) {
        this.mainController = mc;
    }

    @FXML
    private void handleAdd() {
        String name = itemNameField.getText().trim();
        String wStr = itemWeightField.getText().trim();
        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty");
            return;
        }
        try {
            int w = Integer.parseInt(wStr);
            if (w <= 0) {
                System.out.println("Weight must be > 0");
                return;
            }
            mainController.addItemToMenu(name, w);
            close();
        } catch (NumberFormatException ex) {
            System.out.println("Invalid weight, must be numeric");
        }
    }

    @FXML
    private void handleClose() {
        close();
    }

    private void close() {
        Stage st = (Stage) itemNameField.getScene().getWindow();
        st.close();
    }
}
