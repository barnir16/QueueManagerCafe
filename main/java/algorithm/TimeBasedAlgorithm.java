package algorithm;

import model.Order;
import queue.IQueueAlgorithm;

import java.util.LinkedList;
import java.util.Queue;

public class TimeBasedAlgorithm implements IQueueAlgorithm {
    private final Queue<Order> queue;

    public TimeBasedAlgorithm() {
        this.queue = new LinkedList<>();
    }

    @Override
    public void addOrder(Order order) {
        queue.add(order);
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
