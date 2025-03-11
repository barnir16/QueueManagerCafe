package app.client.ui;

import app.MainApp;
import app.client.Client;
import app.server.service.Request;
import app.server.service.Response;
import algorithm.BatchItemAlgorithm;
import algorithm.ItemWeightAlgorithm;
import algorithm.MemberPriorityAlgorithm;
import algorithm.TimeBasedAlgorithm;
import algorithm.TimeWeightedAlgorithm;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import queue.IQueueAlgorithm;
import queue.OrderQueue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * A merged controller that handles:
 *  - Login fields (username/password) + potential "Register"
 *  - Main cafe logic: cart, items, queue, algorithm switching, etc.
 */
public class ClientUIController {

    // ----------------------------------------------------------------
    //  LOGIN-RELATED FIELDS
    // ----------------------------------------------------------------
    @FXML private TextField usernameField;      // If used by your login form
    @FXML private TextField passwordField;      // If used by your login form
    @FXML private Label responseLabel;          // For showing login status, etc.

    private final Gson gson = new Gson();

    /**
     * Example: handleLogin. If not needed, you can remove or adapt.
     */
    @FXML
    private void handleLogin() {
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "LOGIN");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        String json = gson.toJson(new Request(headers, body));
        String serverResponse = Client.sendJsonRequest(json);
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.startsWith("ERROR:")) {
            if (responseLabel != null) {
                responseLabel.setText("Login failed: " + serverResponse);
            }
            return;
        }

        Response resp = gson.fromJson(serverResponse, Response.class);
        if ("SUCCESS".equals(resp.getMessage())) {
            if (responseLabel != null) {
                responseLabel.setText("Login successful!");
            }
            // Close login window if desired
            if (responseLabel != null) {
                Stage stage = (Stage) responseLabel.getScene().getWindow();
                stage.close();
            }
            // Show main cafe
            MainApp.showMainCafe();
        } else {
            if (responseLabel != null) {
                responseLabel.setText("Login failed: " + resp.getMessage());
            }
        }
    }

    /**
     * Example: handleRegister. If not needed, you can remove or adapt.
     */
    @FXML
    private void handleRegister() {
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "REGISTER");

        Map<String, String> body = new HashMap<>();
        body.put("username", usernameField.getText());
        body.put("password", passwordField.getText());

        String json = gson.toJson(new Request(headers, body));
        String serverResponse = Client.sendJsonRequest(json);
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.startsWith("ERROR:")) {
            if (responseLabel != null) {
                responseLabel.setText("Registration failed: " + serverResponse);
            }
            return;
        }

        Response resp = gson.fromJson(serverResponse, Response.class);
        if ("REGISTERED".equals(resp.getMessage())) {
            if (responseLabel != null) {
                responseLabel.setText("Registration successful!");
            }
        } else {
            if (responseLabel != null) {
                responseLabel.setText("Registration failed: " + resp.getMessage());
            }
        }
    }

    // ----------------------------------------------------------------
    //  MAIN CAFE FIELDS
    // ----------------------------------------------------------------
    @FXML private Label currentAlgorithmLabel;
    @FXML private VBox menuBox;
    @FXML private ListView<String> cartView;
    @FXML private ListView<String> queueView;
    @FXML private ListView<String> processedOrdersView;
    @FXML private CheckBox memberCheckBox; // "Member?" in FXML

    // Items in the cart: itemName -> quantity
    private final Map<String, Integer> cartMap = new HashMap<>();

    // Lists for the queue & processed
    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    private final ObservableList<String> processedList = FXCollections.observableArrayList();

    // Local item weights map
    private final Map<String, Integer> itemWeights = new HashMap<>();

    private OrderQueue cafeQueue;
    private IQueueAlgorithm currentAlg;

    private int nextOrderId = 1;
    private boolean isDarkMode = false;

    // ----------------------------------------------------------------
    //  FXML initialize()
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        // Default algorithm
        currentAlg = new TimeBasedAlgorithm();
        cafeQueue = new OrderQueue(currentAlg);

        if (currentAlgorithmLabel != null) {
            currentAlgorithmLabel.setText("Current Algorithm: Time Based");
        }

        if (queueView != null) {
            queueView.setItems(queueList);
        }
        if (processedOrdersView != null) {
            processedOrdersView.setItems(processedList);
        }

        // Default item weights
        itemWeights.put("Cappuccino", 2);
        itemWeights.put("Espresso",   1);
        itemWeights.put("Sandwich",   3);

        Platform.runLater(() -> {
            // e.g. applyDarkMode(false);
        });
    }

    // ----------------------------------------------------------------
    //  SWITCH ALGORITHM
    // ----------------------------------------------------------------
    public void updateAlgorithm(String algName) {
        IQueueAlgorithm newAlg;
        switch (algName) {
            case "Batch Item":
                BatchItemAlgorithm bia = new BatchItemAlgorithm();
                // Example batch vs. individual
                bia.setWeights(3, 1);
                newAlg = bia;
                break;
            case "Member Priority":
                newAlg = new MemberPriorityAlgorithm();
                break;
            case "Item Weight":
                ItemWeightAlgorithm iwa = new ItemWeightAlgorithm();
                // Also set default item weights for JAR logic
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
    //  PLUS / MINUS
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
        if (menuBox == null) {
            System.err.println("menuBox is null - can't add item UI");
            return;
        }
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

        // Insert above "Member?" if possible
        int insertIndex = findMemberCheckBoxIndex();
        if (insertIndex < 0) {
            insertIndex = menuBox.getChildren().size();
        }
        // Insert the label, then the HBox
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
                    // remove the label
                    children.remove(i);
                    // remove next node if HBox
                    if (i < children.size() && children.get(i) instanceof HBox) {
                        children.remove(i);
                    }
                    System.out.println("Removed item from menu: " + name);
                    return;
                }
            }
        }
        System.out.println("Item '" + name + "' not found in the menuBox UI.");
    }

    /**
     * Find the index of the memberCheckBox in the menuBox children, or -1 if not found.
     */
    private int findMemberCheckBoxIndex() {
        if (memberCheckBox == null || menuBox == null) return -1;
        var children = menuBox.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == memberCheckBox) {
                return i;
            }
        }
        return -1;
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
    //  THEME / TIME VALUES / TIME WEIGHTS / SELECT ALGORITHM
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
            ctrl.setMainController(this);

            // If currentAlg is TimeWeightedAlgorithm, show thresholds
            if (currentAlg instanceof TimeWeightedAlgorithm twa) {
                ctrl.initValues(twa.getTimeThresholds());
            }

            Stage dialog = new Stage();
            dialog.setTitle("Set Time Values");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException ex) {
            showError("Failed to open SetTimeValues.fxml\n" + ex.getMessage());
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
        } catch (IOException ex) {
            showError("Failed to open SetTimeWeights.fxml\n" + ex.getMessage());
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
            showError("Failed to open SelectAlg.fxml\n" + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  OPENING ADD/REMOVE/CHANGE ITEM DIALOGS
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
            System.err.println("Failed to load AddNewItem.fxml: " + ex.getMessage());
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
            System.err.println("Failed to load RemoveItem.fxml: " + ex.getMessage());
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
            System.err.println("Failed to load ChangeItemWeight.fxml: " + ex.getMessage());
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

    /**
     * Expose the current algorithm for controllers like SetTimeValues/SetTimeWeights.
     */
    public IQueueAlgorithm getCurrentAlgorithm() {
        return currentAlg;
    }

    /**
     * For ChangeItemWeightController usage
     */
    public int getWeightForItem(String item) {
        return itemWeights.getOrDefault(item, 1);
    }

    public void setWeightForItem(String item, int newWeight) {
        if (itemWeights.containsKey(item)) {
            itemWeights.put(item, newWeight);
        }
    }
}
