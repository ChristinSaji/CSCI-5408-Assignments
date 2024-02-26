package services;

import utils.FileUtils;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Manage user authentication
 */
public class AuthenticationService {

    private final Scanner scanner;
    public final String usersFilePath = "DataSource/System/user_info.txt";

    /**
     * Initialize new AuthenticationService with scanner
     */
    public AuthenticationService() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Verify user from provided username and password
     *
     * @param username Username of user
     * @param password Password of user
     * @return True if login successful with captcha validation, false otherwise
     */
    public boolean login(String username, String password) {
        HashMap<String, String> userMap = FileUtils.loadUsersFromFile(usersFilePath);
        String hashedPassword = hashPassword(password);

        if (userMap.containsKey(username) && userMap.get(username).equals(hashedPassword)) {
            if (performCaptcha()) {
                System.out.println("\nLogin successful!");
                return true;
            } else {
                System.out.println("\nCaptcha validation failed.");
                return false;
            }
        } else {
            System.out.println("\nLogin failed due to incorrect credentials.");
            return false;
        }
    }

    /**
     * Hashes a password using MD5 hashing algorithm
     *
     * @param password Password to hash
     * @return Hashes password
     * @throws RuntimeException If MD5 algorithm is not available
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("\nMD5 hashing algorithm not found", e);
        }
    }

    /**
     * Perform a simple captcha validation
     *
     * @return True if user enters captcha correctly, false otherwise
     */
    public boolean performCaptcha() {
        int captcha = new Random().nextInt(8999) + 1000; // Generate a 4-digit captcha
        System.out.println("\nCaptcha: " + captcha);
        System.out.print("Enter captcha: ");

        int input = scanner.nextInt();
        scanner.nextLine();
        return input == captcha;
    }
}
