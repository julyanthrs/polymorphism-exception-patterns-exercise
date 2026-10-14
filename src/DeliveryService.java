// Strategy pattern: each delivery service has its own fee calculation.
public interface DeliveryService {
    String getName();

    double calculateFee(double baseFee, double chargeableWeight) throws ParcelException;
}
