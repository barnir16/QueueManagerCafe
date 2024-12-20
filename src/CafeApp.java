import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class CafeApp extends Application {
    private static final int DEFAULT_ORDER_ID_LENGTH = 5;
    private static int currentOrderId = 1;
    private boolean isDarkMode = false;

    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    private final ObservableList<String> processedOrdersList = FXCollections.observableArrayList();
    private final Map<String, Integer> currentOrder = new HashMap<>();
    private final String[] menuItems = {"Cappuccino", "Espresso", "Sandwich", "Muffin"};

    private Label currentAlgorithmLabel;
    private VBox mainLayout;

    @Override
    public void start(Stage primaryStage) {
        ListView<String> cartView = createStyledListView("Your cart is empty");
        ListView<String> queueListView = createStyledListView("Queue is empty");
        queueListView.setItems(queueList); // Bind to the observable list
        ListView<String> processedOrdersView = createStyledListView("No processed orders yet");
        processedOrdersView.setItems(processedOrdersList); // Bind to the observable list

        VBox menuBox = createMenuBox(cartView);
        HBox buttonBox = createButtonBox(cartView, queueListView, processedOrdersView);

        SplitPane verticalSplitPane = new SplitPane(cartView, queueListView, processedOrdersView);
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.setDividerPositions(0.33, 0.66);

        SplitPane horizontalSplitPane = new SplitPane(menuBox, verticalSplitPane);
        horizontalSplitPane.setDividerPositions(0.25);

        currentAlgorithmLabel = new Label("Current Algorithm: Weighted Priority Algorithm");
        mainLayout = new VBox(10, currentAlgorithmLabel, horizontalSplitPane, buttonBox);

        Scene scene = new Scene(mainLayout, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cafe Order Management");
        primaryStage.show();
    }

    private ListView<String> createStyledListView(String placeholderText) {
        ListView<String> listView = new ListView<>();
        listView.setPlaceholder(new Label(placeholderText));
        listView.setPrefHeight(200); // Adjust default size
        return listView;
    }

    private VBox createMenuBox(ListView<String> cartView) {
        VBox menuBox = new VBox(10);
        for (String item : menuItems) {
            HBox itemBox = new HBox(10);
            Label itemLabel = new Label(item);
            Button addButton = new Button("+");
            Button removeButton = new Button("-");
            Label quantityLabel = new Label("0");

            addButton.setOnAction(e -> {
                currentOrder.put(item, currentOrder.getOrDefault(item, 0) + 1);
                quantityLabel.setText(String.valueOf(currentOrder.get(item)));
                updateCartView(cartView);
            });

            removeButton.setOnAction(e -> {
                if (currentOrder.getOrDefault(item, 0) > 0) {
                    currentOrder.put(item, currentOrder.get(item) - 1);
                    if (currentOrder.get(item) == 0) currentOrder.remove(item);
                    quantityLabel.setText(String.valueOf(currentOrder.getOrDefault(item, 0)));
                    updateCartView(cartView);
                }
            });

            itemBox.getChildren().addAll(itemLabel, addButton, removeButton, quantityLabel);
            menuBox.getChildren().add(itemBox);
        }

        menuBox.getChildren().add(new CheckBox("Member"));
        return menuBox;
    }

    private HBox createButtonBox(ListView<String> cartView, ListView<String> queueListView, ListView<String> processedOrdersView) {
        HBox buttonBox = new HBox(10);
        Button placeOrderButton = new Button("Place Order");
        Button processOrderButton = new Button("Process Order");
        Button deleteProcessedOrderButton = new Button("Delete Processed Order");
        Button toggleThemeButton = new Button("Toggle Theme");
        Button switchAlgorithmButton = new Button("Switch Algorithm");

        placeOrderButton.setOnAction(e -> {
            if (!currentOrder.isEmpty()) {
                int orderId = currentOrderId;
                currentOrderId = (currentOrderId % (int) Math.pow(10, DEFAULT_ORDER_ID_LENGTH)) + 1;

                queueList.add("Order ID: " + orderId + " " + currentOrder.toString());
                currentOrder.clear();
                cartView.getItems().clear();
                reapplyDarkMode(cartView, queueListView, processedOrdersView);
            }
        });

        processOrderButton.setOnAction(e -> {
            if (!queueList.isEmpty()) {
                String processedOrder = queueList.remove(0);
                processedOrdersList.add(processedOrder);
            }
        });

        deleteProcessedOrderButton.setOnAction(e -> {
            String selected = processedOrdersView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                processedOrdersList.remove(selected);
            }
        });

        toggleThemeButton.setOnAction(e -> toggleTheme(cartView, queueListView, processedOrdersView));
        switchAlgorithmButton.setOnAction(e -> showAlgorithmSelectionDialog());

        buttonBox.getChildren().addAll(placeOrderButton, processOrderButton, deleteProcessedOrderButton, toggleThemeButton, switchAlgorithmButton);
        return buttonBox;
    }

    private void toggleTheme(ListView<String>... listViews) {
        if (isDarkMode) {
            mainLayout.getStyleClass().remove("dark-mode");
            for (ListView<String> listView : listViews) {
                listView.getStyleClass().remove("dark-mode");
            }
        } else {
            mainLayout.getStyleClass().add("dark-mode");
            for (ListView<String> listView : listViews) {
                listView.getStyleClass().add("dark-mode");
            }
        }
        isDarkMode = !isDarkMode;
    }

    private void reapplyDarkMode(ListView<String>... listViews) {
        if (isDarkMode) {
            mainLayout.getStyleClass().add("dark-mode");
            for (ListView<String> listView : listViews) {
                listView.getStyleClass().add("dark-mode");
            }
        }
    }

    private void showAlgorithmSelectionDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Weighted Priority Algorithm", "Weighted Priority Algorithm", "Batch Item Algorithm", "Item Weight Algorithm", "Time-Based Algorithm");
        dialog.setTitle("Switch Algorithm");
        dialog.setHeaderText("Select a Queue Algorithm");
        dialog.setContentText("Available algorithms:");

        dialog.showAndWait().ifPresent(selected -> currentAlgorithmLabel.setText("Current Algorithm: " + selected));
    }

    private void updateCartView(ListView<String> cartView) {
        cartView.getItems().clear();
        currentOrder.forEach((item, qty) -> cartView.getItems().add(item + " x" + qty));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
