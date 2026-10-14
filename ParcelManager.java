import java.util.ArrayList;

// Singleton pattern: the application shares one parcel manager.
public class ParcelManager {
    private static final ParcelManager instance = new ParcelManager();
    private final ArrayList<Parcel> parcels = new ArrayList<>();

    private ParcelManager() {
    }

    public static ParcelManager getInstance() {
        return instance;
    }

    public void addParcel(Parcel parcel) {
        if (parcel == null) {
            throw new IllegalArgumentException("Parcel cannot be empty!");
        }
        if (findParcel(parcel.getTrackingNumber()) != null) {
            throw new IllegalArgumentException("Tracking number already exists!");
        }
        if (parcel.getDeliveryStrategy() == null) {
            throw new IllegalArgumentException("Please select a delivery service!");
        }
        if (parcel.isDelivered()) {
            throw new IllegalArgumentException("New parcels must have Pending status!");
        }
        parcels.add(parcel);
    }

    public Parcel findParcel(String trackingNumber) {
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingNumber().equals(trackingNumber)) {
                return parcel;
            }
        }
        return null;
    }

    private Parcel getParcel(String trackingNumber) {
        Parcel parcel = findParcel(trackingNumber);
        if (parcel == null) {
            throw new IllegalArgumentException("Parcel not found!");
        }
        return parcel;
    }

    public Parcel getPendingParcel(String trackingNumber) {
        Parcel parcel = getParcel(trackingNumber);
        if (parcel.isDelivered()) {
            throw new IllegalArgumentException("Parcel already delivered!");
        }
        return parcel;
    }

    public double changeDeliveryService(String trackingNumber, DeliveryStrategy service) {
        Parcel parcel = getPendingParcel(trackingNumber);
        parcel.setDeliveryStrategy(service);
        return parcel.getDeliveryFee();
    }

    public void markAsDelivered(String trackingNumber) {
        Parcel parcel = getParcel(trackingNumber);
        parcel.markAsDelivered();
    }

    public ArrayList<Parcel> getAllParcels() {
        return new ArrayList<>(parcels);
    }
}
