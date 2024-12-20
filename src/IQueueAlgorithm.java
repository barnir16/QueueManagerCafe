import java.util.List;

public interface IQueueAlgorithm {
    void addOrder(Order order);
    Order processNextOrder();
    List<Order> getQueue();
    int calculatePriority(Order order); // Ensure all algorithms implement this
}
