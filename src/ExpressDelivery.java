public class ExpressDelivery implements DeliveryService {
    @Override
    public String getName() {
        return "Express";
    }

    @Override
    public double calculateFee(double baseFee, double chargeableWeight) {
        return baseFee * 1.5;
    }
}
