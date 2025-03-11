package app.client.ui;

import algorithm.*;
import model.Order;
import queue.IQueueAlgorithm;
import queue.OrderQueue;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A single merged controller that handles:
 * 1) Basic login fields (username/password).
 * 2) Main cafe logic: cart, items, queue, algorithm switching, etc.
 */
public class ClientUIController {

    // ----------------------------------------------------------------
    //  LOGIN-RELATED FIELDS (if you want to use them)
    // ----------------------------------------------------------------
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label responseLabel;

    // Example placeholders if you want "SaveUser"/"FindUser"
    @FXML
    private void handleSaveUser() {
        System.out.println("Saving user with username="
                + (usernameField != null ? usernameField.getText() : "??"));
    }

    @FXML
    private void handleFindUser() {
        System.out.println("Finding user with username="
                + (usernameField != null ? usernameField.getText() : "??"));
    }

    // ----------------------------------------------------------------
    //  MAIN CAFE LOGIC
    // ----------------------------------------------------------------
    @FXML private Label currentAlgorithmLabel;
    @FXML private VBox menuBox;
    @FXML private ListView<String> cartView;
    @FXML private ListView<String> queueView;
    @FXML private ListView<String> processedOrdersView;
    @FXML private CheckBox memberCheckBox; // "Member?" checkbox in FXML

    // Cart: itemName -> count
    private final Map<String, Integer> cartMap = new HashMap<>();

    // Lists for queue & processed orders in the UI
    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    private final ObservableList<String> processedList = FXCollections.observableArrayList();

    // Local map for item weights
    private final Map<String, Integer> itemWeights = new HashMap<>();

    // The JAR-based queue + current algorithm
    private OrderQueue cafeQueue;
    private IQueueAlgorithm currentAlg;

    private int nextOrderId = 1;
    private boolean isDarkMode = false;

    // ----------------------------------------------------------------
    //  FXML Initialization
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        // Default algorithm: TimeBased
        currentAlg = new TimeBasedAlgorithm();
        cafeQueue = new OrderQueue(currentAlg);
        if (currentAlgorithmLabel != null) {
            currentAlgorithmLabel.setText("Current Algorithm: Time Based");
        }

        // Bind queue & processed lists to the UI
        if (queueView != null) {
            queueView.setItems(queueList);
        }
        if (processedOrdersView != null) {
            processedOrdersView.setItems(processedList);
        }

        // Example default item weights
        itemWeights.put("Cappuccino", 2);
        itemWeights.put("Espresso",   1);
        itemWeights.put("Sandwich",   3);

