import java.util.List;

public class CafeOrderQueue {
    private IQueueAlgorithm algorithm;

    public CafeOrderQueue(IQueueAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setAlgorithm(IQueueAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void addOrder(Order order) {
        algorithm.addOrder(order);
    }

    public Order processNextOrder() {
        return algorithm.processNextOrder();
    }

    public List<Order> getQueue() {
        return algorithm.getQueue();
    }
}
