package algorithm;

import model.Order;
import queue.IQueueAlgorithm;

import java.util.PriorityQueue;
import java.util.Queue;

public class ItemWeightAlgorithm implements IQueueAlgorithm {
    private final Queue<Order> queue;

    public ItemWeightAlgorithm() {
        this.queue = new PriorityQueue<>((o1, o2) -> Integer.compare(calculatePriority(o2), calculatePriority(o1)));
    }

    private int calculatePriority(Order order) {
        return order.getItems().values().stream().mapToInt(Integer::intValue).sum();
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
