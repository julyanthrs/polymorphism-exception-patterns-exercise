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

    public void addParcel(Parcel parcel) throws ParcelException {
        if (findParcel(parcel.getTrackingNumber()) != null) {
            throw new ParcelException("Tracking number already exists!");
        }
        if (parcel.getDeliveryService() == null) {
            throw new ParcelException("Please select a delivery service!");
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

    public ArrayList<Parcel> getAllParcels() {
        return new ArrayList<>(parcels);
    }
}
