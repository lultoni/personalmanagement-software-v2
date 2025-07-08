package util;

import java.sql.Connection;
import java.sql.Statement;
import db.DatabaseManager;

/**
 * Mit dieser Klasse wird eine Datenbank erstellt nach den database.properties, falls keine eigene vorhanden ist.
 * Klasse wird statisch verwendet.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class DatabaseGenerator {

    /**
     * Statische Methode zum Erstellen der Tabellen
     */
    public static void createTables() {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // TODO überarbeiten dieses Beispieles zur finalen table struktur
            String createEmployeesTable = "CREATE TABLE IF NOT EXISTS Employees (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(255))";

            stmt.execute(createEmployeesTable);
            System.out.println("Tabelle 'Employees' wurde erstellt.");

            // Weitere Tabellen können hier erstellt werden
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbManager.disconnect();
        }
    }
}