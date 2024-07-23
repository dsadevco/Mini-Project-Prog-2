import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class DagmayWeaverMonitorouput {
    static boolean loggedIn = false;
    static String loggedInUser = "";
    static String loggedInUserRole = "";
    static ArrayList<User> users = new ArrayList<>(); // ArrayList to store User objects
    static ArrayList<DagmayItem> dagmayItems = new ArrayList<>(); // ArrayList to store Dagmay items
    // Declaration of salesRecords ArrayList
    static ArrayList<Sale> salesRecords = new ArrayList<>();
    static HashMap<Integer, Integer> shoppingCart = new HashMap<>(); // Cart to store item IDs and quantities
    static ArrayList<StaffAddItem> itemsAddedByStaff = new ArrayList<>();

    public static void main(String[] args) {
        welcomeMessage();
    }

    static void welcomeMessage() {
        System.out.println("\t+----------------------------------------------------------------+");
        System.out.println("\t|        WELCOME TO DAVAO ORIENTAL DAGMAY WEAVERS DIRECTORY      |");
        System.out.println("\t|                 AND OUTPUT MONITORING PORTAL                   |");
        System.out.println("\t+----------------------------------------------------------------+");
        loadUsers(); // Load users from file at the start
        loadDagmayItems(); // Load Dagmay items from file at the start
        loadStaffAddedItems(); // Load staff added items from file
        loadSalesRecords(); // Load sales records from file
        askIfUserHasAccount();
    }

    static void loadUsers() {
        users.clear();
        try (Scanner scanner = new Scanner(new File("UserCredentials.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch ( FileNotFoundException e ) {
            System.out.println("User credentials file not found. Creating a new one.");
            try {
                new File("UserCredentials.txt").createNewFile();
            } catch ( IOException ex ) {
                System.out.println("Error creating file: " + ex.getMessage());
            }
        }
    }

    static void loadDagmayItems() {
        dagmayItems.clear();
        try (Scanner scanner = new Scanner(new File("DagmayItems.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        int itemId = Integer.parseInt(parts[0]);
                        String itemName = parts[1];
                        double price = Double.parseDouble(parts[2]);
                        String description = parts[3];
                        int stock = Integer.parseInt(parts[4]);
                        dagmayItems.add(new DagmayItem(itemId, itemName, price, description, stock));
                    } catch ( NumberFormatException e ) {
                        System.out.println("Error reading Dagmay item: " + e.getMessage());
                    }
                }
            }
        } catch ( FileNotFoundException e ) {
            System.out.println("Dagmay items file not found. Creating a new one.");
            try {
                new File("DagmayItems.txt").createNewFile();
            } catch ( IOException ex ) {
                System.out.println("Error creating file: " + ex.getMessage());
            }
        }
    }

    static void askIfUserHasAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you have an account? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            askToLogin();
        } else if (choice.equalsIgnoreCase("N")) {
            askToSignup();
        } else {
            System.out.println("Invalid input. Please try again.");
            askIfUserHasAccount();
        }
    }

    static void displayMenu() {
        if (!loggedIn) {
            System.out.println("You must be logged in to access the menu.");
            return;
        }

        // Display the menu based on the user's role
        switch (loggedInUserRole.toLowerCase()) {
            case "customer" -> {
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println("\t|                      CUSTOMER MAIN MENU                        |");
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println("\t|  A. List of Dagmay Items                                       |");
                System.out.println("\t|  B. Buy Dagmay Items                                           |");
                System.out.println("\t|  C. View Cart                                                  |");
                System.out.println("\t|  D. Logout                                                     |");
                System.out.println("\t|  E. Quit                                                       |");
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println();
                handleUserInput();
            }
            case "staff" -> {
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println("\t|                        STAFF MAIN MENU                         |");
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println("\t|  A. List of Dagmay Items                                       |");
                System.out.println("\t|  B. Add/Update Dagmay Items                                    |");
                System.out.println("\t|  C. Logout                                                     |");
                System.out.println("\t|  D. Quit                                                       |");
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println();
                handleUserInput();
            }
            case "admin" -> {
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println("\t|                        ADMIN MAIN MENU                         |");
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println("\t|  A. List of Dagmay Items                                       |");
                System.out.println("\t|  B. Add/Update Dagmay Items                                    |");
                System.out.println("\t|  C. Delete Dagmay Items                                        |");
                System.out.println("\t|  D. Monitor output                                             |");
                System.out.println("\t|  E. Logout                                                     |");
                System.out.println("\t|  F. Quit                                                       |");
                System.out.println("\t+----------------------------------------------------------------+");
                System.out.println();
                handleUserInput();
            }
            default -> {
                System.out.println("Invalid user role. Logging out...");
                logout();
            }
        }
    }

    private static void handleUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your choice: ");
        char choice = scanner.next().charAt(0);
        scanner.nextLine(); // consume newline character

        // Handle menu choices based on user role
        switch (loggedInUserRole.toLowerCase()) {
            case "customer" -> {
                switch (choice) {
                    case 'a', 'A' -> listDagmayItems(loggedInUserRole);
                    case 'b', 'B' -> buyDagmayItems();
                    case 'c', 'C' -> viewCart();
                    case 'd', 'D' -> askToLogout();
                    case 'e', 'E' -> quitProgram();
                    default -> {
                        System.out.println("Invalid input. Please try again.");
                        handleUserInput();
                    }
                }
            }
            case "staff" -> {
                switch (choice) {
                    case 'a', 'A' -> listDagmayItems(loggedInUserRole);
                    case 'b', 'B' -> {
                        System.out.println("1. Add Dagmay Item");
                        System.out.println("2. Update Dagmay Item");
                        System.out.print("Enter your choice (1-2): ");
                        int subChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (subChoice == 1) {
                            // Create a new Scanner for this section
                            System.out.print("Enter item name: ");
                            String itemName = scanner.nextLine();

                            double price = 0.0;
                            while (true) {
                                System.out.print("Enter price: ");
                                if (scanner.hasNextDouble()) {
                                    price = scanner.nextDouble();
                                    if (price > 0) {
                                        break; // Price is valid, exit the loop
                                    } else {
                                        System.out.println("Price must be positive. Please try again.");
                                        scanner.nextLine(); // Consume the leftover input
                                    }
                                } else {
                                    System.out.println("Invalid price input. Please enter a number.");
                                    scanner.next(); // Consume the invalid input
                                }
                            }

                            scanner.nextLine(); // Consume the leftover newline
                            System.out.print("Enter description: ");
                            String description = scanner.nextLine();

                            addDagmayItem(itemName, price, description);
                        } else if (subChoice == 2) {
                            System.out.print("Enter the ID of the item to update: ");
                            int itemId = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Enter new item name: ");
                            String itemName = scanner.nextLine();

                            double price = 0.0;
                            while (true) {
                                System.out.print("Enter new price: ");
                                if (scanner.hasNextDouble()) {
                                    price = scanner.nextDouble();
                                    if (price > 0) {
                                        break;
                                    } else {
                                        System.out.println("Price must be positive. Please try again.");
                                        scanner.nextLine();
                                    }
                                } else {
                                    System.out.println("Invalid price input. Please enter a number.");
                                    scanner.next();
                                }
                            }

                            scanner.nextLine(); // Consume leftover newline
                            System.out.print("Enter new description: ");
                            String description = scanner.nextLine();

                            updateDagmayItem(itemId, itemName, price, description);
                        } else {
                            System.out.println("Invalid choice.");
                        }
                    }
                    case 'c', 'C' -> askToLogout();
                    case 'd', 'D' -> quitProgram();
                    default -> {
                        System.out.println("Invalid input. Please try again.");
                        handleUserInput();
                    }
                }
            }
            case "admin" -> {
                switch (choice) {
                    case 'a', 'A' -> listDagmayItems(loggedInUserRole);
                    case 'b', 'B' -> {
                        System.out.println("1. Add Dagmay Item");
                        System.out.println("2. Update Dagmay Item");
                        System.out.print("Enter your choice (1-2): ");
                        int subChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (subChoice == 1) {
                            System.out.print("Enter item name: ");
                            String itemName = scanner.nextLine();

                            double price = 0.0;
                            while (true) {
                                System.out.print("Enter price: ");
                                if (scanner.hasNextDouble()) {
                                    price = scanner.nextDouble();
                                    if (price > 0) {
                                        break; // Price is valid, exit the loop
                                    } else {
                                        System.out.println("Price must be positive. Please try again.");
                                        scanner.nextLine(); // Consume the leftover input
                                    }
                                } else {
                                    System.out.println("Invalid price input. Please enter a number.");
                                    scanner.next(); // Consume the invalid input
                                }
                            }

                            scanner.nextLine(); // Consume the leftover newline
                            System.out.print("Enter description: ");
                            String description = scanner.nextLine();

                            addDagmayItem(itemName, price, description);
                        } else if (subChoice == 2) {
                            System.out.print("Enter the ID of the item to update: ");
                            int itemId = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Enter new item name: ");
                            String itemName = scanner.nextLine();

                            double price = 0.0;
                            while (true) {
                                System.out.print("Enter new price: ");
                                if (scanner.hasNextDouble()) {
                                    price = scanner.nextDouble();
                                    if (price > 0) {
                                        break;
                                    } else {
                                        System.out.println("Price must be positive. Please try again.");
                                        scanner.nextLine();
                                    }
                                } else {
                                    System.out.println("Invalid price input. Please enter a number.");
                                    scanner.next();
                                }
                            }

                            scanner.nextLine(); // Consume leftover newline
                            System.out.print("Enter new description: ");
                            String description = scanner.nextLine();

                            updateDagmayItem(itemId, itemName, price, description);
                        } else {
                            System.out.println("Invalid choice.");
                        }
                    }
                    case 'c', 'C' -> {
                        System.out.print("Enter the ID of the item to delete: ");
                        int itemId = scanner.nextInt();
                        scanner.nextLine();
                        deleteDagmayItem(itemId);
                    }
                    case 'd', 'D' -> monitorOutput();
                    case 'e', 'E' -> askToLogout();
                    case 'f', 'F' -> quitProgram();
                    default -> {
                        System.out.println("Invalid input. Please try again.");
                        handleUserInput();
                    }
                }
            }
            default -> {
                System.out.println("Invalid user role. Logging out...");
                logout();
            }
        }
    }

    static boolean askToContinue() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nDo you want to continue? (Y/N): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                return true;
            } else if (choice.equalsIgnoreCase("N")) {
                returnToMainMenu();
                return false;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void returnToMainMenu() {
        if (loggedIn) {
            displayMenu();
        } else {
            welcomeMessage();
        }
    }

    static void quitProgram() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you sure you want to quit? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            // Save the customer's cart before quitting
            if (loggedInUserRole.equalsIgnoreCase("customer")) {
                saveCustomerCart(loggedInUser);
                shoppingCart.clear(); // Clear the shopping cart after saving
            }

            System.out.println("Quitting...");
            System.exit(0);
        } else if (choice.equalsIgnoreCase("N")) {
            askToContinue();
        } else {
            System.out.println("Invalid input. Please try again.");
            quitProgram();
        }
    }

    static void askToLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to log in? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            login();
        } else if (choice.equalsIgnoreCase("N")) {
            quitProgram();
        } else {
            System.out.println("Invalid input. Please try again.");
            askToLogin();
        }
    }

    static void login() {
        Scanner scanner = new Scanner(System.in);
        String username, password;

        System.out.print("Enter your username: ");
        username = scanner.nextLine();

        System.out.print("Enter your password: ");
        password = scanner.nextLine();

        if (authenticateUser(username, password)) {
            System.out.printf("Welcome, %s!\n", username);
            loadCustomerCart(username);
            displayMenu();
        } else {
            System.out.println("Invalid username or password. Please try again.");
            askToLogin();
        }
    }

    static boolean authenticateUser(String username, String password) {
        for (DagmayWeaverMonitorouput.User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                loggedIn = true;
                loggedInUser = username;
                loggedInUserRole = user.role;

                System.out.println("Successfully logged in as " + loggedInUserRole);
                return true;
            }
        }
        return false;
    }

    static void askToLogout() {
        if (loggedIn) {
            System.out.printf("User %s is logged in.\n", loggedInUser);
            System.out.println("Are you sure you want to log out? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String confirmLogout = scanner.nextLine();
            if (confirmLogout.equalsIgnoreCase("Y")) {
                logout();
            } else if (confirmLogout.equalsIgnoreCase("N")) {
                returnToMainMenu();
            } else {
                System.out.println("Invalid input. Please try again.");
                askToLogout();
            }
        }
    }

    static void logout() {
        loggedIn = false;
        loggedInUser = "";
        loggedInUserRole = "";
        System.out.println("You have been logged out.");
        welcomeMessage();
    }

//    static void forgotPassword() {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Forgot Password? (Y/N): ");
//        String choice = scanner.nextLine();
//
//        if (choice.equalsIgnoreCase("Y")) {
//            createNewPassword();
//        } else if (choice.equalsIgnoreCase("N")) {
//            askToContinue();
//            login();
//        } else {
//            System.out.println("Invalid input. Please try again.");
//            forgotPassword();
//        }
//    }
//
//    private static void createNewPassword() {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("\n+---------------------------------------+");
//        System.out.println("|            RECOVER PASSWORD           |");
//        System.out.println("+---------------------------------------+");
//
//        System.out.print("Enter your username: ");
//        String username = scanner.nextLine();
//        System.out.print("Enter your email address: ");
//        String email = scanner.nextLine();
//
//        boolean isUserFound = false;
//        for (int i = 0; i < users.size(); i++) {
//            DagmayWeaverMonitorouput.User user = users.get(i);
//            if (user.username.equals(username) && user.email.equals(email)) {
//                isUserFound = true;
//                String newPassword;
//                String confirmPassword;
//                while (true) {
//                    System.out.print("Enter your new password: ");
//                    newPassword = scanner.nextLine();
//                    System.out.print("Confirm your new password: ");
//                    confirmPassword = scanner.nextLine();
//
//                    if (newPassword.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
//                        if (newPassword.equals(confirmPassword)) {
//                            user.password = newPassword;
//                            break;
//                        } else {
//                            System.out.println("Passwords do not match. Please try again.");
//                        }
//                    } else {
//                        System.out.println("Invalid password. Password must contain at least a lowercase/uppercase and a numeric character.");
//                        System.out.println("Password must be in alphanumeric characters (A-Z, a-z, 0-9) and no special characters.");
//                    }
//                }
//                // Update user in the list
//                users.set(i, user);
//                saveUsers();
//                System.out.println("Password updated successfully.");
//                break;
//            }
//        }
//
//        if (!isUserFound) {
//            System.out.println("User not found. Please try again.");
//        }
//    }

    static void askToSignup() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to sign up? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            signup();
        } else if (choice.equalsIgnoreCase("N")) {
            askToContinue();
            askIfUserHasAccount();
        } else {
            System.out.println("Invalid input. Please try again.");
            askToSignup();
        }
    }

    static void signup() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|                SIGNUP                 |");
        System.out.println("\t+---------------------------------------+");

        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine();

        String email = "";
        while (true) {
            System.out.print("Enter your email address: ");
            email = scanner.nextLine();

            if (email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                break;
            } else {
                System.out.println("Invalid email address. Please try again.");
            }
        }
        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        String password = "";
        String confirmPassword = "";
        while (true) {
            System.out.print("Enter your password (alphanumeric characters only): ");
            password = scanner.nextLine();
            System.out.print("Confirm your password: ");
            confirmPassword = scanner.nextLine();

            if (password.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
                if (password.equals(confirmPassword)) {
                    break;
                } else {
                    System.out.println("Passwords do not match. Please try again.");
                }
            } else {
                System.out.println("Invalid password. Password must contain at least a lowercase/uppercase and a numeric character.");
                System.out.println("Password must be in alphanumeric characters (A-Z, a-z, 0-9) and no special characters.");
            }
        }
        // Ask for the user's role
        String role = "";
        while (true) {
            System.out.print("Enter your role (Customer, Staff, Admin): ");
            role = scanner.nextLine();
            if (role.equalsIgnoreCase("Customer") || role.equalsIgnoreCase("Staff") || role.equalsIgnoreCase("Admin")) {
                break;
            } else {
                System.out.println("Invalid role. Please enter Customer, Staff, or Admin.");
            }
        }
        // Implement password code for Staff and Admin
        if (role.equalsIgnoreCase("Staff")) {
            String staffPasswordCode = "";
            while (true) {
                System.out.print("Enter the Staff password code: ");
                staffPasswordCode = scanner.nextLine();
                if (staffPasswordCode.equals("StaffCode")) { // Replace "StaffCode" with your actual Staff code
                    break;
                } else {
                    System.out.println("Incorrect Staff password code. Try again.");
                }
            }
        } else if (role.equalsIgnoreCase("Admin")) {
            String adminPasswordCode = "";
            while (true) {
                System.out.print("Enter the Admin password code: ");
                adminPasswordCode = scanner.nextLine();
                if (adminPasswordCode.equals("AdminCode")) { // Replace "AdminCode" with your actual Admin code
                    break;
                } else {
                    System.out.println("Incorrect Admin password code. Try again.");
                }
            }
        }

        // Create a new user
        DagmayWeaverMonitorouput.User newUser = new DagmayWeaverMonitorouput.User(username, password, email, role);
        users.add(newUser);
        saveUsers();

        System.out.println("\nThank you for signing up!\n");
        System.out.printf("Name: %s\nEmail: %s\nUsername: %s\nPassword: %s\nRole: %s\n", fullName, email, username, password, role);
        System.out.printf("%s has been signed up!\n", username);
        askToContinue();
        login();
    }

    static ArrayList<String> usernames = new ArrayList<>(); // ArrayList to store usernames
    static ArrayList<String> passwords = new ArrayList<>(); // ArrayList to store passwords
    static ArrayList<String> roles = new ArrayList<>();

    static void saveUsers() {
        try (FileWriter writer = new FileWriter("UserCredentials.txt")) {
            for (DagmayWeaverMonitorouput.User user : users) {
                writer.write(String.format("%s,%s,%s,%s\n", user.username, user.password, user.email, user.role));
            }
            writeUserCredentialsToCSV("UserCredentials.csv");

            // 2. Write to JSON
            writeUserCredentialsToJSON("UserCredentials.json");

            // 3. Write to XML
            writeUserCredentialsToXML("UserCredentials.xml");


        } catch ( IOException e ) {
            System.out.println("Error: Failed to save users. " + e.getMessage());
        }
    }


    // Method to save users to CSV
    static void writeUserCredentialsToCSV(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (DagmayWeaverMonitorouput.User user : users) {
                writer.write(String.format("%s,%s,%s,%s\n", user.username, user.password, user.email, user.role));
            }
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    // Method to save users to JSON
    static void writeUserCredentialsToJSON(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("[\n");
            for (int i = 0; i < users.size(); i++) {
                DagmayWeaverMonitorouput.User user = users.get(i);
                writer.write(String.format("  {\"username\": \"%s\", \"password\": \"%s\", \"email\": \"%s\", \"role\": \"%s\"}\n",
                        user.username, user.password, user.email, user.role));
                if (i < users.size() - 1) {
                    writer.write(",\n");
                }
            }
            writer.write("]");
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    // Method to save users to XML
    static void writeUserCredentialsToXML(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<users>\n");
            for (DagmayWeaverMonitorouput.User user : users) {
                writer.write(String.format("  <user username=\"%s\" password=\"%s\" email=\"%s\" role=\"%s\" />\n",
                        user.username, user.password, user.email, user.role));
            }
            writer.write("</users>\n");
        } catch (IOException e) {
            System.out.println("Error writing to XML file: " + e.getMessage());
        }
    }




    public static void listDagmayItems(String role) {
        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|          LIST OF DAGMAY ITEMS        |");
        System.out.println("\t+---------------------------------------+");

        if (dagmayItems.isEmpty()) {
            System.out.println("\t|     No Dagmay items available.     |");
            System.out.println("\t+---------------------------------------+");
            return;
        }

        for (DagmayItem item : dagmayItems) {
            System.out.println("\t| Item ID: " + item.itemId);
            System.out.println("\t| Name: " + item.itemName);

            // Display price only for customers
            if (role.equalsIgnoreCase("customer")) {
                System.out.println("\t| Price: ₱ " + item.price);
            }

            System.out.println("\t| Description: " + item.description);



            // Display stock only for staff and admin
            if (!role.equalsIgnoreCase("customer")) {
                System.out.println("\t| Stock: " + item.stock);
            }

            System.out.println("\t+---------------------------------------+");
        }
        askToContinue();
        returnToMainMenu();

    }

    // Method to add a new Dagmay item
    public static void addDagmayItem(String itemName, double price, String description) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|           ADD NEW DAGMAY ITEM        |");
        System.out.println("\t+---------------------------------------+");

        // Get new item ID
        int newItemId = 1;
        if (!dagmayItems.isEmpty()) {
            newItemId = dagmayItems.get(dagmayItems.size() - 1).itemId + 1;
        }

        // Get item name
        while (true) {
            System.out.print("\t| Enter item name: ");
            itemName = scanner.nextLine();
            if (!itemName.isEmpty()) {
                break;
            }
            System.out.println("\t| Item name cannot be empty. Please try again.");
        }

        // Get item price
        while (true) {
            System.out.print("\t| Enter item price: ₱ ");
            if (scanner.hasNextDouble()) {
                price = scanner.nextDouble();
                if (price > 0) {
                    break;
                } else {
                    System.out.println("\t| Price must be positive. Please try again.");
                }
            } else {
                System.out.println("\t| Invalid price input. Please try again.");
                scanner.next(); // Consume invalid input
            }
        }

        scanner.nextLine(); // consume the leftover newline

        // Get item description
        System.out.print("\t| Enter item description: ");
        description = scanner.nextLine();

        // Get item stock (only if Staff or Admin)
        int stock = 0;
        if (loggedInUserRole.equalsIgnoreCase("staff") || loggedInUserRole.equalsIgnoreCase("admin")) {
            while (true) {
                System.out.print("\t| Enter item stock: ");
                if (scanner.hasNextInt()) {
                    stock = scanner.nextInt();
                    if (stock >= 0) {
                        break;
                    } else {
                        System.out.println("\t| Stock cannot be negative. Please try again.");
                    }
                } else {
                    System.out.println("\t| Invalid stock input. Please try again.");
                    scanner.next(); // Consume invalid input
                }
            }
            scanner.nextLine(); // consume the leftover newline
        }

        // Create a new Dagmay item
        DagmayItem newItem = new DagmayItem(newItemId, itemName, price, description, stock);

        // Add the new item to the list
        dagmayItems.add(newItem);
        saveDagmayItems();

        // Record the staff who added the item
//
        StaffAddItem staffAddItem = new StaffAddItem(loggedInUser, newItemId, itemName, price, description, stock);
        itemsAddedByStaff.add(staffAddItem);

        // Save staff added items to file
        saveStaffAddedItems();

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|    Dagmay item added successfully.    |");
        System.out.println("\t+---------------------------------------+");

        while (true) {
            System.out.print("Do you want to add/update another item (A), or return to the main menu (M)? ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("A")) {
                addDagmayItem(itemName, price, description);
                // Add/update another item, break the loop to get to the top
            } else if (choice.equalsIgnoreCase("M")) {
                returnToMainMenu();
                return; // Exit the method
            } else {
                System.out.println("Invalid input. Please enter A or M.");
            }
        }

        // Ask to continue

    }

    // Method to update an existing Dagmay item
    public static void updateDagmayItem(int itemId, String itemName, double price, String description) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|        UPDATE DAGMAY ITEM           |");
        System.out.println("\t+---------------------------------------+");

        // Get the item ID to update
        int itemIdToUpdate;
        while (true) {
            System.out.print("\t| Enter the ID of the Dagmay item to update: ");
            if (scanner.hasNextInt()) {
                itemIdToUpdate = scanner.nextInt();
                break;
            } else {
                System.out.println("\t| Invalid input. Please enter a valid ID.");
                scanner.next(); // Consume invalid input
            }
        }
        scanner.nextLine(); // consume the leftover newline

        // Find the item in the list
        DagmayItem itemToUpdate = null;
        for (DagmayItem item : dagmayItems) {
            if (item.itemId == itemIdToUpdate) {
                itemToUpdate = item;
                break;
            }
        }

        if (itemToUpdate == null) {
            System.out.println("\t| Dagmay item with ID " + itemIdToUpdate + " not found.");
            System.out.println("\t+---------------------------------------+");
            return;
        }

        // Display current item details
        System.out.println("\t| Current Item Details:");
        System.out.println("\t| Item ID: " + itemToUpdate.itemId);
        System.out.println("\t| Name: " + itemToUpdate.itemName);
        System.out.println("\t| Price: ₱ " + itemToUpdate.price);
        System.out.println("\t| Description: " + itemToUpdate.description);
        if (loggedInUserRole.equalsIgnoreCase("staff") || loggedInUserRole.equalsIgnoreCase("admin")) {
            System.out.println("\t| Stock: " + itemToUpdate.stock);
        }

        // Get new name (optional)
        System.out.print("\t| Enter new name (press Enter to skip): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            itemToUpdate.itemName = newName;
        }

        // Get new price (optional)
        System.out.print("\t| Enter new price (press Enter to skip): ");
        String priceInput = scanner.nextLine();
        if (!priceInput.isEmpty()) {
            try {
                double newPrice = Double.parseDouble(priceInput);
                if (newPrice > 0) {
                    itemToUpdate.price = newPrice;
                } else {
                    System.out.println("\t| Price must be positive.");
                }
            } catch ( NumberFormatException e ) {
                System.out.println("\t| Invalid price input.");
            }
        }

        // Get new description (optional)
        System.out.print("\t| Enter new description (press Enter to skip): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.isEmpty()) {
            itemToUpdate.description = newDescription;
        }

        // Get new stock (optional, only for Staff and Admin)
        if (loggedInUserRole.equalsIgnoreCase("staff") || loggedInUserRole.equalsIgnoreCase("admin")) {
            System.out.print("\t| Enter new stock (press Enter to skip): ");
            String stockInput = scanner.nextLine();
            if (!stockInput.isEmpty()) {
                try {
                    int newStock = Integer.parseInt(stockInput);
                    if (newStock >= 0) {
                        itemToUpdate.stock = newStock;
                    } else {
                        System.out.println("\t| Stock cannot be negative.");
                    }
                } catch ( NumberFormatException e ) {
                    System.out.println("\t| Invalid stock input.");
                }
            }
        }

        saveDagmayItems();

        // Record the staff who updated the item
        StaffAddItem staffAddItem = new StaffAddItem(loggedInUser, itemIdToUpdate, itemToUpdate.itemName,
                itemToUpdate.price, itemToUpdate.description, itemToUpdate.stock);
        itemsAddedByStaff.add(staffAddItem);

        // Save staff added items to file
        saveStaffAddedItems();

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|     Dagmay item updated successfully.  |");
        System.out.println("\t+---------------------------------------+");

        while (true) {
            System.out.print("Do you want to add/update another item (A), or return to the main menu (M)? ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("A")) {
                addDagmayItem(itemName, price, description);
                // Add/update another item, break the loop to get to the top
            } else if (choice.equalsIgnoreCase("M")) {
                returnToMainMenu();
                return; // Exit the method
            } else {
                System.out.println("Invalid input. Please enter A or M.");
            }
        }
    }

    static void saveStaffAddedItems() {
        try (FileWriter writer = new FileWriter("StaffAddedItems.txt")) {
            for (StaffAddItem item : itemsAddedByStaff) {
                writer.write(String.format("%s,%d,%s,%.2f,%s,%d\n", item.staffName, item.itemId, item.itemName,
                        item.price, item.description, item.stock));
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to save staff added items. " + e.getMessage());
        }
    }

    // Method to load staff added items from file
    static void loadStaffAddedItems() {
        itemsAddedByStaff.clear(); // Clear the list before loading

        try (Scanner scanner = new Scanner(new File("StaffAddedItems.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    try {
                        String staffName = parts[0];
                        int itemId = Integer.parseInt(parts[1]);
                        String itemName = parts[2];
                        double price = Double.parseDouble(parts[3]);
                        String description = parts[4];
                        int stock = Integer.parseInt(parts[5]);
                        itemsAddedByStaff.add(new StaffAddItem(staffName, itemId, itemName, price, description, stock));
                    } catch (NumberFormatException e) {
                        System.out.println("Error reading staff added item: " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // File not found, no need to load anything
        }
    }

    // Method to save sales records to a file
    static void saveSalesRecords() {
        try (FileWriter writer = new FileWriter("SalesRecords.csv")) {
            for (Sale sale : salesRecords) {
                // Format the sale data for writing to the file
                writer.write(String.format("%s,", sale.customerName));
                for (String itemName : sale.itemsSold.keySet()) {
                    int quantity = sale.itemsSold.get(itemName);
                    writer.write(String.format("%s:%d,", itemName, quantity));
                }
                writer.write("\n"); // Move to the next line for the next sale
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to save sales records. " + e.getMessage());
        }
    }

    // Method to load sales records from a file
    static void loadSalesRecords() {
        salesRecords.clear(); // Clear the list before loading

        try (Scanner scanner = new Scanner(new File("SalesRecords.csv"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                // Extract customer name
                String customerName = parts[0];

                // Create a HashMap to store items sold for this sale
                HashMap<String, Integer> itemsSold = new HashMap<>();

                // Extract items sold and their quantities
                for (int i = 1; i < parts.length; i++) {
                    String[] itemPart = parts[i].split(":");
                    if (itemPart.length == 2) {
                        String itemName = itemPart[0];
                        int quantity = Integer.parseInt(itemPart[1]);
                        itemsSold.put(itemName, quantity);
                    }
                }

                // Create a Sale object and add it to the list
                salesRecords.add(new Sale(customerName, itemsSold));
            }
        } catch (FileNotFoundException e) {
            // File not found, no need to load anything
        }
    }

    // Method to delete a Dagmay item
    public static void deleteDagmayItem(int itemId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|         DELETE DAGMAY ITEM          |");
        System.out.println("\t+---------------------------------------+");

        // Get the item ID to delete
        int itemIdToDelete;
        while (true) {
            System.out.print("\t| Enter the ID of the Dagmay item to delete: ");
            if (scanner.hasNextInt()) {
                itemIdToDelete = scanner.nextInt();
                break;
            } else {
                System.out.println("\t| Invalid input. Please enter a valid ID.");
                scanner.next(); // Consume invalid input
            }
        }
        scanner.nextLine(); // consume the leftover newline

        // Find the item in the list
        DagmayItem itemToDelete = null;
        int itemIndex = -1; // Store the index of the item to delete
        for (int i = 0; i < dagmayItems.size(); i++) {
            if (dagmayItems.get(i).itemId == itemIdToDelete) {
                itemToDelete = dagmayItems.get(i);
                itemIndex = i;
                break;
            }
        }

        if (itemToDelete == null) {
            System.out.println("\t| Dagmay item with ID " + itemIdToDelete + " not found.");
            System.out.println("\t+---------------------------------------+");
            return;
        }

        // Display the item details to be deleted
        System.out.println("\t| Item to be deleted:");
        System.out.println("\t| Item ID: " + itemToDelete.itemId);
        System.out.println("\t| Name: " + itemToDelete.itemName);
        System.out.println("\t| Price: ₱ " + itemToDelete.price);
        System.out.println("\t| Description: " + itemToDelete.description);
        if (loggedInUserRole.equalsIgnoreCase("staff") || loggedInUserRole.equalsIgnoreCase("admin")) {
            System.out.println("\t| Stock: " + itemToDelete.stock);
        }

        // Prompt for confirmation
        System.out.print("\t| Are you sure you want to delete this item? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {
            // Remove the item from the list
            dagmayItems.remove(itemIndex);
            saveDagmayItems();
            System.out.println("\t+---------------------------------------+");
            System.out.println("\t|  Dagmay item deleted successfully.  |");
            System.out.println("\t+---------------------------------------+");
        } else {
            System.out.println("\t+---------------------------------------+");
            System.out.println("\t|        Deletion cancelled.           |");
            System.out.println("\t+---------------------------------------+");
        }
        askToContinue();
        returnToMainMenu();
    }

    // Method to monitor output (example implementation)
    // Method to monitor output (example implementation)
    // Method to monitor output (example implementation)
    static void monitorOutput() {


        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMonitor Output Menu:");
            System.out.println("  1. Items Added by Staff");
            System.out.println("  2. Sold Dagmay Items");
            System.out.println("  3. Total Sales Value");
            System.out.println("  4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Display information about items added by staff
                    displayItemsAddedByStaff();
                    break;
                case 2:
                    displaySoldItems();
                    break;
                case 3:
                    displayTotalSales();
                    break;
                case 4:
                    returnToMainMenu();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Method to display items added by staff
    static void displayItemsAddedByStaff() {
        if (itemsAddedByStaff.isEmpty()) {
            System.out.println("\nNo items added by staff yet.");
            return;
        }

        System.out.println("\nItems Added by Staff:");
        for (StaffAddItem item : itemsAddedByStaff) {
            System.out.printf("- Staff: %s\n", item.staffName);
            System.out.printf("  - Item ID: %d\n", item.itemId);
            System.out.printf("  - Name: %s\n", item.itemName);
            System.out.printf("  - Price: ₱ %.2f\n", item.price);
            System.out.printf("  - Description: %s\n", item.description);
            System.out.printf("  - Stock: %d\n", item.stock);
            System.out.println();
        }
    }

    // Method to display sold items
    static void displaySoldItems() {
        System.out.println("\nSold Dagmay Items:");
        for (Sale sale : salesRecords) {
            System.out.printf("- Customer: %s\n", sale.customerName);
            for (String itemName : sale.itemsSold.keySet()) {
                int quantity = sale.itemsSold.get(itemName);
                double itemPrice = findDagmayItemPrice(itemName);
                double itemTotal = itemPrice * quantity;
                System.out.printf("   - %s (x%d) - $%.2f\n", itemName, quantity, itemTotal);
            }
            System.out.println();
        }
    }

    // Method to display total sales value
    static void displayTotalSales() {
        double totalSalesValue = 0.0;
        for (Sale sale : salesRecords) {
            for (String itemName : sale.itemsSold.keySet()) {
                int quantity = sale.itemsSold.get(itemName);
                double itemPrice = findDagmayItemPrice(itemName);
                totalSalesValue += itemPrice * quantity;
            }
        }
        System.out.printf("\nTotal Sales Value: ₱ %.2f\n", totalSalesValue);
    }

    // Method to view the shopping cart
    public static void viewCart() {
        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|              VIEW CART              |");
        System.out.println("\t+---------------------------------------+");

        if (shoppingCart.isEmpty()) {
            System.out.println("\t|         Your cart is empty.         |");
            System.out.println("\t+---------------------------------------+");
            askToContinue();
            returnToMainMenu();
            return;
        }

        double totalPrice = 0.0;
        for (int itemId : shoppingCart.keySet()) {
            int quantity = shoppingCart.get(itemId);
            // Find the item in the dagmayItems list
            DagmayItem item = findDagmayItemById(itemId);
            if (item != null) {
                System.out.println("\t| Item ID: " + itemId);
                System.out.println("\t| Name: " + item.itemName);
                System.out.println("\t| Price: " + item.price);
                System.out.println("\t| Quantity: " + quantity);
                System.out.println("\t| Subtotal: " + (item.price * quantity));
                System.out.println("\t+---------------------------------------+");

                totalPrice += item.price * quantity;
            } else {
                System.out.println("\t| Item with ID " + itemId + " not found in the inventory.");
            }
        }
        System.out.println("\t| Total Price: " + totalPrice);
        System.out.println("\t+---------------------------------------+");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to checkout (C), remove an item (R), or go back (B)? ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("C")) {
            // Proceed to checkout
            checkout(totalPrice);
        } else if (choice.equalsIgnoreCase("R")) {
            // Implement item removal logic (you'll need to add this)
            removeItemFromCart();
        } else if (choice.equalsIgnoreCase("B")) {
            // Save the customer's cart before going back
            if (loggedInUserRole.equalsIgnoreCase("customer")) {
                saveCustomerCart(loggedInUser);
            }
            // Go back to the main menu
            returnToMainMenu();
        } else {
            // Invalid input
            System.out.println("Invalid input. Please enter C, R, or B.");
            viewCart(); // Call viewCart() again to redisplay the options
        }
    }

    private static void removeItemFromCart() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the item to remove: ");
        if (scanner.hasNextInt()) {
            int itemIdToRemove = scanner.nextInt();
            if (shoppingCart.containsKey(itemIdToRemove)) {
                shoppingCart.remove(itemIdToRemove);
                System.out.println("Item removed from cart.");
                // You might want to redisplay the cart here
                viewCart();
            } else {
                System.out.println("Item not found in cart.");
            }
        } else {
            System.out.println("Invalid input. Please enter a valid item ID.");
        }
    }

    // Method for checkout process
    private static void checkout(double totalPrice) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the amount of money you have: ");
        if (scanner.hasNextDouble()) {
            double amountPaid = scanner.nextDouble();
            if (amountPaid >= totalPrice) {
                // Calculate change
                double change = amountPaid - totalPrice;
                System.out.println("Thank you for your purchase! Your change is: ₱" + change);
                // Record the sale
                recordSale(loggedInUser, convertShoppingCartToSaleItems());
                // Clear the shopping cart
                shoppingCart.clear();
                System.out.println("Your cart has been cleared.");
                // Update inventory
                updateInventory();

                // Go back to the main menu
                returnToMainMenu();
            } else {
                // Insufficient funds
                System.out.println("Insufficient funds. Please add more money.");
                checkout(totalPrice);
            }
        } else {
            // Invalid input
            System.out.println("Invalid input. Please enter a number.");
            checkout(totalPrice);
        }
    }

    // Method to convert shopping cart to a HashMap for sales records
    private static HashMap<String, Integer> convertShoppingCartToSaleItems() {
        HashMap<String, Integer> saleItems = new HashMap<>();
        for (int itemId : shoppingCart.keySet()) {
            int quantity = shoppingCart.get(itemId);
            DagmayItem item = findDagmayItemById(itemId);
            if (item != null) {
                saleItems.put(item.itemName, quantity);
            }
        }
        return saleItems;
    }

    // Method to update inventory after a sale
    private static void updateInventory() {
        for (int itemId : shoppingCart.keySet()) {
            int quantity = shoppingCart.get(itemId);
            DagmayItem item = findDagmayItemById(itemId);
            if (item != null) {
                item.stock -= quantity; // Reduce stock
            }
        }
        saveDagmayItems(); // Save updated inventory
    }

    // Method to save a customer's cart to a file
    private static void saveCustomerCart(String username) {
        try (FileWriter writer = new FileWriter("UserSaveCart/" + username + "_Cart.txt")) {
            for (int itemId : shoppingCart.keySet()) {
                int quantity = shoppingCart.get(itemId);
                writer.write(itemId + ":" + quantity + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to save customer cart. " + e.getMessage());
        }
    }

    // Method to load a customer's cart from a file
    private static void loadCustomerCart(String username) {
        shoppingCart.clear(); // Clear the cart before loading

        try (Scanner scanner = new Scanner(new File("UserSaveCart/" + username + "_Cart.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    int itemId = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);
                    shoppingCart.put(itemId, quantity);
                }
            }
        } catch (FileNotFoundException e) {
            // File not found, no cart to load
        }
    }

    // Method to buy Dagmay items
    static void displayDagmayItems(String role) {
        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|          LIST OF DAGMAY ITEMS        |");
        System.out.println("\t+---------------------------------------+");

        if (dagmayItems.isEmpty()) {
            System.out.println("\t|     No Dagmay items available.     |");
            System.out.println("\t+---------------------------------------+");
            return;
        }

        for (DagmayItem item : dagmayItems) {
            System.out.println("\t| Item ID: " + item.itemId);
            System.out.println("\t| Name: " + item.itemName);

            // Display price only for customers
            if (role.equalsIgnoreCase("customer")) {
                System.out.println("\t| Price: " + item.price);
            }

            System.out.println("\t| Description: " + item.description);

            // Display stock only for staff and admin
            if (!role.equalsIgnoreCase("customer")) {
                System.out.println("\t| Stock: " + item.stock);
            }

            System.out.println("\t+---------------------------------------+");
        }
        askToContinue();
    }

    public static void buyDagmayItems() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\t+---------------------------------------+");
        System.out.println("\t|           BUY DAGMAY ITEMS          |");
        System.out.println("\t+---------------------------------------+");

        displayDagmayItems("customer"); // Display available items


        while (true) {

            // Get the item ID to purchase
            System.out.print("\t| Enter the ID of the item to buy (or 0 to finish): ");
            if (scanner.hasNextInt()) {
                int itemIdToBuy = scanner.nextInt();

                if (itemIdToBuy == 0) {
                    break; // Exit the buying process
                }

                // Find the item in the dagmayItems list
                DagmayItem itemToBuy = findDagmayItemById(itemIdToBuy);
                if (itemToBuy != null) {
                    // Get the quantity
                    System.out.print("\t| Enter the quantity: ");
                    if (scanner.hasNextInt()) {
                        int quantity = scanner.nextInt();
                        if (quantity > 0) {
                            // Add or update the item in the shopping cart
                            shoppingCart.put(itemIdToBuy, quantity);
                            System.out.println("\t| Item added to cart.");
                        } else {
                            System.out.println("\t| Invalid quantity. Please enter a positive number.");
                        }
                    } else {
                        System.out.println("\t| Invalid quantity input. Please enter a number.");
                        scanner.next(); // Consume invalid input
                    }
                } else {
                    System.out.println("\t| Item not found. Please enter a valid ID.");
                }
            } else {
                System.out.println("\t| Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
            System.out.println("\t+---------------------------------------+");
        }
        // Display the shopping cart
        viewCart();
    }

    // Helper method to find a Dagmay item by ID
    private static DagmayItem findDagmayItemById(int itemId) {
        for (DagmayItem item : dagmayItems) {
            if (item.itemId == itemId) {
                return item;
            }
        }
        return null; // Item not found
    }

    // Method to save Dagmay items to the file
    static void saveDagmayItems() {
        try (FileWriter writer = new FileWriter("DagmayItems.txt")) {
            for (DagmayItem item : dagmayItems) {
                writer.write(String.format("%d,%s,%.2f,%s,%d\n", item.itemId, item.itemName, item.price,
                        item.description, item.stock));
            }
        } catch ( IOException e ) {
            System.out.println("Error: Failed to save Dagmay items. " + e.getMessage());
        }
    }

    // recordSale() method implementation
    // Call saveSalesRecords() after recording a sale in recordSale()
    static void recordSale(String customerName, HashMap<String, Integer> itemsSold) {
        Sale newSale = new Sale(customerName, itemsSold);
        salesRecords.add(newSale);

        saveSalesRecords(); // Save sales records to file after adding a new sale
    }

    // findDagmayItemPrice() method implementation
    static double findDagmayItemPrice(String itemName) {
        for (DagmayItem item : dagmayItems) {
            if (item.itemName.equalsIgnoreCase(itemName)) {
                return item.price;
            }
        }
        System.out.println("Item not found: " + itemName); // Handle case where item is not found
        return 0.0; // Or throw an exception if you want to handle it differently
    }

    // Inner class to represent a User
    static class User {
        String username;
        String password;
        String email;
        String role;

        public User(String username, String password, String email, String role) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.role = role;
        }
    }

    // Inner class to represent a Dagmay Item
    static class DagmayItem {
        int itemId;
        String itemName;
        double price;
        String description;
        int stock; // Additional detail for Staff/Admin

        public DagmayItem(int itemId, String itemName, double price, String description, int stock) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.price = price;
            this.description = description;
            this.stock = stock;
        }
    }

    static class Sale {
        String customerName;
        HashMap<String, Integer> itemsSold;

        public Sale(String customerName, HashMap<String, Integer> itemsSold) {
            this.customerName = customerName;
            this.itemsSold = itemsSold;
        }
    }
    // saveSalesRecords() method implementation

    static class StaffAddItem {
        String staffName; // Name of the staff who added the item
        int itemId; // ID of the item added
        String itemName; // Name of the item
        double price; // Price of the item
        String description; // Description of the item
        int stock; // Stock of the item

        public StaffAddItem(String staffName, int itemId, String itemName, double price, String description, int stock) {
            this.staffName = staffName;
            this.itemId = itemId;
            this.itemName = itemName;
            this.price = price;
            this.description = description;
            this.stock = stock;
        }
    }



}