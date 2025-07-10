package core;

import db.DatabaseManager;
import gui.GuiManager;
import gui.views.TestNotificationView;
import model.Employee;

import java.util.Calendar;
import java.util.Date;

/**
 * Hauptklasse der HR-Management-Software.
 * Diese Klasse ist der Startpunkt für die gesamte Anwendung.
 *
 * @author Elias Glauert
 * @version 1.5
 * @since 2025-07-05
 */
public class Main {

    /**
     * Nur für das korrekte Schließen des Programms wird der DatabaseManager als Variable hier erstellt.
     */
    private static DatabaseManager dbManager;
    private static DatabaseManager backupManager;

    /**
     * Die main-Methode ist der Startpunkt des Programms.
     * Hier werden die zentralen Manager initialisiert und das GUI gestartet.
     *
     * @param args Kommandozeilenargumente (werden in dieser Anwendung nicht verwendet).
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public static void main(String[] args) {

        System.out.println("Initialisiere Kernkomponenten...");
        EventManager eventManager = new EventManager(null, null);
        dbManager = new DatabaseManager(false);
        dbManager.setupDatabase();
        backupManager = new DatabaseManager(true);
        backupManager.setupDatabase();
        NotificationManager notificationManager = new NotificationManager(eventManager);
        eventManager.setNotificationManager(notificationManager);

        System.out.println("Starte grafische Benutzeroberfläche...");
        //javax.swing.SwingUtilities.invokeLater(() -> new GuiManager(eventManager));
        GuiManager guiManager = new GuiManager(eventManager);
        eventManager.setGuiManager(guiManager);
        //eventManager.callEvent("changeView", new Object[]{new DefaultView()});

        System.out.println("Anwendung erfolgreich gestartet.\n\n");

        eventManager.callEvent("changeView", new Object[]{new TestNotificationView(eventManager)});
        // TODO when we have a home screen / start screen we change this to the correct one
        //  (or do it before we print the message that it was started correctly)

        // --------- TEST AREA (REMOVE BEFORE FINAL VERSION) ----------
//        System.out.println("\n-----State before doing anything:-----");
//        dbManager.printAllTables();
//        dbManager.printTable("EMPLOYEES");
//        backupManager.printAllTables();
//        backupManager.printTable("EMPLOYEES");
//
//        System.out.println("\n-----After Adding an example employee:-----");
//        Employee employee = new Employee(
//                "max.mustermann",
//                "12345678",
//                "",
//                "Max",
//                "Mustermann",
//                "max.mustermann@btbc.de",
//                "+49 1234 5678910",
//                new Date(2007, Calendar.FEBRUARY, 9),
//                "Musterstraße 123, 14469 Potsdam",
//                'M',
//                new Date(2024, Calendar.SEPTEMBER, 1),
//                "null",
//                "null",
//                "null",
//                "null",
//                "null",
//                "null",
//                0,
//                dbManager);
//        System.out.println("normal db:");
//        dbManager.printTable("EMPLOYEES");
//        System.out.println("backup db:");
//        backupManager.printTable("EMPLOYEES");
//
//        System.out.println("\n-----After Making a backup:-----");
//        dbManager.copyDatabaseToOtherDbManager(backupManager);
//        System.out.println("normal db:");
//        dbManager.printTable("EMPLOYEES");
//        System.out.println("backup db:");
//        backupManager.printTable("EMPLOYEES");
//
//        System.out.println("\n-----After clearing normal db:-----");
//        dbManager.clearTable("EMPLOYEES");
//        System.out.println("normal db:");
//        dbManager.printTable("EMPLOYEES");
//        System.out.println("backup db:");
//        backupManager.printTable("EMPLOYEES");
//
//        System.out.println("\n-----After loading the backup:-----");
//        backupManager.copyDatabaseToOtherDbManager(dbManager);
//        System.out.println("normal db:");
//        dbManager.printTable("EMPLOYEES");
//        System.out.println("backup db:");
//        backupManager.printTable("EMPLOYEES");

    }

    /**
     * Debug Methode für das Ausgeben von args in der Konsole.
     * @param args Die Argumente aus einer Methode, welche zu einer Liste in einem String verwandelt werden sollen.
     * @return String, welcher die Liste der args-Objekte ist.
     * @author Elias Glauert
     */
    public static String argsToString(Object[] args) {
        if (args == null) return "null";
        String ret_string = "[";
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];
            ret_string += object.toString() + (i == args.length - 1 ? "" : ", ");
        }
        return ret_string + "]";
    }

    /**
     * Beendet das Programm, nachdem die Datenbankverbindungen getrennt wurden.
     * @author Elias Glauert
     */
    public static void exitProgram() {
        System.out.println("\nStarting Exit Process...");
        dbManager.disconnect();
        backupManager.disconnect();
        System.exit(0);
    }
}
