package utils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.nio.file.Paths;

/**
 * Utility class for file operation for user data
 */
public class FileUtils {

    /**
     * Append a new user with hashed password
     *
     * @param filePath       File path where user data is stored
     * @param username       Username of user
     * @param hashedPassword Hashed password of user
     */
    public static void appendUserToFile(String filePath, String username, String hashedPassword) {
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println(username + "=" + hashedPassword + ";");
        } catch (IOException e) {
            System.err.println("\nAn error occurred while writing to the user file: " + e.getMessage());
        }
    }

    /**
     * Loads users and their hashed passwords from file
     *
     * @param filePath File path where user data is stored
     * @return A hashmap containing username and hashed password
     */
    public static HashMap<String, String> loadUsersFromFile(String filePath) {
        HashMap<String, String> userMap = new HashMap<>();
        try (Scanner scanner = new Scanner(Paths.get(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] userEntries = line.split(";");
                for (String entry : userEntries) {
                    String[] userDetails = entry.split("=");
                    if (userDetails.length == 2) {
                        userMap.put(userDetails[0], userDetails[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("\nFailed to load users from file: " + e.getMessage());
        }
        return userMap;
    }
}
