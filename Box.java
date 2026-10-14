public class Box extends Parcel {
    private final int length;
    private final int width;
    private final int height;

    public Box(String trackingNumber, String receiverName, double weight,
               int length, int width, int height) {
        super(trackingNumber, receiverName, weight);
        if (weight > 50) {
            throw new IllegalArgumentException("Box weight must be 50 kg or less!");
        }
        if (length <= 0 || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Length, width, and height must be whole numbers greater than 0!");
        }
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    public String getType() {
        return "Box";
    }

    public double getVolumetricWeight() {
        // Convert before multiplication to avoid integer overflow.
        return (double) length * width * height / 3500;
    }

    @Override
    public double getChargeableWeight() {
        return Math.ceil(Math.max(getWeight(), getVolumetricWeight()));
    }

    @Override
    public double getBaseFee() {
        return 40 * getChargeableWeight();
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
