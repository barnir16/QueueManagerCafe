package algorithm;

import model.Order;
import queue.IQueueAlgorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BatchItemAlgorithm implements IQueueAlgorithm {
    private final Queue<Order> queue;

    public BatchItemAlgorithm() {
        this.queue = new LinkedList<>();
    }

    @Override
    public void addOrder(Order order) {
        // Logic for batching similar orders
        for (Order existingOrder : queue) {
            if (existingOrder.getItems().equals(order.getItems())) {
                int batchWeight = existingOrder.getBatchWeight() + 1;
                existingOrder.setBatchWeight(batchWeight);
                return; // Don't add duplicate order
            }
        }
        queue.add(order); // Add new order if not already batched
    }

    @Override
    public Order processNextOrder() {
        return queue.poll();
    }

    @Override
    public Queue<Order> getQueue() {
        return queue;
    }
}
