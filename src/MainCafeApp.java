public class MainCafeApp {
    public static void main(String[] args) {
        CafeOrderQueue queue = new CafeOrderQueue(new WeightedPriorityAlgorithm());
        // Optionally switch to other algorithms during runtime
        CafeApp.main(args);
    }
}
