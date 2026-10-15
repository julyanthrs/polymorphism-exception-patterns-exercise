// Simple checks without a testing framework. Run this separately from Main.
public class ParcelSystemTest {
    private static int checks = 0;

    public static void main(String[] args) {
        checkFees();
        checkRequiredExamples();
        checkServiceChanges();
        checkValidation();
        checkManager();
        System.out.println("All " + checks + " checks passed.");
    }

    private static void checkRequiredExamples() {
        Parcel lightBox = new Box("ABC0040", "Juan", 1, 10, 10, 10);
        lightBox.setDeliveryStrategy(new StandardDelivery());
        check(lightBox.getDeliveryFee() == 40, "Required 1 kg box: Php 40.00");

        Parcel roundedBox = new Box("ABC0041", "Juan", 2.1, 10, 10, 10);
        roundedBox.setDeliveryStrategy(new StandardDelivery());
        check(roundedBox.getDeliveryFee() == 120, "Required 2.1 kg box: Php 120.00");

        Parcel volumeBox = new Box("ABC0042", "Juan", 1, 35, 20, 10);
        volumeBox.setDeliveryStrategy(new StandardDelivery());
        check(volumeBox.getDeliveryFee() == 80, "Required 35 x 20 x 10 box: Php 80.00");

        Parcel sameDayBox = new Box("ABC0043", "Juan", 5, 35, 50, 20);
        sameDayBox.setDeliveryStrategy(new SameDayDelivery());
        check(sameDayBox.getChargeableWeight() == 10, "Required Same Day box: 10 kg");
        check(sameDayBox.getDeliveryFee() == 900, "Required Same Day box: Php 900.00");

        Parcel bulkyBox = new Box("ABC0044", "Juan", 5, 35, 50, 21);
        check(bulkyBox.getChargeableWeight() == 11, "Required rejected Same Day box: 11 kg");
        try {
            bulkyBox.setDeliveryStrategy(new SameDayDelivery());
            throw new AssertionError("Required bulky Same Day box should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Same Day delivery is only for parcels up to 10 kg!"),
                    "Required bulky box rejects Same Day");
        }

