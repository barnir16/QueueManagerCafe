import java.util.ArrayList;
import java.util.List;

/**
 * TimeBasedAlgorithm - Prioritizes orders based on the time they were added to the queue.
 * The earlier an order is placed, the higher its priority.
 */
public class TimeBasedAlgorithm implements IQueueAlgorithm {
    private final List<Order> queue = new ArrayList<>();

    @Override
    public void addOrder(Order order) {
        queue.add(order);
    }

    @Override
    public int calculatePriority(Order order) {
        // In a time-based system, priority is simply determined by the order's position in the queue.
        // Earlier orders have higher priority (lower index -> higher priority).
        return queue.indexOf(order);
    }

    @Override
    public Order processNextOrder() {
        if (queue.isEmpty()) return null;

        // Process the first order in the queue (FIFO - First In, First Out)
        return queue.remove(0);
    }

    @Override
    public List<Order> getQueue() {
        return new ArrayList<>(queue);
    }
}
