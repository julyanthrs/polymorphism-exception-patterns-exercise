public class SameDayDelivery implements DeliveryService {
    @Override
    public String getName() {
        return "Same Day";
    }

    @Override
    public double calculateFee(double baseFee, double chargeableWeight) throws ParcelException {
        if (chargeableWeight > 10) {
            throw new ParcelException("Same Day delivery is only for parcels up to 10 kg!");
        }
        return (baseFee * 2) + 100;
    }
}