        Parcel lightFragile = new Fragile("ABC0045", "Juan", 1, 10, 10, 10, "Careful");
        lightFragile.setDeliveryStrategy(new StandardDelivery());
        check(lightFragile.getDeliveryFee() == 140, "Required fragile parcel: Php 140.00");
    }

    private static void checkFees() {
        Parcel document = new Document("ABC0001", "Juan", 0.5, 100);
        check(document.getChargeableWeight() == 0.5, "Document uses its actual weight");
        check(document.getBaseFee() == 50, "Document has a flat base fee");
        document.setDeliveryStrategy(new StandardDelivery());
        check(document.getDeliveryFee() == 50, "Document Standard fee");
        document.setDeliveryStrategy(new ExpressDelivery());
        check(document.getDeliveryFee() == 75, "Document Express fee");
        document.setDeliveryStrategy(new SameDayDelivery());
        check(document.getDeliveryFee() == 200, "Document Same Day fee");

        Parcel box = new Box("ABC0002", "Ana", 2, 10, 10, 10);
        check(box.getChargeableWeight() == 2, "Whole-number weight stays the same");
        box.setDeliveryStrategy(new StandardDelivery());
        check(box.getDeliveryFee() == 80, "Box Standard fee");
        box.setDeliveryStrategy(new ExpressDelivery());
        check(box.getDeliveryFee() == 120, "Box Express fee");
        box.setDeliveryStrategy(new SameDayDelivery());
        check(box.getDeliveryFee() == 260, "Box Same Day fee");

        Parcel roundedBox = new Box("ABC0003", "Ben", 2.1, 10, 10, 10);
        check(roundedBox.getChargeableWeight() == 3, "Decimal box weight rounds up");
        Box volumeBox = new Box("ABC0004", "Liza", 1, 40, 20, 10);
        check(Math.abs(volumeBox.getVolumetricWeight() - 8000.0 / 3500) < 0.000001,
                "Volumetric weight formula");
        check(volumeBox.getChargeableWeight() == 3, "Larger volumetric weight is used");

        Parcel fragile = new Fragile("ABC0005", "Maria", 1.8, 40, 20, 10, "Handle with care");
        check(fragile instanceof Box, "Fragile extends Box");
        check(fragile.getChargeableWeight() == 3, "Fragile chargeable weight");
        check(fragile.getBaseFee() == 220, "Fragile handling fee");
        fragile.setDeliveryStrategy(new StandardDelivery());
        check(fragile.getDeliveryFee() == 220, "Fragile Standard fee");
        fragile.setDeliveryStrategy(new ExpressDelivery());
        check(fragile.getDeliveryFee() == 330, "Assignment example: Php 330.00");
        fragile.setDeliveryStrategy(new SameDayDelivery());
        check(fragile.getDeliveryFee() == 540, "Fragile Same Day fee");

        Parcel maximumBox = new Box("ABC0006", "Max", 50, 1, 1, 1);
        Parcel maximumFragile = new Fragile("ABC0007", "Max", 30, 1, 1, 1, "Careful");
        check(maximumBox.getChargeableWeight() == 50, "Box maximum weight is allowed");
        check(maximumFragile.getChargeableWeight() == 30, "Fragile maximum weight is allowed");
        Box largeBox = new Box("ABC0008", "Large", 1, 50000, 50000, 50000);
        check(largeBox.getVolumetricWeight() > 0, "Dimensions do not overflow as integers");
    }

    private static void checkServiceChanges() {
        Parcel parcel = new Box("ABC0010", "Pat", 10, 1, 1, 1);
        parcel.setDeliveryStrategy(new SameDayDelivery());
        check(parcel.getDeliveryFee() == 900, "Exactly 10 kg qualifies for Same Day");

        Parcel heavy = new Box("ABC0011", "Pat", 10.01, 1, 1, 1);
        DeliveryStrategy oldService = new ExpressDelivery();
        heavy.setDeliveryStrategy(oldService);
        try {
            heavy.setDeliveryStrategy(new SameDayDelivery());
            throw new AssertionError("Heavy Same Day delivery should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Same Day delivery is only for parcels up to 10 kg!"),
                    "Same Day rejection message");
        }
        check(heavy.getDeliveryStrategy() == oldService, "Rejected change preserves old strategy");
        check(heavy.getDeliveryFee() == 660, "Rejected change preserves old fee");

        DeliveryStrategy sameService = heavy.getDeliveryStrategy();
        try {
            heavy.setDeliveryStrategy(new ExpressDelivery());
            throw new AssertionError("Changing to the current service should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("This parcel already uses Express delivery service!"),
                    "Same delivery service rejection message");
        }
        check(heavy.getDeliveryStrategy() == sameService, "Same service rejection preserves the strategy");
        check(heavy.getDeliveryFee() == 660, "Same service rejection preserves the fee");

        Parcel bulky = new Box("ABC0012", "Pat", 1, 100, 100, 100);
        try {
            bulky.setDeliveryStrategy(new SameDayDelivery());
            throw new AssertionError("Bulky Same Day delivery should fail");
        } catch (IllegalArgumentException exception) {
            check(bulky.getDeliveryStrategy() == null, "Same Day checks chargeable weight, not actual weight");
        }

        check(parcel.getStatus().equals("Pending"), "New parcel is Pending");
        parcel.markAsDelivered();
        check(parcel.getStatus().equals("Delivered"), "Parcel becomes Delivered");
        try {
            parcel.setDeliveryStrategy(new StandardDelivery());
            throw new AssertionError("Delivered service change should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Parcel already delivered!"), "Delivered service is locked");
        }
        try {
            parcel.markAsDelivered();
            throw new AssertionError("Second delivery should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Parcel was already delivered."), "Repeated delivery message");
        }
    }

    private static void checkValidation() {
        String[] invalidTrackingNumbers = { "", "ABC123", "ABC12345", "XYZ0001", "ABC00 1", "ABC00!1" };
        for (String trackingNumber : invalidTrackingNumbers) {
            try {
                new Document(trackingNumber, "Juan", 0.1, 1);
                throw new AssertionError("Invalid tracking format should fail: " + trackingNumber);
            } catch (IllegalArgumentException exception) {
                check(true, "Invalid tracking format rejected");
            }
        }
        Document namedDocument = new Document("ABC0020", "  Ana-Maria O'Brien  ", 0.1, 1);
        check(namedDocument.getReceiverName().equals("Ana-Maria O'Brien"), "Ordinary names are trimmed and preserved");
        try {
            new Document("abc0001", "Juan", 0.1, 1);
            throw new AssertionError("Invalid tracking number should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Invalid tracking number rejected");
        }
        try {
            new Document("ABC0020", "  ", 0.1, 1);
            throw new AssertionError("Blank receiver should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Blank receiver rejected");
        }
        double[] invalidWeights = { 0, -1, Double.NaN, Double.POSITIVE_INFINITY, 0.51 };
        for (double weight : invalidWeights) {
            try {
                new Document("ABC0020", "Juan", weight, 1);
                throw new AssertionError("Invalid document weight should fail: " + weight);
            } catch (IllegalArgumentException exception) {
                check(true, "Invalid document weight rejected");
            }
        }
        int[] invalidPages = { 0, 101 };
        for (int pages : invalidPages) {
            try {
                new Document("ABC0020", "Juan", 0.1, pages);
                throw new AssertionError("Invalid page count should fail");
            } catch (IllegalArgumentException exception) {
                check(true, "Invalid page count rejected");
            }
        }
        try {
            new Box("ABC0021", "Juan", 50.01, 1, 1, 1);
            throw new AssertionError("Overweight box should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Overweight box rejected");
        }
        try {
            new Box("ABC0021", "Juan", 1, 0, 1, 1);
            throw new AssertionError("Invalid dimension should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Invalid dimension rejected");
        }
        try {
            new Fragile("ABC0022", "Juan", 30.01, 1, 1, 1, "Careful");
            throw new AssertionError("Overweight fragile parcel should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Overweight fragile parcel rejected");
        }
        try {
            new Fragile("ABC0022", "Juan", 1, 1, 1, 1, "  ");
            throw new AssertionError("Blank handling note should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Blank handling note rejected");
        }
    }

    private static void checkManager() {
        ParcelManager manager = ParcelManager.getInstance();
        check(manager == ParcelManager.getInstance(), "Singleton returns the same manager");
        check(manager.hasNoParcels(), "Manager starts with no parcels");
        try {
            manager.changeDeliveryService("ABC0030", new StandardDelivery());
            throw new AssertionError("Empty parcel list should reject a service change");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("No parcels available! Add a parcel first."),
                    "Empty parcel list service-change message");
        }
        try {
            manager.markAsDelivered("ABC0030");
            throw new AssertionError("Empty parcel list should reject delivery");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("No parcels available! Add a parcel first."),
                    "Empty parcel list delivery message");
        }
        Parcel parcel = new Document("ABC0030", "Juan", 0.1, 1);
        try {
            manager.addParcel(parcel);
            throw new AssertionError("Parcel without a service should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Service required before adding");
        }
        parcel.setDeliveryStrategy(new StandardDelivery());
        manager.addParcel(parcel);
        check(manager.findParcel("ABC0030") == parcel, "Tracking number lookup");
        check(manager.findParcel("ABC9999") == null, "Unknown tracking number");
        Parcel duplicate = new Document("ABC0030", "Ana", 0.2, 2);
        duplicate.setDeliveryStrategy(new StandardDelivery());
        try {
            manager.addParcel(duplicate);
            throw new AssertionError("Duplicate tracking number should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Tracking number already exists!"), "Duplicate rejected");
        }
        check(manager.getAllParcels().size() == 1, "Duplicate is not stored");
        manager.getAllParcels().clear();
        check(manager.getAllParcels().size() == 1, "Returned list cannot clear the manager");

        check(manager.changeDeliveryService("ABC0030", new ExpressDelivery()) == 75,
                "Manager changes service and returns the new fee");
        DeliveryStrategy oldService = parcel.getDeliveryStrategy();
        try {
            manager.changeDeliveryService("ABC0030", null);
            throw new AssertionError("Empty strategy should fail");
        } catch (IllegalArgumentException exception) {
            check(parcel.getDeliveryStrategy() == oldService, "Empty strategy preserves the old service");
        }
        check(parcel.getDeliveryFee() == 75, "Empty strategy preserves the old fee");

        manager.markAsDelivered("ABC0030");
        check(parcel.isDelivered(), "Manager marks a parcel Delivered");
        try {
            manager.changeDeliveryService("ABC0030", new StandardDelivery());
            throw new AssertionError("Manager should reject a delivered service change");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Parcel already delivered!"), "Manager protects delivered parcels");
        }
        check(parcel.getDeliveryFee() == 75, "Delivered fee is preserved");
        try {
            manager.markAsDelivered("ABC0030");
            throw new AssertionError("Manager should reject repeated delivery");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Parcel was already delivered."), "Manager repeated-delivery message");
        }
        try {
            manager.addParcel(duplicate);
            throw new AssertionError("Delivered tracking number should remain unique");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Tracking number already exists!"), "Delivered tracking number remains unique");
        }
        try {
            manager.changeDeliveryService("ABC9999", new StandardDelivery());
            throw new AssertionError("Missing parcel service change should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Parcel not found!"), "Manager handles missing service-change parcel");
        }
        try {
            manager.markAsDelivered("ABC9999");
            throw new AssertionError("Missing parcel delivery should fail");
        } catch (IllegalArgumentException exception) {
            check(exception.getMessage().equals("Parcel not found!"), "Manager handles missing delivery parcel");
        }
        try {
            manager.addParcel(null);
            throw new AssertionError("Null parcel should fail");
        } catch (IllegalArgumentException exception) {
            check(true, "Null parcel rejected");
        }
        Parcel alreadyDelivered = new Document("ABC0031", "Ana", 0.1, 1);
        alreadyDelivered.setDeliveryStrategy(new StandardDelivery());
        alreadyDelivered.markAsDelivered();
        try {
            manager.addParcel(alreadyDelivered);
            throw new AssertionError("New parcel must be Pending");
        } catch (IllegalArgumentException exception) {
            check(manager.getAllParcels().size() == 1, "Invalid additions do not change stored parcels");
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
