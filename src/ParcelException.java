// A checked exception for invalid parcel details and delivery operations.
public class ParcelException extends Exception {
    private static final long serialVersionUID = 1L;

    public ParcelException(String message) {
        super(message);
    }
}
