package model;

import model.Order;
import queue.IQueueAlgorithm;

public class QueueClient {
    private final IQueueAlgorithm algorithm;

    public QueueClient(IQueueAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void addOrder(Order order) {
        algorithm.addOrder(order);
    }

    public Order processNextOrder() {
        return algorithm.processNextOrder();
    }
}
