public class Document extends Parcel {
    private final int numberOfPages;

    public Document(String trackingNumber, String receiverName, double weight,
                    int numberOfPages) {
        super(trackingNumber, receiverName, weight);
        if (weight > 0.5) {
            throw new IllegalArgumentException("Document weight must be 0.5 kg or less!");
        }
        if (numberOfPages < 1 || numberOfPages > 100) {
            throw new IllegalArgumentException("Number of pages must be from 1 to 100!");
        }
        this.numberOfPages = numberOfPages;
    }

    @Override
    public String getType() {
        return "Document";
    }

    @Override
    public double getChargeableWeight() {
        return getWeight();
    }

    @Override
    public double getBaseFee() {
        return 50;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }
}
