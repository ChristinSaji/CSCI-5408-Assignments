package objects;

/**
 * Abstract class to represent database query
 */
public abstract class Query {
    /**
     * Directory path to the database
     */
    protected String dbDirectoryPath;

    /**
     * Constructs new Query instance
     *
     * @param dbDirectoryPath File path to the database directory
     */
    public Query(String dbDirectoryPath) {
        this.dbDirectoryPath = dbDirectoryPath;
    }

    /**
     * Executes the query
     *
     * @param query SQL string
     */
    public abstract void execute(String query);
}
