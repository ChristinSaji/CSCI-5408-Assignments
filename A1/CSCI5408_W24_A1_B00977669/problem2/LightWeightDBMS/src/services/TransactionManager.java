package services;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage Transaction for SQL operations
 */
public class TransactionManager {
    private final List<String> transactionQueries;
    private boolean activeTransaction;

    /**
     * Initialize a new TransactionManager instance
     */
    public TransactionManager() {
        this.transactionQueries = new ArrayList<>();
        this.activeTransaction = false;
    }

    /**
     * Begins a new transaction
     */
    public void beginTransaction() {
        activeTransaction = true;
        transactionQueries.clear();
    }

    /**
     * Commits the current transaction to the database
     *
     * @param queryProcessor QueryProcessor instance to execute transaction query
     */
    public void commit(QueryProcessor queryProcessor) {
        if (!activeTransaction) {
            System.out.println("\nNo active transaction to commit.");
            return;
        }

        for (String query : transactionQueries) {
            queryProcessor.executeQueryDirectly(query);
        }

        activeTransaction = false;
        transactionQueries.clear();
    }

    /**
     * Rolls back current transaction
     */
    public void rollback() {
        activeTransaction = false;
        transactionQueries.clear();
        System.out.println("\nTransaction has been rolled back.");
    }

    /**
     * Adds a query to current transaction
     *
     * @param query SQL query to add to transaction
     * @throws IllegalStateException If there is no active transaction
     */
    public void addQuery(String query) {
        if (activeTransaction) {
            transactionQueries.add(query);
        } else {
            throw new IllegalStateException("\nNo active transaction. Cannot add query.");
        }
    }

    /**
     * Checks if there is an active transaction
     *
     * @return True if transaction is active, false otherwise
     */
    public boolean isActiveTransaction() {
        return activeTransaction;
    }
}
