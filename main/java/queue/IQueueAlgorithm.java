package queue;

import model.Order;

import java.util.Queue;

public interface IQueueAlgorithm {
    void addOrder(Order order);

    Order processNextOrder();

    Queue<Order> getQueue();
}
