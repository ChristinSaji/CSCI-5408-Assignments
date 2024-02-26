package services;

import objects.*;
import utils.DatabaseContext;

import java.nio.file.Paths;

/**
 * Processes SQL queries and manage transaction
 */
public class QueryProcessor {

    private final String baseDatabasePath;
    private final TransactionManager transactionManager;

    /**
     * Initialize new QueryProcessor with specified database path
     *
     * @param baseDatabasePath File path to the database directory
     */
    public QueryProcessor(String baseDatabasePath) {
        this.baseDatabasePath = baseDatabasePath;
        this.transactionManager = new TransactionManager();
    }

    /**
     * Process SQL query either executing directly or queueing it within a transaction
     *
     * @param query SQL query
     */
    public void processQuery(String query) {
        // Normalize the query to handle different cases
        String normalizedQuery = query.trim().toLowerCase();

        if (normalizedQuery.startsWith("begin transaction")) {
            transactionManager.beginTransaction();
        } else if (normalizedQuery.startsWith("commit")) {
            transactionManager.commit(this);
        } else if (normalizedQuery.startsWith("rollback")) {
            transactionManager.rollback();
        } else if (transactionManager.isActiveTransaction()) {
            transactionManager.addQuery(query);
        } else {
            executeQueryDirectly(query);
        }
    }

    /**
     * Executes SQL query directly to database
     *
     * @param query SQL query
     */
    public void executeQueryDirectly(String query) {
        // Normalize the query to handle different cases
        String normalizedQuery = query.trim().toLowerCase();

        if (normalizedQuery.startsWith("create database")) {
            handleCreateDatabase(query);
        } else if (normalizedQuery.startsWith("use")) {
            handleUseDatabase(query);
        } else if (DatabaseContext.hasDatabaseSelected()) {
            if (normalizedQuery.startsWith("create table")) {
                handleCreateTable(query);
            } else if (normalizedQuery.startsWith("insert into")) {
                handleInsertInto(query);
            } else if (normalizedQuery.startsWith("select")) {
                handleSelect(query);
            } else if (normalizedQuery.startsWith("update")) {
                handleUpdate(query);
            } else if (normalizedQuery.startsWith("delete")) {
                handleDelete(query);
            }
            // Additional DML operations can be added here
        } else {
            System.out.println("\nPlease select a database first using the USE command.");
        }
    }

    private void handleCreateDatabase(String query) {
        String databaseName = extractNameAfterKeyword(query);
        String dbDirectoryPath = Paths.get(baseDatabasePath, databaseName).toString();
        CreateDatabase createDb = new CreateDatabase(dbDirectoryPath);
        createDb.execute(query);
    }

    /**
     * Extracts name immediately from CREATE DATABASE command
     *
     * @param query SQL query
     * @return Name
     * @throws IllegalArgumentException If keyword is not followed by name
     */
    private String extractNameAfterKeyword(String query) {
        String keyword = "create database";
        String lowerCaseQuery = query.toLowerCase();
        int keywordIndex = lowerCaseQuery.indexOf(keyword);
        if (keywordIndex == -1) {
            throw new IllegalArgumentException("\nKeyword '" + keyword + "' not found in the query.");
        }
        String afterKeyword = query.substring(keywordIndex + keyword.length()).trim();
        String[] parts = afterKeyword.split("\\s+", 2);
        if (parts.length == 0 || parts[0].isEmpty()) {
            throw new IllegalArgumentException("\nThe keyword '" + keyword + "' was not followed by a name.");
        }
        return parts[0].replaceAll(";", "");
    }

    private void handleUseDatabase(String query) {
        Use useDatabase = new Use(baseDatabasePath);
        useDatabase.execute(query);
    }

    private void handleCreateTable(String query) {
        String currentDbPath = DatabaseContext.getCurrentDatabase();
        Create createTable = new Create(currentDbPath);
        createTable.execute(query);
    }

    private void handleInsertInto(String query) {
        String currentDbPath = DatabaseContext.getCurrentDatabase();
        Insert insertData = new Insert(currentDbPath);
        insertData.execute(query);
    }

    private void handleSelect(String query) {
        String currentDbPath = DatabaseContext.getCurrentDatabase();
        Select selectQuery = new Select(currentDbPath);
        selectQuery.execute(query);
    }

    private void handleUpdate(String query) {
        String currentDbPath = DatabaseContext.getCurrentDatabase();
        Update updateData = new Update(currentDbPath);
        updateData.execute(query);
    }

    private void handleDelete(String query) {
        String currentDbPath = DatabaseContext.getCurrentDatabase();
        Delete deleteData = new Delete(currentDbPath);
        deleteData.execute(query);
    }
}
