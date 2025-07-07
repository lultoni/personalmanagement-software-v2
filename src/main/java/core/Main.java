package core;

import db.DatabaseManager;
import gui.GuiManager;
import gui.views.DefaultView;
import gui.views.TestNotificationView;

/**
 * Hauptklasse der HR-Management-Software.
 * Diese Klasse ist der Startpunkt für die gesamte Anwendung.
 *
 * @author Elias Glauert
 * @version 1.1
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

        System.out.println("Anwendung erfolgreich gestartet.");

        eventManager.callEvent("changeView", new Object[]{new TestNotificationView(eventManager)});
    }
}
