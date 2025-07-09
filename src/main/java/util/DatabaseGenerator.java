package util;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import db.DatabaseManager;

/**
 * Mit dieser Klasse wird eine Datenbank erstellt nach den database.properties, falls keine eigene vorhanden ist.
 * Klasse wird statisch verwendet.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-05
 */
public class DatabaseGenerator {

    private static DatabaseManager dbManager = null;

    /**
     * Statische Methode zum Erstellen der Tabellen
     * @author Elias Glauert
     */
    public static void createTables() {
        dbManager.connect();

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            String createEmployeesTable = SqlReader.giveCommand("createTableEmployees");

            stmt.execute(createEmployeesTable);
            System.out.println("Tabelle 'Employees' wurde erstellt.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbManager.disconnect();
        }
    }

    /**
     * Erstellt die Datenbank-Dateien, falls sie noch nicht vorhanden sind.
     * @author Elias Glauert
     */
    public static void setupDatabase() {
        File dbFile = new File(dbManager.getDbFilePath() + ".mv.db");
        if (!dbFile.exists()) {
            System.out.println("Datenbankdatei nicht vorhanden. Generiere neue Datei...");
            DatabaseGenerator.createTables();
        } else {
            System.out.println("Datenbankdatei vorhanden. Verbindung wird nur hergestellt...");
        }
        dbManager.connect();
    }

    public static void setDbManager(DatabaseManager dbManager1) {
        dbManager = dbManager1;
    }
}