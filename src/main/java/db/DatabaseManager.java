package db;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.io.InputStream;
import util.DatabaseGenerator;

/**
 * Diese Klasse verwaltet die Haupt-Datenbank, auf welcher die persistenten Daten gespeichert sind.
 *
 * @author Elias Glauert
 * @version 1.4
 * @since 2025-07-05
 */
public class DatabaseManager {

    private Connection connection;
    private String dbFilePath;
    private boolean isBackup;

    /**
     * Konstruktor für den DatabaseManager.
     * @author Elias Glauert
     */
    public DatabaseManager(boolean isBackup) {
        this.isBackup = isBackup;
        try {
            // Properties einstellen
            String file_location_properties = fetchPropertyName();
            System.out.println("Aufsetzen des DatabaseManagers für folgende Properties: " + file_location_properties);
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(file_location_properties)) {
                if (input == null) {
                    System.out.println("Fehler: konnte die " + file_location_properties + " nicht finden!");
                } else {
                    props.load(input);
                    System.out.println("Properties erfolgreich geladen.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dbFilePath = props.getProperty("db.url").replace("jdbc:h2:", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gives back the name of the Properties File based on if this is the backup DatabaseManager or not.
     * @author Elias Glauert
     */
    private String fetchPropertyName() {
        return isBackup ? "backup.properties" : "database.properties";
    }

    /**
     * Gives back the name of the database based on if this is the backup DatabaseManager or not.
     * @author Elias Glauert
     */
    private String fetchClearName() {
        return fetchPropertyName().substring(0, fetchPropertyName().length() == 17 ? 6 : 8);
    }

    /**
     * Methode zum Überprüfen und Erstellen der Datenbank
     * @author Elias Glauert
     */
    public void setupDatabase() {
        DatabaseGenerator.setDbManager(this);
        DatabaseGenerator.setupDatabase();
    }

    /**
     * Methode zum Öffnen einer Verbindung
     * @author Elias Glauert
     */
    public void connect() {
        try {
            Properties props = new Properties();
            try (InputStream input = getClass().getResourceAsStream("/" + fetchPropertyName())) {
                props.load(input);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("connect() - Verbindung zu " + fetchClearName() + " hergestellt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode zum Schließen der Verbindung
     * @author Elias Glauert
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("disconnect() - Verbindung zu " + fetchClearName() + " geschlossen.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Methode zum Abrufen der Verbindung
     * @author Elias Glauert
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect(); // Reconnect if necessary
        }
        return connection;
    }

    /**
     * Debug Function that prints out all the Tables in the database without the content.
     * @author Elias Glauert
     */
    public void printAllTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Get the metadata of the database
            DatabaseMetaData metaData = conn.getMetaData();

            // Retrieve all tables in the current database
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
                System.out.println("List of tables in the " + fetchClearName() + ":");
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println(" - " + (isSystemTable(tableName) ? "(sys) " : "") + tableName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Debug function that print out a full table from the database.
     * @param tableName Name of the table that should be printed into the console.
     * @author Elias Glauert
     */
    public void printTable(String tableName) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Execute a query to fetch all content from the table
            String query = "SELECT * FROM " + tableName;
            try (ResultSet rs = stmt.executeQuery(query)) {
                // Get metadata of the result set for column-count and names
                int columnCount = rs.getMetaData().getColumnCount();

                // Print column names
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();

                // Print each row of the result set
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears all elements from a table.
     * @param tableName Name of the table that should be cleared.
     * @author Elias Glauert
     */
    public void clearTable(String tableName) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Formulate the SQL command to delete all rows in the specified table
            String sql = "DELETE FROM " + tableName;

            // Execute the command
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("clearTable() - " + rowsAffected + " rows deleted from " + tableName);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("clearTable() - Error clearing table: " + tableName);
        }
    }

    /**
     * Copies the own DB-Content to the DB of the other DbManager, so that the other DB's old data is lost.
     * @param otherDatabaseManager DbManager whose DB will be overwritten.
     * @author Elias Glauert
     */
    public void copyDatabaseToOtherDbManager(DatabaseManager otherDatabaseManager) {
        try {
            // Ensure both connections are active
            this.connect();
            otherDatabaseManager.connect();

            // Prepare to copy data
            Connection sourceConnection = this.getConnection();
            Connection targetConnection = otherDatabaseManager.getConnection();

            DatabaseMetaData metaData = sourceConnection.getMetaData();

            // Retrieve all tables from the source database
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                Statement sourceStmt = sourceConnection.createStatement();
                Statement targetStmt = targetConnection.createStatement();

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    // Filter out system tables by checking the schema name or using specific table name checks
                    if (!isSystemTable(tableName)) {
                        try {
                            // Drop the table in the target database if it exists (to clear it)
                            targetStmt.execute("DROP TABLE IF EXISTS " + tableName);

                            // Create table structure in the target database
                            ResultSet columnInfo = metaData.getColumns(null, null, tableName, null);
                            StringBuilder createTableSQL = new StringBuilder("CREATE TABLE " + tableName + " (");

                            // Track columns to avoid duplicates
                            HashSet<String> columnNamesSet = new HashSet<>();

                            while (columnInfo.next()) {
                                String columnName = columnInfo.getString("COLUMN_NAME");

                                // Check if the column name has not been added before to avoid duplicates
                                if (!columnNamesSet.contains(columnName)) {
                                    columnNamesSet.add(columnName);
                                    String columnType = columnInfo.getString("TYPE_NAME");
                                    int columnSize = columnInfo.getInt("COLUMN_SIZE");

                                    // Handle types and sizes
                                    createTableSQL.append(columnName).append(" ").append(columnType);
                                    if (columnType.equalsIgnoreCase("VARCHAR")) {
                                        createTableSQL.append("(").append(columnSize).append(")");
                                    }
                                    createTableSQL.append(", ");
                                }
                            }
                            if (createTableSQL.charAt(createTableSQL.length() - 2) == ',') {
                                createTableSQL.deleteCharAt(createTableSQL.length() - 2); // Remove trailing comma
                            }
                            createTableSQL.append(")");

                            targetStmt.execute(createTableSQL.toString());

                            // Copy data from source table to target table
                            try (ResultSet rs = sourceStmt.executeQuery("SELECT * FROM " + tableName)) {
                                ResultSetMetaData rsMetaData = rs.getMetaData();
                                int columnCount = rsMetaData.getColumnCount();

                                // Prepare insert statement for target database
                                StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                                for (int i = 0; i < columnCount; i++) {
                                    insertSQL.append("?");
                                    if (i < columnCount - 1) insertSQL.append(", ");
                                }
                                insertSQL.append(")");

                                // Insert data into the target table
                                try (PreparedStatement insertStmt = targetConnection.prepareStatement(insertSQL.toString())) {
                                    while (rs.next()) {
                                        for (int i = 1; i <= columnCount; i++) {
                                            insertStmt.setObject(i, rs.getObject(i));
                                        }
                                        insertStmt.executeUpdate();
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();  // Log error for table creation or data copying
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Global error logging
        }
    }

    /**
     * Determines if a table is a system table based on its name.
     * Modify this method based on the actual system tables found in your environment.
     * @param tableName the name of the table to check.
     * @return true if the table is a system table, false otherwise.
     */
    private boolean isSystemTable(String tableName) {
        // Add the names of system tables you wish to exclude
        HashSet<String> systemTables = new HashSet<>(Arrays.asList(
                "CONSTANTS", "ENUM_VALUES", "INDEXES", "INDEX_COLUMNS",
                "INFORMATION_SCHEMA_CATALOG_NAME", "IN_DOUBT", "LOCKS",
                "QUERY_STATISTICS", "RIGHTS", "ROLES", "SESSIONS",
                "SESSION_STATE", "SETTINGS", "SYNONYMS", "USERS"));

        return systemTables.contains(tableName);
    }

    /**
     * Führt ein Update auf die Datenbank aus.
     * @param sqlQuery SQL-Befehl, der ausgeführt werden soll. Beispiele könnten INSERT, UPDATE und DELETE sein.
     * @author Elias Glauert
     */
    public void executeUpdate(String sqlQuery) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(sqlQuery);
            System.out.println("executeUpdate() - Befehl ausgeführt, betroffene Zeilen: " + rowsAffected);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("executeUpdate() - Fehler beim Ausführen des Updates.");
        }
    }

    public String getDbFilePath() {
        return dbFilePath;
    }
}