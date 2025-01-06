package queue;

import model.Order;
import java.util.Queue;

public class CafeOrderQueue {
    private IQueueAlgorithm algorithm;

    public CafeOrderQueue(IQueueAlgorithm initialAlgorithm) {
        this.algorithm = initialAlgorithm;
    }

    public void setAlgorithm(IQueueAlgorithm newAlgorithm) {
        Queue<Order> currentOrders = algorithm.getQueue();
        this.algorithm = newAlgorithm;

        // Validate and re-add orders to the new algorithm's queue
        for (Order order : currentOrders) {
            if (order != null && order.getItems() != null) {
                this.algorithm.addOrder(order);
            }
        }
    }

    public void addOrder(Order order) {
        if (order != null && order.getItems() != null) {
            algorithm.addOrder(order);
        }
    }

    public Order processNextOrder() {
        return algorithm.processNextOrder();
    }

    public Queue<Order> getQueue() {
        return algorithm.getQueue();
    }
}
