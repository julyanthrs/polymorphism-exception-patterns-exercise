// Polymorphism: different parcel types provide their own weights and base fees.
public abstract class Parcel {
    private final String trackingNumber;
    private final String receiverName;
    private final double weight;
    private DeliveryService deliveryService;
    private double deliveryFee;
    private boolean delivered;

    public Parcel(String trackingNumber, String receiverName, double weight) throws ParcelException {
        if (trackingNumber == null || !trackingNumber.matches("ABC[0-9]{4}")) {
            throw new ParcelException("Tracking number must be ABC followed by 4 digits (example: ABC0001).");
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new ParcelException("Receiver name cannot be empty!");
        }
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new ParcelException("Weight must be a valid number greater than 0!");
        }
        this.trackingNumber = trackingNumber;
        this.receiverName = receiverName.trim();
        this.weight = weight;
    }

    public abstract String getType();
    public abstract double getChargeableWeight();
    public abstract double getBaseFee();

    public void setDeliveryService(DeliveryService newService) throws ParcelException {
        if (delivered) {
            throw new ParcelException("Parcel already delivered!");
        }
        if (newService == null) {
            throw new ParcelException("Please select a delivery service!");
        }
        // Calculate first: a rejected service must not replace the old service.
        double newFee = newService.calculateFee(getBaseFee(), getChargeableWeight());
        deliveryService = newService;
        deliveryFee = newFee;
    }

    public void markAsDelivered() throws ParcelException {
        if (delivered) {
            throw new ParcelException("Parcel was already delivered.");
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

    public DeliveryService getDeliveryService() {
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
