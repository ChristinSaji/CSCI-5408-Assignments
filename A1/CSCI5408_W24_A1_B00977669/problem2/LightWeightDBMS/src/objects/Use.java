package objects;

import utils.DatabaseContext;
import java.io.File;

/**
 * Handles the USE database
 */
public class Use extends Query {

    /**
     * Construct a Use object
     *
     * @param baseDatabasePath File path to the database directory
     */
    public Use(String baseDatabasePath) {
        super(baseDatabasePath);
    }

    /**
     * Executes USE database to set current database context
     *
     * @param query USE query string
     */
    @Override
    public void execute(String query) {
        // Split the query by whitespace and check if it has at least 3 parts ("use", "dbname", ";")
        String[] parts = query.trim().split("\\s+");

        if (parts.length >= 2 && "use".equalsIgnoreCase(parts[0])) {
            String databaseName = parts[1].replaceAll(";", "");
            String databasePath = this.dbDirectoryPath + File.separator + databaseName;

            File databaseDir = new File(databasePath);
            if (databaseDir.exists() && databaseDir.isDirectory()) {
                DatabaseContext.setCurrentDatabase(databasePath);
                System.out.println("\nUsing database: " + databaseName);
            } else {
                System.out.println("\nDatabase does not exist.");
            }
        } else {
            System.out.println("\nInvalid format for USE statement.");
        }
    }
}
