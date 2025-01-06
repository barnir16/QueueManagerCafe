package algorithm;

import model.Order;
import queue.IQueueAlgorithm;

import java.util.PriorityQueue;
import java.util.Queue;

public class MemberPriorityAlgorithm implements IQueueAlgorithm {
    private final Queue<Order> queue;

    public MemberPriorityAlgorithm() {
        this.queue = new PriorityQueue<>((o1, o2) -> Integer.compare(calculatePriority(o2), calculatePriority(o1)));
    }

    protected int calculatePriority(Order order) {
        return order.isMember() ? 10 : 1;
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
