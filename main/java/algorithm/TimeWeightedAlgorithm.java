package algorithm;

import model.Order;
import queue.IQueueAlgorithm;

import java.util.LinkedList;
import java.util.Queue;

public class TimeWeightedAlgorithm implements IQueueAlgorithm {
    private int[] timeThresholds = {5, 10, 15}; // Default thresholds
    private int[] weights = {1, 2, 3};          // Default weights
    private final Queue<Order> queue = new LinkedList<>();

    @Override
    public void addOrder(Order order) {
        queue.add(order);
        // Custom logic for sorting can be added here
    }

    @Override
    public Order processNextOrder() {
        return queue.poll(); // FIFO processing for now
    }

    @Override
    public Queue<Order> getQueue() {
        return queue;
    }

    public int[] getTimeThresholds() {
        return timeThresholds;
    }

    public void setTimeThresholds(int low, int mid, int high) {
        if (low > mid || mid > high) {
            throw new IllegalArgumentException("Low <= Mid <= High required.");
        }
        this.timeThresholds = new int[]{low, mid, high};
    }

    public int[] getWeights() {
        return weights;
    }

    public void setWeights(int low, int mid, int high) {
        if (low <= 0 || mid <= 0 || high <= 0) {
            throw new IllegalArgumentException("Weights must be positive.");
        }
        this.weights = new int[]{low, mid, high};
    }
}
