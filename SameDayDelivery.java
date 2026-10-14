public class SameDayDelivery implements DeliveryStrategy {
    @Override
    public String getName() {
        return "Same Day";
    }

    @Override
    public double calculateFee(double baseFee) {
        return (baseFee * 2) + 100;
    }
}
