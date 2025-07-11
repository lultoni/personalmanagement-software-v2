package core;

import db.DatabaseManager;
import gui.GuiManager;
import gui.views.LoginView;
import gui.views.TestNotificationView;

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
        dbManager = new DatabaseManager(false);
        dbManager.setupDatabase();
        backupManager = new DatabaseManager(true);
        backupManager.setupDatabase();
        EventManager eventManager = new EventManager(null, null, dbManager, backupManager);
        NotificationManager notificationManager = new NotificationManager(eventManager);
        eventManager.setNotificationManager(notificationManager);

        System.out.println("Starte grafische Benutzeroberfläche...");
        GuiManager guiManager = new GuiManager(eventManager);
        eventManager.setGuiManager(guiManager);

        eventManager.callEvent("changeView", new Object[]{new LoginView()});

        System.out.println("Anwendung erfolgreich gestartet.\n\n");

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
