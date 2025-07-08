package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.util.Properties;
import java.io.InputStream;
import util.DatabaseGenerator;

/**
 * Diese Klasse verwaltet die Haupt-Datenbank, auf welcher die persistenten Daten gespeichert sind.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class DatabaseManager {

    private Connection connection;
    private String dbFilePath;

    /**
     * Konstruktor für den DatabaseManager.
     * @author Elias Glauert
     */
    public DatabaseManager() {
        try {
            // Properties einstellen
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    System.out.println("Fehler: konnte die database.properties nicht finden!");
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
     * Methode zum Überprüfen und Erstellen der Datenbank
     * @author Elias Glauert
     */
    public void setupDatabase() {
        File dbFile = new File(dbFilePath + ".mv.db"); // H2-Datenbank speichert Dateien mit der Endung .mv.db
        if (!dbFile.exists()) {
            System.out.println("Datenbankdatei nicht vorhanden. Generiere neue Datei...");
            DatabaseGenerator.createTables();
        } else {
            System.out.println("Datenbankdatei vorhanden. Verbindung wird nur hergestellt...");
        }
        connect();
    }

    /**
     * Methode zum Öffnen einer Verbindung
     * @author Elias Glauert
     */
    public void connect() {
        try {
            Properties props = new Properties();
            try (InputStream input = getClass().getResourceAsStream("/database.properties")) {
                props.load(input);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung zur Datenbank hergestellt.");
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
                System.out.println("Verbindung zur Datenbank geschlossen.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Methode zum Abrufen der Verbindung
     * @author Elias Glauert
     */
    public Connection getConnection() {
        return connection;
    }
}
