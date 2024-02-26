package objects;

import java.io.File;

/**
 * CREATE DATABASE query class to create new database
 */
public class CreateDatabase extends Query {

    /**
     * Constructs a Create Database handler
     *
     * @param dbDirectoryPath File path to the database directory
     */
    public CreateDatabase(String dbDirectoryPath) {
        super(dbDirectoryPath);
    }

    /**
     * Creation of database directory based on query
     *
     * @param query Create database query string
     */
    @Override
    public void execute(String query) {
        File databaseDir = new File(dbDirectoryPath);
        if (!databaseDir.exists()) {
            if (databaseDir.mkdirs()) {
                System.out.println("\nDatabase created successfully.");
            } else {
                System.out.println("\nFailed to create the database.");
            }
        } else {
            System.out.println("\nDatabase already exists.");
        }
    }
}
