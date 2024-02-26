package application;

import services.AuthenticationService;
import services.QueryProcessor;
import utils.FileUtils;
import java.util.Scanner;

/**
 * Main application class for Light-Weight DBMS
 */
public class DBApp {

    private final AuthenticationService authService;
    private final Scanner scanner;

    /**
     * Initialize authentication service and scanner
     */
    public DBApp() {
        this.authService = new AuthenticationService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Launches main menu
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        DBApp dbApp = new DBApp();
        dbApp.displayMainMenu();
    }

    /**
     * Main menu interface
     */
    private void displayMainMenu() {
        boolean exitRequest = false;
        while (!exitRequest) {
            System.out.println("\nWelcome to the Light-Weight DBMS Application!");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("\nExiting application...");
                    exitRequest = true;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select 1, 2, or 3.");
                    break;
            }
        }
    }

    /**
     * Login process
     */
    private void login() {
        System.out.print("\nUsername: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (authService.login(username, password)) {
            System.out.println("\nYou are now logged in.");
            query();
        } else {
            System.out.println("\nLogin attempt failed.");
        }
    }

    /**
     * User registration
     */
    private void register() {
        System.out.print("\nChoose a Username: ");
        String username = scanner.nextLine();
        System.out.print("Choose a Password: ");
        String password = scanner.nextLine();

        String hashedPassword = authService.hashPassword(password);
        FileUtils.appendUserToFile(authService.usersFilePath, username, hashedPassword);

        System.out.println("\nRegistration successful. You can now login.");
    }

    /**
     * SQL query interface
     */
    public void query() {
        QueryProcessor processor = new QueryProcessor("DataSource/Database"); // Base path for databases
        while (true) {
            System.out.println("\nEnter your SQL query (or 'exit' to log out):");
            String userQuery = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userQuery)) {
                break;
            }

            processor.processQuery(userQuery);
        }
    }
}
