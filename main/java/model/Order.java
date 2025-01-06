package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Order {
    private final int orderId;
    private final Map<String, Integer> items;
    private final long orderTime;
    private int batchWeight;
    private final boolean isMember;

    public Order(int orderId, Map<String, Integer> items, boolean isMember) {
        this.orderId = orderId;
        this.items = items;
        this.isMember = isMember;
        this.orderTime = System.currentTimeMillis();
        this.batchWeight = 1; // Default weight
    }

    public int getOrderId() {
        return orderId;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public int getBatchWeight() {
        return batchWeight;
    }

    public void setBatchWeight(int batchWeight) {
        this.batchWeight = batchWeight;
    }

    public boolean isMember() {
        return isMember;
    }

    @Override
    public String toString() {
        return String.format("OrderID: %d | Items: %s | Time: %s | Weight: %d | Member: %s",
                orderId,
                formatItems(),
                formatOrderTime(),
                batchWeight,
                isMember ? "Yes" : "No");
    }

    private String formatItems() {
        return items.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " x" + entry.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("No items");
    }

    private String formatOrderTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(orderTime));
    }
}
