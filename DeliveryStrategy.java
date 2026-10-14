// Strategy pattern: each delivery service has its own fee calculation.
public interface DeliveryStrategy {
    String getName();

    double calculateFee(double baseFee);
}
