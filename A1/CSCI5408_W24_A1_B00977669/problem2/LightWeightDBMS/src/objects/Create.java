package objects;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * CREATE query class to create tables in the database
 */
public class Create extends Query {

    /**
     * Constructs a Create query handler
     *
     * @param databasePath File path to the database directory
     */
    public Create(String databasePath) {
        super(databasePath);
    }

    /**
     * Generate schema and data files for the table
     *
     * @param query Create query string
     */
    @Override
    public void execute(String query) {
        Path schemaDirPath = Paths.get(dbDirectoryPath, "Schema");
        if (!Files.exists(schemaDirPath)) {
            try {
                Files.createDirectories(schemaDirPath);
            } catch (Exception e) {
                System.err.println("\nFailed to create schema directory: " + e.getMessage());
                return;
            }
        }

        String tableName = extractTableName(query);
        String[] columnType = extractColumnType(query);


       // Create the table schema file using the custom delimiter format
        Path schemaFilePath = schemaDirPath.resolve(tableName + "_schema.txt");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(schemaFilePath))) {
            for (int i = 0; i < columnType.length; i++) {
                String[] parts = columnType[i].trim().split("\\s+");
                if (parts.length == 2) {
                    writer.print(parts[0] + "=" + parts[1]);
                    if (i < columnType.length - 1) {
                        writer.print(";");
                    }
                }
            }
            System.out.println("\nTable schema for '" + tableName + "' created successfully.");
        } catch (Exception e) {
            System.err.println("\nFailed to create table schema file: " + e.getMessage());
        }



        // Create the table data file and write the column names as the first row
        Path tableDataPath = Paths.get(dbDirectoryPath, tableName + ".data");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tableDataPath))) {
            for (int i = 0; i < columnType.length; i++) {
                String columnName = columnType[i].trim().split("\\s+")[0];
                writer.print(columnName + (i < columnType.length - 1 ? "$" : ""));
            }
            writer.println();
            System.out.println("\nTable data file for '" + tableName + "' created successfully.");
        } catch (Exception e) {
            System.err.println("\nFailed to create table data file: " + e.getMessage());
        }
    }

    /**
     * Extracts the name of the table
     *
     * @param query Create query string
     * @return Name of the table
     */
    private String extractTableName(String query) {
        String[] parts = query.split("\\s+");
        return parts[2];
    }

    /**
     * Extracts the columns and their types
     *
     * @param query Create query string
     * @return Array of string containing column names and their types
     */
    private String[] extractColumnType(String query) {
        String columnsPart = query.substring(query.indexOf('(') + 1, query.lastIndexOf(')'));
        return columnsPart.split(",");
    }
}
