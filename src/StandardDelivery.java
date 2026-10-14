public class StandardDelivery implements DeliveryService {
    @Override
    public String getName() {
        return "Standard";
    }

    @Override
    public double calculateFee(double baseFee, double chargeableWeight) {
        return baseFee;
    }
}
