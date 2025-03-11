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
    //  LOGIN-RELATED FIELDS (from your old ClientUIController)
    // ----------------------------------------------------------------
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label responseLabel;

    // (Optional) If you want to handle "SaveUser" or "FindUser":
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
    //  TIME-VALUES & TIME-WEIGHTS STORAGE (so user can set them anytime)
    // ----------------------------------------------------------------
    /**
     * Stored "last known" thresholds for time-based algorithms:
     * [0] = low threshold, [1] = mid threshold.
     */
    private int[] storedTimeThresholds = {5, 10};

    /**
     * Stored "last known" weights for time-based algorithms:
     * [0] = low weight, [1] = mid weight, [2] = high weight.
     */
    private int[] storedTimeWeights = {1, 2, 3};

    public int[] getStoredTimeThresholds() {
        return storedTimeThresholds;
    }
    public void setStoredTimeThresholds(int low, int mid) {
        storedTimeThresholds[0] = low;
        storedTimeThresholds[1] = mid;
    }

    public int[] getStoredTimeWeights() {
        return storedTimeWeights;
    }
    public void setStoredTimeWeights(int low, int mid, int high) {
        storedTimeWeights[0] = low;
        storedTimeWeights[1] = mid;
        storedTimeWeights[2] = high;
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
                // Example defaults for batch vs. individual
                bia.setWeights(3, 1);
                // Also apply stored time-thresholds/weights if you want
                bia.setTimeThresholds(storedTimeThresholds[0], storedTimeThresholds[1]);
                bia.setWeights(storedTimeWeights[0], storedTimeWeights[1], storedTimeWeights[2]);
                newAlg = bia;
                break;

            case "Member Priority":
                MemberPriorityAlgorithm mpa = new MemberPriorityAlgorithm();
                // Also apply stored thresholds/weights
                mpa.setTimeThresholds(storedTimeThresholds[0], storedTimeThresholds[1]);
                mpa.setWeights(storedTimeWeights[0], storedTimeWeights[1], storedTimeWeights[2]);
                newAlg = mpa;
                break;

            case "Item Weight":
                ItemWeightAlgorithm iwa = new ItemWeightAlgorithm();
                // Example: set item weights for the JAR side
                iwa.setItemWeight("Cappuccino", 2);
                iwa.setItemWeight("Espresso",   1);
                iwa.setItemWeight("Sandwich",   3);
                // (If your ItemWeightAlgorithm also extends TimeWeightedAlgorithm,
                //  you can do iwa.setTimeThresholds(...) / iwa.setWeights(...) too)
                newAlg = iwa;
                break;

            case "Time Based":
            default:
                TimeBasedAlgorithm tba = new TimeBasedAlgorithm();
                newAlg = tba;
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
        for (Map.Entry<String, Integer> e : cartMap.entrySet()) {
            cartView.getItems().add(e.getKey() + " x" + e.getValue());
        }
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
        // Insert near the bottom, above the last 3 big buttons
        int insertIndex = Math.max(0, menuBox.getChildren().size() - 3);
        menuBox.getChildren().add(insertIndex, lbl);
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
                    // remove next node if it's an HBox
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
            // Now references THIS controller (ClientUIController)
            ctrl.setMainController(this);

            // Show the "stored" thresholds, even if not TWA
            int[] stored = getStoredTimeThresholds();
            ctrl.initValues(stored);

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

            // Show the "stored" weights
            int[] w = getStoredTimeWeights();
            ctrl.initWeights(w);

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
        } catch (IOException ex) {
            System.err.println("Failed to load FXML: " + ex.getMessage());
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
