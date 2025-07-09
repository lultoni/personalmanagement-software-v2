package db;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import util.DatabaseGenerator;

/**
 * Diese Klasse verwaltet die Haupt-Datenbank, auf welcher die persistenten Daten gespeichert sind.
 *
 * @author Elias Glauert
 * @version 1.3
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
                    System.out.println(" - " + tableName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Debug Function that print out a full Table from the Database.
     * @param tableName Name of the Table that should be printed into the Console.
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

    public String getDbFilePath() {
        return dbFilePath;
    }
}
