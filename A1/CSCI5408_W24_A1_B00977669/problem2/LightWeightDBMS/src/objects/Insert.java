package objects;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;
import utils.DatabaseContext;

/**
 * Insert query class to insert new records in the table
 */
public class Insert extends Query {

    /**
     * Constructs an Insert query handler
     *
     * @param databasePath File path to the database directory
     */
    public Insert(String databasePath) {
        super(databasePath);
    }

    /**
     * Insert new records in the table
     *
     * @param query Insert query string
     */
    @Override
    public void execute(String query) {
        // Split the input into individual INSERT statements
        String[] insertCommands = query.split(";\\s*");
        Arrays.stream(insertCommands)
                .filter(insertCommand -> !insertCommand.trim().isEmpty())
                .forEach(this::processSingleInsertStatement);
    }

    /**
     * Process a single insert statement
     *
     * @param insertCommand Insert query string
     */
    private void processSingleInsertStatement(String insertCommand) {
        String tableName = extractTableName(insertCommand);
        String[] values = extractValues(insertCommand);

        Path tableDataPath = Paths.get(DatabaseContext.getCurrentDatabase(), tableName + ".data");

        // Format using custom delimiter
        String dataLine = Arrays.stream(values)
                .map(this::removeQuotes)
                .collect(Collectors.joining("$")) + System.lineSeparator();

        try (BufferedWriter writer = Files.newBufferedWriter(tableDataPath, StandardOpenOption.APPEND)) {
            writer.write(dataLine);
            System.out.println("\nData inserted successfully into table " + tableName);
        } catch (IOException e) {
            System.err.println("\nFailed to insert data into table " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Extracts the name of the table
     *
     * @param insertCommand Insert query string
     * @return Table name
     */
    private String extractTableName(String insertCommand) {
        return insertCommand.split("\\s+")[2];
    }

    /**
     * Extracts value to be inserted from the query
     *
     * @param insertCommand Insert query string
     * @return Array of values to be inserted
     */
    private String[] extractValues(String insertCommand) {
        String valuesString = insertCommand.substring(insertCommand.indexOf("VALUES") + "VALUES".length()).trim();
        valuesString = valuesString.substring(valuesString.indexOf('(') + 1, valuesString.lastIndexOf(')'));
        return valuesString.split(",\\s*");
    }

    /**
     * Remove single quotes from a string value
     *
     * @param value String value to process
     * @return String value without single quotes
     */
    private String removeQuotes(String value) {
        return value.replace("'", "").trim();
    }
}
