package objects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import utils.DatabaseContext;

/**
 * Update query class to update records
 */
public class Update extends Query {

    /**
     * Constructs Delete query handler
     *
     * @param databasePath File path to the database directory
     */
    public Update(String databasePath) {
        super(databasePath);
    }

    /**
     * Modify records in a table
     *
     * @param query Update query string
     */
    @Override
    public void execute(String query) {
        String tableName = extractTableName(query);
        HashMap<String, String> setClause = extractSetClause(query);
        String whereCondition = extractWhereCondition(query);

        Path tableDataPath = Paths.get(DatabaseContext.getCurrentDatabase(), tableName + ".data");

        try {
            List<String> lines = Files.readAllLines(tableDataPath);
            List<String> columnNames = Arrays.asList(lines.getFirst().split("\\$"));
            List<String> updatedLines = lines.stream()
                    .map(line -> updateLineIfNeeded(line, setClause, whereCondition, columnNames))
                    .collect(Collectors.toList());

            Files.write(tableDataPath, updatedLines);
            System.out.println("\nTable " + tableName + " updated successfully.");

        } catch (IOException e) {
            System.err.println("\nFailed to update table " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Extracts table name
     *
     * @param updateCommand Update query string
     * @return Table name
     * @throws IllegalArgumentException If Update query format is invalid
     */
    private String extractTableName(String updateCommand) {
        String[] parts = updateCommand.split("\\s+");
        if (parts.length > 1 && "update".equalsIgnoreCase(parts[0])) {
            return parts[1];
        } else {
            throw new IllegalArgumentException("\nInvalid UPDATE query format.");
        }
    }

    /**
     * Extracts and parse SET clause from Update
     *
     * @param updateCommand Update query string
     * @return A map of column names to their new value
     * @throws IllegalArgumentException If SET clause format is invalid
     */
    private HashMap<String, String> extractSetClause(String updateCommand) {
        HashMap<String, String> updates = new HashMap<>();
        String setClause = updateCommand.substring(updateCommand.indexOf("SET") + 3,
                updateCommand.contains("WHERE") ? updateCommand.indexOf("WHERE") : updateCommand.length());

        String[] assignments = setClause.split(",");
        for (String assignment : assignments) {
            String[] parts = assignment.trim().split("=");
            if (parts.length == 2) {
                String column = parts[0].trim();
                String value = parts[1].trim().replaceAll("'", "");
                updates.put(column, value);
            } else {
                throw new IllegalArgumentException("\nInvalid SET clause format.");
            }
        }
        return updates;
    }

    /**
     * Extracts WHERE condition from Update command
     *
     * @param query Update query string
     * @return WHERE condition of the query
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
     * Update line if it satisfies the WHERE condition
     *
     * @param line Current line of data from table
     * @param setClause A map of column names
     * @param whereCondition WHERE condition to satisfy
     * @param columnNames Column names in the table
     * @return Updated line, or original line if no update is needed
     */
    private String updateLineIfNeeded(String line, HashMap<String, String> setClause, String whereCondition, List<String> columnNames) {
        if (satisfiesWhereCondition(line, whereCondition, columnNames)) {
            String[] values = line.split("\\$");
            setClause.forEach((columnName, newValue) -> {
                int columnIndex = columnNames.indexOf(columnName);
                if (columnIndex != -1) {
                    if (isArithmeticExpression(newValue)) {
                        int updatedValue = calculateNewValue(values[columnIndex], newValue);
                        values[columnIndex] = String.format("%d", updatedValue);
                    } else {
                        values[columnIndex] = newValue;
                    }
                }
            });
            return String.join("$", values);
        }
        return line;
    }

    /**
     * Update a line if it satisfies WHERE condition
     *
     * @param line Current line of data from table
     * @param whereCondition A map of column names
     * @param columnNames Column names in the table
     * @return True if the condition is satisfied, false otherwise
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
     * Compare two values based on operator
     *
     * @param dataValue Value from data line
     * @param operator Operator
     * @param conditionValue Value to compare against
     * @return True if the comparison is true, false otherwise
     * @throws NumberFormatException Numeric conversion fails
     * @throws IllegalArgumentException If unsupported operator is provided
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
     * Checks if a value string is an arithmetic expression
     *
     * @param value Value string to check
     * @return True if the string is arithmetic expression, false otherwise
     */
    private boolean isArithmeticExpression(String value) {
        return value.matches("[a-zA-Z_]+\\s*[+-]\\s*\\d+");
    }

    /**
     * Calculates the new value based on arithmetic expression
     *
     * @param currentValue Current value of the column
     * @param expression Arithmetic expression
     * @return New value after applying expression
     * @throws IllegalArgumentException If expression is invalid
     */
    private int calculateNewValue(String currentValue, String expression) {
        int currentValueNum = Integer.parseInt(currentValue);
        Pattern pattern = Pattern.compile("([a-zA-Z_]+)\\s*([+-])\\s*(\\d+)");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            String operation = matcher.group(2);
            int operand = Integer.parseInt(matcher.group(3));
            if ("+".equals(operation)) {
                return currentValueNum + operand;
            } else if ("-".equals(operation)) {
                return currentValueNum - operand;
            }
        }
        throw new IllegalArgumentException("\nInvalid arithmetic expression: " + expression);
    }
}
