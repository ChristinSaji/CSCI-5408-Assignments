package objects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import utils.DatabaseContext;

/**
 * Select query class to fetch data records
 */
public class Select extends Query {

    /**
     * Constructs a Select query handler
     *
     * @param databasePath File path to the database directory
     */
    public Select(String databasePath) {
        super(databasePath);
    }

    /**
     * Fetch records from table
     *
     * @param query Select query string
     */
    @Override
    public void execute(String query) {
        String tableName = extractTableName(query);
        List<String> selectedColumns = extractSelectedColumns(query);
        String whereCondition = extractWhereCondition(query);

        Path tableDataPath = Paths.get(DatabaseContext.getCurrentDatabase(), tableName + ".data");

        try {
            List<String> lines = Files.readAllLines(tableDataPath);
            List<String> columnNames = Arrays.asList(lines.getFirst().split("\\$"));

            // Print column headers
            if (selectedColumns.contains("*")) {
                selectedColumns = columnNames;
            }
            printColumnHeaders(selectedColumns);

            // Process and print each row of data
            List<String> finalSelectedColumns = selectedColumns;
            lines.stream().skip(1)
                    .filter(line -> satisfiesWhereCondition(line, whereCondition, columnNames))
                    .map(line -> selectColumns(line, finalSelectedColumns, columnNames))
                    .forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("\nFailed to read from table " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Extract table name
     *
     * @param query Select query string
     * @return Table name
     */
    private String extractTableName(String query) {
        String[] parts = query.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            if ("from".equalsIgnoreCase(parts[i])) {
                return parts[i + 1].replaceAll(";", "");
            }
        }
        throw new IllegalArgumentException("\nInvalid query: Table name not found.");
    }

    /**
     * Extracts columns to be selected
     *
     * @param query Select query string
     * @return A list of columns to be selected. Return all columns if * is present
     */
    private List<String> extractSelectedColumns(String query) {
        String selectClause = query.substring(query.indexOf("SELECT") + 6, query.indexOf("FROM")).trim();
        if (selectClause.equals("*")) {
            return List.of("*");
        } else {
            return Arrays.asList(selectClause.split(",\\s*"));
        }
    }

    /**
     * Extracts WHERE condition from Select query
     *
     * @param query Select query string
     * @return WHERE condition as a string or an empty string if not present
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
     * Prints the columns headers
     *
     * @param selectedColumns Columns to be printed
     */
    private void printColumnHeaders(List<String> selectedColumns) {
        String headers = selectedColumns.stream()
                .map(name -> String.format("%-15s", name))
                .collect(Collectors.joining());
        System.out.println(headers);
    }

    /**
     * Determine if given line satisfies WHERE condition
     *
     * @param line           Data line from table
     * @param whereCondition WHERE condition to be evaluated
     * @param columnNames    Column names
     * @return True if the line satisfies the condition, false otherwise
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
     * Compare data value against condition value based on specified operator
     *
     * @param dataValue      Value from the data line
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

    /**
     * Selects and formats the specified columns
     *
     * @param line            Data line from the table
     * @param selectedColumns Columns to be selected
     * @param columnNames     Name of all the columns
     * @return A string representing the selected and formatted columns
     */
    private String selectColumns(String line, List<String> selectedColumns, List<String> columnNames) {
        String[] values = line.split("\\$");
        return IntStream.range(0, columnNames.size())
                .filter(i -> selectedColumns.contains(columnNames.get(i)))
                .mapToObj(i -> String.format("%-15s", values[i]))
                .collect(Collectors.joining());
    }
}
