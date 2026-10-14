// Simple checks without a testing framework. Run this separately from Main.
public class ParcelSystemTest {
    private static int checks = 0;

    public static void main(String[] args) throws ParcelException {
        checkFees();
        checkServiceChanges();
        checkValidation();
        checkManager();
        System.out.println("All " + checks + " checks passed.");
    }

    private static void checkFees() throws ParcelException {
        Parcel document = new DocumentParcel("ABC0001", "Juan", 0.5, 100);
        check(document.getChargeableWeight() == 0.5, "Document uses its actual weight");
        check(document.getBaseFee() == 50, "Document has a flat base fee");
        document.setDeliveryService(new StandardDelivery());
        check(document.getDeliveryFee() == 50, "Document Standard fee");
        document.setDeliveryService(new ExpressDelivery());
        check(document.getDeliveryFee() == 75, "Document Express fee");
        document.setDeliveryService(new SameDayDelivery());
        check(document.getDeliveryFee() == 200, "Document Same Day fee");

        Parcel box = new BoxParcel("ABC0002", "Ana", 2, 10, 10, 10);
        check(box.getChargeableWeight() == 2, "Whole-number weight stays the same");
        box.setDeliveryService(new StandardDelivery());
        check(box.getDeliveryFee() == 80, "Box Standard fee");
        box.setDeliveryService(new ExpressDelivery());
        check(box.getDeliveryFee() == 120, "Box Express fee");
        box.setDeliveryService(new SameDayDelivery());
        check(box.getDeliveryFee() == 260, "Box Same Day fee");

        Parcel roundedBox = new BoxParcel("ABC0003", "Ben", 2.1, 10, 10, 10);
        check(roundedBox.getChargeableWeight() == 3, "Decimal box weight rounds up");
        BoxParcel volumeBox = new BoxParcel("ABC0004", "Liza", 1, 40, 20, 10);
        check(Math.abs(volumeBox.getVolumetricWeight() - 8000.0 / 3500) < 0.000001,
                "Volumetric weight formula");
        check(volumeBox.getChargeableWeight() == 3, "Larger volumetric weight is used");

        Parcel fragile = new FragileParcel("ABC0005", "Maria", 1.8, 40, 20, 10, "Handle with care");
        check(fragile instanceof BoxParcel, "Fragile extends Box");
        check(fragile.getChargeableWeight() == 3, "Fragile chargeable weight");
        check(fragile.getBaseFee() == 220, "Fragile handling fee");
        fragile.setDeliveryService(new StandardDelivery());
        check(fragile.getDeliveryFee() == 220, "Fragile Standard fee");
        fragile.setDeliveryService(new ExpressDelivery());
        check(fragile.getDeliveryFee() == 330, "Assignment example: Php 330.00");
        fragile.setDeliveryService(new SameDayDelivery());
        check(fragile.getDeliveryFee() == 540, "Fragile Same Day fee");

        Parcel maximumBox = new BoxParcel("ABC0006", "Max", 50, 1, 1, 1);
        Parcel maximumFragile = new FragileParcel("ABC0007", "Max", 30, 1, 1, 1, "Careful");
        check(maximumBox.getChargeableWeight() == 50, "Box maximum weight is allowed");
        check(maximumFragile.getChargeableWeight() == 30, "Fragile maximum weight is allowed");
        BoxParcel largeBox = new BoxParcel("ABC0008", "Large", 1, 50000, 50000, 50000);
        check(largeBox.getVolumetricWeight() > 0, "Dimensions do not overflow as integers");
    }

    private static void checkServiceChanges() throws ParcelException {
        Parcel parcel = new BoxParcel("ABC0010", "Pat", 10, 1, 1, 1);
        parcel.setDeliveryService(new SameDayDelivery());
        check(parcel.getDeliveryFee() == 900, "Exactly 10 kg qualifies for Same Day");

        Parcel heavy = new BoxParcel("ABC0011", "Pat", 10.01, 1, 1, 1);
        DeliveryService oldService = new ExpressDelivery();
        heavy.setDeliveryService(oldService);
        try {
            heavy.setDeliveryService(new SameDayDelivery());
            throw new AssertionError("Heavy Same Day delivery should fail");
        } catch (ParcelException exception) {
            check(exception.getMessage().equals("Same Day delivery is only for parcels up to 10 kg!"),
                    "Same Day rejection message");
        }
        check(heavy.getDeliveryService() == oldService, "Rejected change preserves old strategy");
        check(heavy.getDeliveryFee() == 660, "Rejected change preserves old fee");

        Parcel bulky = new BoxParcel("ABC0012", "Pat", 1, 100, 100, 100);
        try {
            bulky.setDeliveryService(new SameDayDelivery());
            throw new AssertionError("Bulky Same Day delivery should fail");
        } catch (ParcelException exception) {
            check(bulky.getDeliveryService() == null, "Same Day checks chargeable weight, not actual weight");
        }

        check(parcel.getStatus().equals("Pending"), "New parcel is Pending");
        parcel.markAsDelivered();
        check(parcel.getStatus().equals("Delivered"), "Parcel becomes Delivered");
        try {
            parcel.setDeliveryService(new StandardDelivery());
            throw new AssertionError("Delivered service change should fail");
        } catch (ParcelException exception) {
            check(exception.getMessage().equals("Parcel already delivered!"), "Delivered service is locked");
        }
        try {
            parcel.markAsDelivered();
            throw new AssertionError("Second delivery should fail");
        } catch (ParcelException exception) {
            check(exception.getMessage().equals("Parcel was already delivered."), "Repeated delivery message");
        }
    }

