import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DagmayWeaverDirectory {
    public static void main(String[] args) {
        welcomeMessage();
    }

    static boolean loggedIn = false;
    static String loggedInUser = "";
    static String loggedInUserRole = "";
    static ArrayList<User> users = new ArrayList<>(); // ArrayList to store User objects

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

    static void welcomeMessage() {
        System.out.println("\t+----------------------------------------------------------------+");
        System.out.println("\t|        WELCOME TO DAVAO ORIENTAL DAGMAY WEAVERS DIRECTORY      |");
        System.out.println("\t|                 AND OUTPUT MONITORING PORTAL                   |");
        System.out.println("\t+----------------------------------------------------------------+");
        loadUsers(); // Load users from file at the start
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
        } catch (FileNotFoundException e) {
            System.out.println("User credentials file not found. Creating a new one.");
            try {
                new File("UserCredentials.txt").createNewFile();
            } catch (IOException ex) {
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
                System.out.println("\t|  C. View Cart                                                   |");
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
                System.out.println("\t|  B. Add/Update Dagmay Items                                   |");
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
                System.out.println("\t|  B. Add/Update Dagmay Items                                   |");
                System.out.println("\t|  C. Delete Dagmay Items                                       |");
                System.out.println("\t|  D. Monitor output                                              |");
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
                    case 'a', 'A' -> System.out.println("List of Dagmay Items (Customer)");
                    case 'b', 'B' -> System.out.println("Buy Dagmay Items");
                    case 'c', 'C' -> System.out.println("View Cart");
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
                    case 'a', 'A' -> System.out.println("List of Dagmay Items (Staff)");
                    case 'b', 'B' -> System.out.println("Add/Update Dagmay Items");
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
                    case 'a', 'A' -> System.out.println("List of Dagmay Items (Admin)");
                    case 'b', 'B' -> System.out.println("Add/Update Dagmay Items");
                    case 'c', 'C' -> System.out.println("Delete Dagmay Items");
                    case 'd', 'D' -> System.out.println("Monitor output");
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
            displayMenu();
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    static boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                loggedIn = true;
                loggedInUser = username;
                loggedInUserRole = user.role;
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

    static void forgotPassword() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Forgot Password? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            createNewPassword();
        } else if (choice.equalsIgnoreCase("N")) {
            askToContinue();
            login();
        } else {
            System.out.println("Invalid input. Please try again.");
            forgotPassword();
        }
    }

    private static void createNewPassword() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n+---------------------------------------+");
        System.out.println("|            RECOVER PASSWORD           |");
        System.out.println("+---------------------------------------+");

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your email address: ");
        String email = scanner.nextLine();

        boolean isUserFound = false;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.username.equals(username) && user.email.equals(email)) {
                isUserFound = true;
                String newPassword;
                String confirmPassword;
                while (true) {
                    System.out.print("Enter your new password: ");
                    newPassword = scanner.nextLine();
                    System.out.print("Confirm your new password: ");
                    confirmPassword = scanner.nextLine();

                    if (newPassword.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
                        if (newPassword.equals(confirmPassword)) {
                            user.password = newPassword;
                            break;
                        } else {
                            System.out.println("Passwords do not match. Please try again.");
                        }
                    } else {
                        System.out.println("Invalid password. Password must contain at least a lowercase/uppercase and a numeric character.");
                        System.out.println("Password must be in alphanumeric characters (A-Z, a-z, 0-9) and no special characters.");
                    }
                }
                // Update user in the list
                users.set(i, user);
                saveUsers();
                System.out.println("Password updated successfully.");
                break;
            }
        }

        if (!isUserFound) {
            System.out.println("User not found. Please try again.");
        }
    }

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
        User newUser = new User(username, password, email, role);
        users.add(newUser);
        saveUsers();

        System.out.println("\nThank you for signing up!\n");
        System.out.printf("Name: %s\nEmail: %s\nUsername: %s\nPassword: %s\nRole: %s\n", fullName, email, username, password, role);
        System.out.printf("%s has been signed up!\n", username);
        askToContinue();
        login();
    }

    static void saveUsers() {
        try (FileWriter writer = new FileWriter("UserCredentials.txt")) {
            for (User user : users) {
                writer.write(String.format("%s,%s,%s,%s\n", user.username, user.password, user.email, user.role));
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to save users. " + e.getMessage());
        }
    }

    // Example: listDagmayItems() method
//    public static void listDagmayItems(String role) {
//        // Load Dagmay items from file or database
//        // ...
//        // Display items differently based on the role (e.g., show prices only for customers)
//        if (role.equalsIgnoreCase("customer")) {
//            // Display item name, price, and description
//        } else { // Staff or Admin
//            // Display item name, price, description, and additional details (stock, etc.)
//        }
//    }
//
//    // Example: addDagmayItem() method
//    public static void addDagmayItem(String itemName, double price, String description) {
//        // Validate input (ensure itemName is not empty, price is positive, etc.)
//        // ...
//        // Create a new Dagmay item object
//        DagmayItem newItem = new DagmayItem(itemName, price, stock, description);
//
//        // Add the newItem to your    data source (file or database)
//        // ...
//    }
//
//    // Example: monitorOutput() method
//    public static void monitorOutput() {
//        // Retrieve production data from your data source
//        // ...
//        // Calculate and display output statistics
//        // ... (e.g., total items produced, items per weaver, etc.)
//    }
}

