import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ParcelManager manager = ParcelManager.getInstance();

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        int choice = 0;

        try {
            do {
                printMenu();
                choice = readInteger("Choose an option: ", 1, 5);
                switch (choice) {
                    case 1:
                        addParcel();
                        break;
                    case 2:
                        viewAllParcels();
                        break;
                    case 3:
                        changeDeliveryService();
                        break;
                    case 4:
                        markAsDelivered();
                        break;
                    case 5:
                        System.out.println("Thank you for using the Parcel Delivery System!");
                        break;
                }
            } while (choice != 5);
        } catch (NoSuchElementException exception) {
            System.out.println("\nInput ended. Closing the Parcel Delivery System.");
        } finally {
            scanner.close();
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Parcel Delivery System ===");
        System.out.println("1. Add Parcel");
        System.out.println("2. View All Parcels");
        System.out.println("3. Change Delivery Service");
        System.out.println("4. Mark as Delivered");
        System.out.println("5. Exit");
    }

    private static void addParcel() {
        System.out.println("\nParcel type: 1. Document  2. Box  3. Fragile");
        int type = readInteger("Select parcel type: ", 1, 3);
        String trackingNumber = readNewTrackingNumber();
        String receiverName = readText("Receiver name: ");
        double maximumWeight = getMaximumWeight(type);
        double weight = readWeight(maximumWeight);

        try {
            Parcel parcel = createParcel(type, trackingNumber, receiverName, weight);
            selectInitialService(parcel);
            manager.addParcel(parcel);
            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n", parcel.getDeliveryFee());
        } catch (ParcelException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static double getMaximumWeight(int type) {
        switch (type) {
            case 1:
                return 0.5;
            case 2:
                return 50;
            default:
                return 30;
        }
    }

    private static Parcel createParcel(int type, String trackingNumber, String receiverName,
                                       double weight) throws ParcelException {
        if (type == 1) {
            int pages = readInteger("Number of pages (1-100): ", 1, 100);
            return new DocumentParcel(trackingNumber, receiverName, weight, pages);
        }

        int length = readInteger("Length in cm: ", 1, Integer.MAX_VALUE);
        int width = readInteger("Width in cm: ", 1, Integer.MAX_VALUE);
        int height = readInteger("Height in cm: ", 1, Integer.MAX_VALUE);

        if (type == 2) {
            return new BoxParcel(trackingNumber, receiverName, weight, length, width, height);
        }

        String handlingNote = readText("Handling note: ");
        return new FragileParcel(trackingNumber, receiverName, weight, length, width, height, handlingNote);
    }

    private static DeliveryService readDeliveryService() {
        System.out.println("Delivery service: 1. Standard  2. Express  3. Same Day");
        int service = readInteger("Select delivery service: ", 1, 3);
        switch (service) {
            case 1:
                return new StandardDelivery();
            case 2:
                return new ExpressDelivery();
            default:
                return new SameDayDelivery();
        }
    }

    private static void selectInitialService(Parcel parcel) {
        boolean serviceSelected = false;
        do {
            try {
                parcel.setDeliveryService(readDeliveryService());
                serviceSelected = true;
            } catch (ParcelException exception) {
                System.out.println(exception.getMessage());
            }
        } while (!serviceSelected);
    }

    private static void viewAllParcels() {
        ArrayList<Parcel> parcels = manager.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("No parcels yet.");
            return;
        }

        int receiverWidth = "Receiver".length();
        for (Parcel parcel : parcels) {
            receiverWidth = Math.max(receiverWidth, parcel.getReceiverName().length());
        }
        String rowFormat = "%-10s | %-8s | %-" + receiverWidth + "s | %13s | %-8s | %18s | %-9s%n";
        System.out.println();
        System.out.printf(rowFormat, "Tracking", "Type", "Receiver", "Chargeable kg", "Service", "Delivery fee", "Status");
        // Each Parcel reference uses the actual object's overridden methods.
        for (Parcel parcel : parcels) {
            System.out.printf(rowFormat, parcel.getTrackingNumber(), parcel.getType(), parcel.getReceiverName(),
                    String.valueOf(parcel.getChargeableWeight()), parcel.getDeliveryService().getName(),
                    String.format("Php %.2f", parcel.getDeliveryFee()), parcel.getStatus());
        }
    }

    private static void changeDeliveryService() {
        String trackingNumber = readText("Tracking number: ");
        Parcel parcel = manager.findParcel(trackingNumber);
        if (parcel == null) {
            System.out.println("Parcel not found!");
            return;
        }
        if (parcel.isDelivered()) {
            System.out.println("Parcel already delivered!");
            return;
        }

        try {
            parcel.setDeliveryService(readDeliveryService());
            System.out.printf("Delivery service changed! New fee: Php %.2f%n", parcel.getDeliveryFee());
        } catch (ParcelException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void markAsDelivered() {
        String trackingNumber = readText("Tracking number: ");
        Parcel parcel = manager.findParcel(trackingNumber);
        if (parcel == null) {
            System.out.println("Parcel not found!");
            return;
        }
        try {
            parcel.markAsDelivered();
            System.out.println("Parcel delivered successfully!");
        } catch (ParcelException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static String readNewTrackingNumber() {
        String trackingNumber;
        boolean valid = false;
        do {
            trackingNumber = readText("Tracking number (ABC0001): ");
            if (!trackingNumber.matches("ABC[0-9]{4}")) {
                System.out.println("Tracking number must be ABC followed by 4 digits (example: ABC0001).");
                continue;
            }
            if (manager.findParcel(trackingNumber) != null) {
                System.out.println("Tracking number already exists!");
                continue;
            }
            valid = true;
        } while (!valid);
        return trackingNumber;
    }

    private static String readText(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("This field cannot be empty!");
            }
        } while (value.isEmpty());
        return value;
    }

    private static int readInteger(String prompt, int minimum, int maximum) {
        int value = 0;
        boolean valid = false;
        do {
            String input = readText(prompt);
            try {
                value = Integer.parseInt(input);
                if (value < minimum || value > maximum) {
                    System.out.printf("Enter a whole number from %d to %d!%n", minimum, maximum);
                    continue;
                }
                valid = true;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number!");
            }
        } while (!valid);
        return value;
    }

    private static double readWeight(double maximum) {
        double weight = 0;
        boolean valid = false;
        do {
            String input = readText("Weight in kg (maximum " + maximum + "): ");
            try {
                weight = Double.parseDouble(input);
                if (!Double.isFinite(weight) || weight <= 0 || weight > maximum) {
                    System.out.println("Weight must be greater than 0 and at most " + maximum + " kg!");
                    continue;
                }
                valid = true;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid weight!");
            }
        } while (!valid);
        return weight;
    }
}
