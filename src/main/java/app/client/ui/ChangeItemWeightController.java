package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ChangeItemWeightController {

    @FXML private MenuButton itemMenu;
    @FXML private TextField weightField;

    @FXML
    private void initialize() {
        // Example: populate the menu items
        MenuItem item1 = new MenuItem("Action 1");
        item1.setOnAction(e -> itemMenu.setText("Action 1"));
        MenuItem item2 = new MenuItem("Action 2");
        item2.setOnAction(e -> itemMenu.setText("Action 2"));

        itemMenu.getItems().addAll(item1, item2);
    }

    @FXML
    private void handleApply(ActionEvent event) {
        String selectedItem = itemMenu.getText();
        String newWeight = weightField.getText();
        System.out.println("Change weight of " + selectedItem + " to " + newWeight);

        // your logic
        closeWindow(event);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
