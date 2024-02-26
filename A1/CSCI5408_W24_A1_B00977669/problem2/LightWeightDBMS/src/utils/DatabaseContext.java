package utils;

/**
 * Maintains context of current selected database
 */
public class DatabaseContext {
    private static String currentDatabasePath = null;

    /**
     * Set path of the current database
     *
     * @param path File path to the current database
     */
    public static void setCurrentDatabase(String path) {
        currentDatabasePath = path;
    }

    /**
     * Retrieves file path of the current database
     *
     * @return Path to current database
     */
    public static String getCurrentDatabase() {
        return currentDatabasePath;
    }

    /**
     * Checks whether database has been selected
     *
     * @return True if database is currently selected, false otherwise
     */
    public static boolean hasDatabaseSelected() {
        return currentDatabasePath != null && !currentDatabasePath.isEmpty();
    }
}
