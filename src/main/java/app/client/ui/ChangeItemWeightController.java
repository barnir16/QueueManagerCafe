package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class ChangeItemWeightController {

    @FXML private ComboBox<String> itemCombo;
    @FXML private TextField weightField;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;

    // REPLACED: private MainCafeController mainController;
    private ClientUIController mainController;

    public void setMainController(ClientUIController mc) {
        this.mainController = mc;
    }

    public void initItems(List<String> items) {
        itemCombo.getItems().setAll(items);

        itemCombo.setOnAction(e -> {
            String sel = itemCombo.getValue();
            if (sel != null && mainController != null) {
                int w = mainController.getWeightForItem(sel);
                weightField.setText(String.valueOf(w));
            }
        });
    }

    @FXML
    private void handleApply() {
        String sel = itemCombo.getValue();
        if (sel == null || sel.isEmpty()) {
            System.out.println("No item selected");
            return;
        }
        try {
            int newW = Integer.parseInt(weightField.getText().trim());
            if (newW <= 0) {
                System.out.println("Weight must be > 0");
                return;
            }
            if (mainController != null) {
                mainController.setWeightForItem(sel, newW);
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
        Stage st = (Stage) itemCombo.getScene().getWindow();
        st.close();
    }
}