    private static void checkValidation() throws ParcelException {
        try {
            new DocumentParcel("abc0001", "Juan", 0.1, 1);
            throw new AssertionError("Invalid tracking number should fail");
        } catch (ParcelException exception) {
            check(true, "Invalid tracking number rejected");
        }
        try {
            new DocumentParcel("ABC0020", "  ", 0.1, 1);
            throw new AssertionError("Blank receiver should fail");
        } catch (ParcelException exception) {
            check(true, "Blank receiver rejected");
        }
        double[] invalidWeights = { 0, -1, Double.NaN, Double.POSITIVE_INFINITY, 0.51 };
        for (double weight : invalidWeights) {
            try {
                new DocumentParcel("ABC0020", "Juan", weight, 1);
                throw new AssertionError("Invalid document weight should fail: " + weight);
            } catch (ParcelException exception) {
                check(true, "Invalid document weight rejected");
            }
        }
        int[] invalidPages = { 0, 101 };
        for (int pages : invalidPages) {
            try {
                new DocumentParcel("ABC0020", "Juan", 0.1, pages);
                throw new AssertionError("Invalid page count should fail");
            } catch (ParcelException exception) {
                check(true, "Invalid page count rejected");
            }
        }
        try {
            new BoxParcel("ABC0021", "Juan", 50.01, 1, 1, 1);
            throw new AssertionError("Overweight box should fail");
        } catch (ParcelException exception) {
            check(true, "Overweight box rejected");
        }
        try {
            new BoxParcel("ABC0021", "Juan", 1, 0, 1, 1);
            throw new AssertionError("Invalid dimension should fail");
        } catch (ParcelException exception) {
            check(true, "Invalid dimension rejected");
        }
        try {
            new FragileParcel("ABC0022", "Juan", 30.01, 1, 1, 1, "Careful");
            throw new AssertionError("Overweight fragile parcel should fail");
        } catch (ParcelException exception) {
            check(true, "Overweight fragile parcel rejected");
        }
        try {
            new FragileParcel("ABC0022", "Juan", 1, 1, 1, 1, "  ");
            throw new AssertionError("Blank handling note should fail");
        } catch (ParcelException exception) {
            check(true, "Blank handling note rejected");
        }
    }

    private static void checkManager() throws ParcelException {
        ParcelManager manager = ParcelManager.getInstance();
        check(manager == ParcelManager.getInstance(), "Singleton returns the same manager");
        Parcel parcel = new DocumentParcel("ABC0030", "Juan", 0.1, 1);
        try {
            manager.addParcel(parcel);
            throw new AssertionError("Parcel without a service should fail");
        } catch (ParcelException exception) {
            check(true, "Service required before adding");
        }
        parcel.setDeliveryService(new StandardDelivery());
        manager.addParcel(parcel);
        check(manager.findParcel("ABC0030") == parcel, "Tracking number lookup");
        check(manager.findParcel("ABC9999") == null, "Unknown tracking number");
        Parcel duplicate = new DocumentParcel("ABC0030", "Ana", 0.2, 2);
        duplicate.setDeliveryService(new StandardDelivery());
        try {
            manager.addParcel(duplicate);
            throw new AssertionError("Duplicate tracking number should fail");
        } catch (ParcelException exception) {
            check(exception.getMessage().equals("Tracking number already exists!"), "Duplicate rejected");
        }
        check(manager.getAllParcels().size() == 1, "Duplicate is not stored");
        manager.getAllParcels().clear();
        check(manager.getAllParcels().size() == 1, "Returned list cannot clear the manager");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
