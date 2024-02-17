import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ECommerce {
    private static final String LOCAL_DB_URL = "jdbc:mysql://localhost:3306/lab4_w24";
    private static final String LOCAL_DB_USER = "root";
    private static final String LOCAL_DB_PASSWORD = "DkSlyr@0807";
    private static final String REMOTE_DB_URL = "jdbc:mysql://34.18.14.64:3306/lab4";
    private static final String REMOTE_DB_USER = "root";
    private static final String REMOTE_DB_PASSWORD = "a\\af|'(S+<EE[/=h";

    public static void main(String[] args) {
        try {
            displayInventory();
            Order order = userOrder();
            insertOrderDetails(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to display the inventory details from the remote database
    private static void displayInventory() throws SQLException {
        String query = "SELECT * FROM Inventory";
        try (Connection connection = DriverManager.getConnection(REMOTE_DB_URL, REMOTE_DB_USER, REMOTE_DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery(query);
            long endTime = System.currentTimeMillis();

            System.out.println("Inventory Details:");
            while (resultSet.next()) {
                System.out.println("Item ID: " + resultSet.getInt("item_id")
                        + ", Item Name: " + resultSet.getString("item_name")
                        + ", Available Quantity: " + resultSet.getInt("available_quantity"));
            }
            System.out.println("Query executed in " + (endTime - startTime) + " milliseconds.");
        }
    }

    // Method to get information from the user for the order
    private static Order userOrder() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter user ID for the order:");
        int userId = scanner.nextInt();
        System.out.println("Enter item name for the order:");
        scanner.nextLine();
        String itemName = scanner.nextLine();
        System.out.println("Enter quantity for the order:");
        int quantity = scanner.nextInt();
        return new Order(userId, itemName, quantity);
    }

    // Method to insert order information which was taken from the user
    private static void insertOrderDetails(Order order) {
        String query = "INSERT INTO Order_info (user_id, item_name, quantity, order_date) VALUES (?, ?, ?, CURDATE())";
        try (Connection connection = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            long startTime = System.currentTimeMillis();

            preparedStatement.setInt(1, order.userId);
            preparedStatement.setString(2, order.itemName);
            preparedStatement.setInt(3, order.quantity);

            int rowsAffected = preparedStatement.executeUpdate();
            long endTime = System.currentTimeMillis();

            if (rowsAffected > 0) {
                System.out.println("Order inserted successfully.");
                updateInventory(order);
            } else {
                System.out.println("Order insertion failed.");
            }

            System.out.println("Insert order query executed in " + (endTime - startTime) + " milliseconds.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update the inventory detail (available quantity) based on the order
    private static void updateInventory(Order order) throws SQLException {
        String query = "UPDATE Inventory SET available_quantity = available_quantity - ? WHERE item_name = ?";
        try (Connection connection = DriverManager.getConnection(REMOTE_DB_URL, REMOTE_DB_USER, REMOTE_DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            long startTime = System.currentTimeMillis();

            preparedStatement.setInt(1, order.quantity);
            preparedStatement.setString(2, order.itemName);

            int rowsAffected = preparedStatement.executeUpdate();
            long endTime = System.currentTimeMillis();

            if (rowsAffected > 0) {
                System.out.println("Inventory updated successfully.");
            } else {
                System.out.println("Inventory update failed.");
            }

            System.out.println("Update inventory query executed in " + (endTime - startTime) + " milliseconds.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class Order {
        int userId;
        String itemName;
        int quantity;

        Order(int userId, String itemName, int quantity) {
            this.userId = userId;
            this.itemName = itemName;
            this.quantity = quantity;
        }
    }
}
