package objects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import utils.DatabaseContext;

/**
 * Delete query class to delete records from the table
 */
public class Delete extends Query {

    /**
     * Constructs a Delete handler
     *
     * @param databasePath File path to the database directory
     */
    public Delete(String databasePath) {
        super(databasePath);
    }

    /**
     * Remove Records from the table
     *
     * @param query Delete query string
     */
    @Override
    public void execute(String query) {
        String tableName = extractTableName(query);
        String whereCondition = extractWhereCondition(query);

        Path tableDataPath = Paths.get(DatabaseContext.getCurrentDatabase(), tableName + ".data");

        try {
            List<String> lines = Files.readAllLines(tableDataPath);
            List<String> columnNames = Arrays.asList(lines.getFirst().split("\\$"));

            List<String> updatedLines = whereCondition.isEmpty() ?
                    lines.subList(0, 1) :
                    lines.stream()
                            .filter(line -> !satisfiesWhereCondition(line, whereCondition, columnNames))
                            .collect(Collectors.toList());

            Files.write(tableDataPath, updatedLines);
            System.out.println("\nRows deleted successfully from table " + tableName + ".");

        } catch (IOException e) {
            System.err.println("\nFailed to delete rows from table " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Extracts the name of the table
     *
     * @param deleteCommand Delete query string
     * @return Name of the table
     * @throws IllegalArgumentException If DELETE command format is invalid
     */
    private String extractTableName(String deleteCommand) {
        String[] parts = deleteCommand.split("\\s+");
        if (parts.length > 2 && "from".equalsIgnoreCase(parts[1])) {
            return parts[2].replaceAll(";", "");
        } else {
            throw new IllegalArgumentException("\nInvalid DELETE query format.");
        }
    }


    /**
     * Extracts WHERE condition from the delete query
     *
     * @param query Delete query string
     * @return Extracted WHERE condition or an empty string if not found
     */
    private String extractWhereCondition(String query) {
        if (query.toLowerCase().contains("where")) {
            int whereIndex = query.toLowerCase().indexOf("where") + 5;
            int endIndex = query.contains(";") ? query.indexOf(';') : query.length();
            return query.substring(whereIndex, endIndex).trim();
        }
        return "";
    }

    /**
     * Determine if row satisfies WHERE condition
     *
     * @param line           Row from the table
     * @param whereCondition WHERE condition to match against
     * @param columnNames    List of column names for table
     * @return True if row satisfies condition, false otherwise
     */
    private boolean satisfiesWhereCondition(String line, String whereCondition, List<String> columnNames) {
        if (whereCondition == null || whereCondition.isEmpty()) {
            return true;
        }

        String[] parts = whereCondition.split("\\s+");
        if (parts.length < 3) {
            System.err.println("\nInvalid WHERE clause format.");
            return false;
        }

        String columnName = parts[0];
        String operator = parts[1];
        String conditionValue = parts[2].replaceAll("'", "");

        int columnIndex = columnNames.indexOf(columnName);
        if (columnIndex == -1) {
            System.err.println("\nColumn " + columnName + " not found.");
            return false;
        }

        String[] rowValues = line.split("\\$");
        String dataValue = rowValues[columnIndex];

        return compareValues(dataValue, operator, conditionValue);
    }

    /**
     * Compare value from data row to the condition value based on the operator
     *
     * @param dataValue      Value to compare from row
     * @param operator       Comparison operator
     * @param conditionValue Value to compare against
     * @return True if the comparison holds, false otherwise
     * @throws NumberFormatException If non-numeric values are compared
     */
    private boolean compareValues(String dataValue, String operator, String conditionValue) {
        try {
            double dataValueNum = Double.parseDouble(dataValue);
            double conditionValueNum = Double.parseDouble(conditionValue);

            return switch (operator) {
                case "=" -> dataValueNum == conditionValueNum;
                case ">" -> dataValueNum > conditionValueNum;
                case "<" -> dataValueNum < conditionValueNum;
                default -> throw new IllegalArgumentException("\nUnsupported operator: " + operator);
            };
        } catch (NumberFormatException e) {
            if (operator.equals("=")) {
                return dataValue.equals(conditionValue);
            } else {
                System.err.println("\nNon-numeric data cannot be compared with > or < operators.");
                return false;
            }
        }
    }
}
