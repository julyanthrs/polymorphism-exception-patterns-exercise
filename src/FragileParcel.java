// Fragile is a Box, so it reuses the dimensions and chargeable-weight rule.
public class FragileParcel extends BoxParcel {
    private final String handlingNote;

    public FragileParcel(String trackingNumber, String receiverName, double weight,
                         int length, int width, int height, String handlingNote) throws ParcelException {
        super(trackingNumber, receiverName, weight, length, width, height);
        if (weight > 30) {
            throw new ParcelException("Fragile weight must be 30 kg or less!");
        }
        if (handlingNote == null || handlingNote.trim().isEmpty()) {
            throw new ParcelException("Handling note cannot be empty!");
        }
        this.handlingNote = handlingNote.trim();
    }

    @Override
    public String getType() {
        return "Fragile";
    }

    @Override
    public double getBaseFee() {
        return super.getBaseFee() + 100;
    }

    public String getHandlingNote() {
        return handlingNote;
    }
}
