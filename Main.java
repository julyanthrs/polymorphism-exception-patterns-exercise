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
                choice = readMenuChoice();
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
        System.out.println("\n========================================");
        System.out.println("          PARCEL DELIVERY SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Add Parcel");
        System.out.println("2. View All Parcels");
        System.out.println("3. Change Delivery Service");
        System.out.println("4. Mark as Delivered");
        System.out.println("5. Exit");
        System.out.println("========================================");
    }

    private static int readMenuChoice() {
        System.out.print("Enter choice: ");
        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (!input.matches("[0-9]+") || choice < 1 || choice > 5) {
                System.out.println("Invalid choice! Enter a whole number from 1 to 5.");
                return 0;
            }
            return choice;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid choice! Enter a whole number from 1 to 5.");
            return 0;
        }
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
        } catch (IllegalArgumentException exception) {
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
                                       double weight) {
        if (type == 1) {
            int pages = readInteger("Number of pages (1-100): ", 1, 100);
            return new Document(trackingNumber, receiverName, weight, pages);
        }

        int length = readInteger("Length in cm: ", 1, Integer.MAX_VALUE);
        int width = readInteger("Width in cm: ", 1, Integer.MAX_VALUE);
        int height = readInteger("Height in cm: ", 1, Integer.MAX_VALUE);

        if (type == 2) {
            return new Box(trackingNumber, receiverName, weight, length, width, height);
        }

        String handlingNote = readText("Handling note: ");
        return new Fragile(trackingNumber, receiverName, weight, length, width, height, handlingNote);
    }

    private static DeliveryStrategy readDeliveryStrategy() {
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
                parcel.setDeliveryStrategy(readDeliveryStrategy());
                serviceSelected = true;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        } while (!serviceSelected);
    }

    private static void viewAllParcels() {
        ArrayList<Parcel> parcels = manager.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("No parcels available!");
            return;
        }

        int receiverWidth = "Receiver".length();
        for (Parcel parcel : parcels) {
            receiverWidth = Math.max(receiverWidth, parcel.getReceiverName().length());
        }
        String rowFormat = "%-12s | %-11s | %-" + receiverWidth + "s | %16s | %-16s | %18s | %-9s%n";
        System.out.println();
        System.out.printf(rowFormat, "Tracking No.", "Parcel Type", "Receiver", "Chargeable kg", "Delivery Service", "Delivery Fee", "Status");
        // Each Parcel reference uses the actual object's overridden methods.
        for (Parcel parcel : parcels) {
            System.out.printf(rowFormat, parcel.getTrackingNumber(), parcel.getType(), parcel.getReceiverName(),
                    String.valueOf(parcel.getChargeableWeight()), parcel.getDeliveryStrategy().getName(),
                    String.format("Php %.2f", parcel.getDeliveryFee()), parcel.getStatus());
        }
    }

    private static void changeDeliveryService() {
        if (showEmptyParcelError()) {
            return;
        }
        String trackingNumber = readExistingTrackingNumber();
        try {
            Parcel parcel = manager.getPendingParcel(trackingNumber);
            System.out.println("Current delivery service: " + parcel.getDeliveryStrategy().getName());
            DeliveryStrategy service = readDeliveryStrategy();
            double newFee = manager.changeDeliveryService(trackingNumber, service);
            System.out.printf("Delivery service changed! New fee: Php %.2f%n", newFee);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void markAsDelivered() {
        if (showEmptyParcelError()) {
            return;
        }
        String trackingNumber = readExistingTrackingNumber();
        try {
            manager.markAsDelivered(trackingNumber);
            System.out.println("Parcel delivered successfully!");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static boolean showEmptyParcelError() {
        if (manager.hasNoParcels()) {
            System.out.println("No parcels available! Add a parcel first.");
            return true;
        }
        return false;
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

    private static String readExistingTrackingNumber() {
        String trackingNumber;
        boolean valid = false;
        do {
            trackingNumber = readText("Tracking number: ");
            if (!trackingNumber.matches("ABC[0-9]{4}")) {
                System.out.println("Tracking number must be ABC followed by 4 digits (example: ABC0001).");
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
                if (!input.matches("[0-9]+")) {
                    System.out.println("Please enter digits only for a positive whole number!");
                    continue;
                }
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
            if (!input.matches("[0-9]*\\.?[0-9]+")) {
                System.out.println("Enter a positive decimal weight using digits and an optional decimal point!");
                continue;
            }
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