        // Optional: run after UI loads
        Platform.runLater(() -> {
            // e.g. applyDarkMode(false);
        });
    }

    // ----------------------------------------------------------------
    //  ALGORITHM SWITCH
    // ----------------------------------------------------------------
    public void updateAlgorithm(String algName) {
        IQueueAlgorithm newAlg;
        switch (algName) {
            case "Batch Item":
                BatchItemAlgorithm bia = new BatchItemAlgorithm();
                // Example defaults
                bia.setWeights(3, 1);
                newAlg = bia;
                break;
            case "Member Priority":
                newAlg = new MemberPriorityAlgorithm();
                break;
            case "Item Weight":
                ItemWeightAlgorithm iwa = new ItemWeightAlgorithm();
                // Let the JAR see the same default item weights
                iwa.setItemWeight("Cappuccino", 2);
                iwa.setItemWeight("Espresso",   1);
                iwa.setItemWeight("Sandwich",   3);
                newAlg = iwa;
                break;
            case "Time Based":
            default:
                newAlg = new TimeBasedAlgorithm();
                break;
        }
        currentAlg = newAlg;
        cafeQueue.setAlgorithm(newAlg);

        if (currentAlgorithmLabel != null) {
            currentAlgorithmLabel.setText("Current Algorithm: " + algName);
        }
        System.out.println("Switched algorithm to: " + algName);

        refreshQueueView();
    }

    // ----------------------------------------------------------------
    //  CART PLUS / MINUS
    // ----------------------------------------------------------------
    @FXML
    private void handleItemPlus(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String itemName = (String) btn.getUserData();
        cartMap.put(itemName, cartMap.getOrDefault(itemName, 0) + 1);
        refreshCartView();
    }

    @FXML
    private void handleItemMinus(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String itemName = (String) btn.getUserData();
        int count = cartMap.getOrDefault(itemName, 0);
        if (count > 0) {
            count--;
            if (count == 0) {
                cartMap.remove(itemName);
            } else {
                cartMap.put(itemName, count);
            }
            refreshCartView();
        }
    }

    private void refreshCartView() {
        if (cartView == null) return;
        cartView.getItems().clear();
        cartMap.forEach((name, qty) ->
                cartView.getItems().add(name + " x" + qty)
        );
    }

    // ----------------------------------------------------------------
    //  ADD / REMOVE items from the left menu
    // ----------------------------------------------------------------
    public void addItemToMenu(String name, int weight) {
        if (itemWeights.containsKey(name)) {
            showInfo("Item already exists: " + name);
            return;
        }
        itemWeights.put(name, weight);

        // We replicate the same "label line + button line" pattern
        Label lbl = new Label(name);
        lbl.setStyle("-fx-text-fill: #ecf0f1;");

        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        plusBtn.setUserData(name);
        plusBtn.setOnAction(this::handleItemPlus);

        Button minusBtn = new Button("-");
        minusBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        minusBtn.setUserData(name);
        minusBtn.setOnAction(this::handleItemMinus);

        HBox buttonRow = new HBox(10, plusBtn, minusBtn);

        if (menuBox == null) {
            System.err.println("menuBox is null - can't add item UI");
            return;
        }
        int insertIndex = Math.max(0, menuBox.getChildren().size() - 3);
        // Add label
        menuBox.getChildren().add(insertIndex, lbl);
        // Add the HBox
        menuBox.getChildren().add(insertIndex + 1, buttonRow);

        System.out.println("Added item: " + name + " weight=" + weight);
    }

    public void removeItemFromMenu(String name) {
        itemWeights.remove(name);
        cartMap.remove(name);
        refreshCartView();

        if (menuBox == null) {
            System.err.println("menuBox is null - can't remove item UI");
            return;
        }
        var children = menuBox.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof Label lbl) {
                if (lbl.getText().equals(name)) {
                    // remove label
                    children.remove(i);
                    // remove next node if HBox
                    if (i < children.size() && (children.get(i) instanceof HBox)) {
                        children.remove(i);
                    }
                    System.out.println("Removed item from menu: " + name);
                    return;
                }
            }
        }
        System.out.println("Item '" + name + "' not found in the menuBox UI.");
    }

    // ----------------------------------------------------------------
    //  PLACE / PROCESS / CLEAR
    // ----------------------------------------------------------------
    @FXML
    private void handlePlaceOrder() {
        if (cartMap.isEmpty()) {
            showInfo("Cart is empty, cannot place an order!");
            return;
        }
        Map<String, Integer> itemsCopy = new HashMap<>(cartMap);

        boolean isMember = (memberCheckBox != null && memberCheckBox.isSelected());
        Order newOrder = new Order(nextOrderId++, itemsCopy, isMember);
        newOrder.setTimePlaced(LocalDateTime.now());

        cafeQueue.addOrder(newOrder);

        cartMap.clear();
        refreshCartView();
        refreshQueueView();
    }

    @FXML
    private void handleProcessOrder() {
        Order processed = cafeQueue.processNextOrder();
        if (processed != null) {
            processed.setTimeProcessed(LocalDateTime.now());
            processedList.add(processed.toString());
            refreshQueueView();
        } else {
            showInfo("Queue is empty!");
        }
    }

    @FXML
    private void handleClearProcessed() {
        processedList.clear();
    }

    private void refreshQueueView() {
        if (queueView == null) return;
        queueList.clear();
        for (Order o : cafeQueue.getQueue()) {
            queueList.add(o.toString());
        }
    }

    // ----------------------------------------------------------------
    //  BOTTOM ROW: THEME, TIME VALUES, TIME WEIGHTS, SELECT ALGORITHM
    // ----------------------------------------------------------------
    @FXML
    private void handleToggleTheme() {
        isDarkMode = !isDarkMode;
        applyDarkMode(isDarkMode);
    }

    @FXML
    private void handleTimeValues() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SetTimeValues.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            SetTimeValuesController ctrl = loader.getController();
            // Now references "ClientUIController" not "MainCafeController"
            ctrl.setMainController(this);

            if (currentAlg instanceof TimeWeightedAlgorithm twa) {
                ctrl.initValues(twa.getTimeThresholds());
            }

            Stage dialog = new Stage();
            dialog.setTitle("Set Time Values");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SetTimeValues.fxml\n" + e.getMessage());
        }
    }

    @FXML
    private void handleTimeWeights() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SetTimeWeights.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            SetTimeWeightsController ctrl = loader.getController();
            ctrl.setMainController(this);

            if (currentAlg instanceof TimeWeightedAlgorithm twa) {
                ctrl.initWeights(twa.getWeights());
            }

            Stage dialog = new Stage();
            dialog.setTitle("Set Time Weights");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SetTimeWeights.fxml\n" + e.getMessage());
        }
    }

    @FXML
    private void handleSelectAlgorithm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SelectAlg.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            SelectAlgController ctrl = loader.getController();
            ctrl.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Select Algorithm");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SelectAlg.fxml\n" + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  HANDLERS for "Add New Item", "Remove Item", "Change Item Weight"
    // ----------------------------------------------------------------
    @FXML
    private void handleOpenAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/AddNewItem.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            AddNewItemController controller = loader.getController();
            controller.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Add New Item");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException ex) {
            System.err.println("Failed to load FXML: " + ex.getMessage());
        }
    }

    @FXML
    private void handleRemoveItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/RemoveItem.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            RemoveItemController controller = loader.getController();
            controller.setMainController(this);
            controller.initComboBox(new ArrayList<>(itemWeights.keySet()));

            Stage dialog = new Stage();
            dialog.setTitle("Remove Item");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException ex) {
            System.err.println("Failed to load FXML: " + ex.getMessage());
        }
    }

    @FXML
    private void handleOpenChangeWeight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/ChangeItemWeight.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            ChangeItemWeightController controller = loader.getController();
            controller.setMainController(this);
            controller.initItems(new ArrayList<>(itemWeights.keySet()));

            Stage dialog = new Stage();
            dialog.setTitle("Change Item Weight");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException ex) {
            System.err.println("Failed to load FXML: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  UTILITY
    // ----------------------------------------------------------------
    private void applyDarkMode(boolean enable) {
        if (menuBox == null || menuBox.getScene() == null) return;
        Scene mainScene = menuBox.getScene();

        var darkCss = getClass().getResource("/css/dark-mode.css");
        if (darkCss == null) {
            System.err.println("dark-mode.css not found!");
            return;
        }
        // Avoid potential NPE warning:
        String darkPath = darkCss.toExternalForm();

        if (enable) {
            if (! mainScene.getStylesheets().contains(darkPath)) {
                mainScene.getStylesheets().add(darkPath);
            }
        } else {
            mainScene.getStylesheets().remove(darkPath);
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // For other controllers to query the current algorithm
    public IQueueAlgorithm getCurrentAlgorithm() {
        return currentAlg;
    }

    // For "ChangeItemWeightController"
    public int getWeightForItem(String item) {
        return itemWeights.getOrDefault(item, 1);
    }

    public void setWeightForItem(String item, int newWeight) {
        if (itemWeights.containsKey(item)) {
            itemWeights.put(item, newWeight);
        }
    }
}
