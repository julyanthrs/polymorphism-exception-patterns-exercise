// Polymorphism: different parcel types provide their own weights and base fees.
public abstract class Parcel {
    private final String trackingNumber;
    private final String receiverName;
    private final double weight;
    private DeliveryStrategy deliveryService;
    private double deliveryFee;
    private boolean delivered;

    public Parcel(String trackingNumber, String receiverName, double weight) {
        if (trackingNumber == null || !trackingNumber.matches("ABC[0-9]{4}")) {
            throw new IllegalArgumentException("Tracking number must be ABC followed by 4 digits (example: ABC0001).");
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver name cannot be empty!");
        }
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException("Weight must be a valid number greater than 0!");
        }
        this.trackingNumber = trackingNumber;
        this.receiverName = receiverName.trim();
        this.weight = weight;
    }

    public abstract String getType();
    public abstract double getChargeableWeight();
    public abstract double getBaseFee();

    public void setDeliveryStrategy(DeliveryStrategy newService) {
        if (delivered) {
            throw new IllegalArgumentException("Parcel already delivered!");
        }
        validateDeliveryStrategy(newService);
        if (deliveryService != null && deliveryService.getClass() == newService.getClass()) {
            throw new IllegalArgumentException("This parcel already uses "
                    + newService.getName() + " delivery service!");
        }
        // Validate and calculate before changing the stored service or fee.
        double newFee = newService.calculateFee(getBaseFee());
        deliveryService = newService;
        deliveryFee = newFee;
    }

    private void validateDeliveryStrategy(DeliveryStrategy service) {
        if (service == null) {
            throw new IllegalArgumentException("Please select a delivery service!");
        }
        if (service instanceof SameDayDelivery && getChargeableWeight() > 10) {
            throw new IllegalArgumentException("Same Day delivery is only for parcels up to 10 kg!");
        }
    }

    public void markAsDelivered() {
        if (delivered) {
            throw new IllegalArgumentException("Parcel was already delivered.");
        }
        delivered = true;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public double getWeight() {
        return weight;
    }

    public DeliveryStrategy getDeliveryStrategy() {
        return deliveryService;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public String getStatus() {
        return delivered ? "Delivered" : "Pending";
    }
}
