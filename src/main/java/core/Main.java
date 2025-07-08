package core;

import db.DatabaseManager;
import gui.GuiManager;
import gui.views.TestNotificationView;

/**
 * Hauptklasse der HR-Management-Software.
 * Diese Klasse ist der Startpunkt für die gesamte Anwendung.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-05
 */
public class Main {

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
        DatabaseManager dbManager = new DatabaseManager(); // TODO finish this setup
        NotificationManager notificationManager = new NotificationManager(eventManager);
        eventManager.setNotificationManager(notificationManager);

        System.out.println("Starte grafische Benutzeroberfläche...");
        //javax.swing.SwingUtilities.invokeLater(() -> new GuiManager(eventManager));
        GuiManager guiManager = new GuiManager(eventManager);
        eventManager.setGuiManager(guiManager);
        //eventManager.callEvent("changeView", new Object[]{new DefaultView()});

        System.out.println("Anwendung erfolgreich gestartet.\n\n");

        eventManager.callEvent("changeView", new Object[]{new TestNotificationView(eventManager)});
        // TODO when we have a home screen / start screen we change this to the correct one (or do it before we print the message that it was started correctly)
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
}
