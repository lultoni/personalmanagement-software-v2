package core;

import db.DatabaseManager;
import gui.GuiManager;

/**
 * Hauptklasse der HR-Management-Software.
 * Diese Klasse ist der Startpunkt für die gesamte Anwendung.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-05
 */
public class Main {

    /**
     * Die main-Methode ist der Startpunkt des Programms.
     * Hier werden die zentralen Manager initialisiert und die GUI im
     * Event-Dispatch-Thread (EDT) von Swing gestartet, um Thread-Sicherheit
     * zu gewährleisten.
     *
     * @param args Kommandozeilenargumente (werden in dieser Anwendung nicht verwendet).
     * @author Elias Glauert
     * @return void
     */
    // TODO überarbeite diese beschreibung
    public static void main(String[] args) {

        System.out.println("Initialisiere Kernkomponenten...");
        EventManager eventManager = new EventManager();
        DatabaseManager dbManager = new DatabaseManager();
        NotificationManager notificationManager = new NotificationManager(eventManager); // TODO wie genau sieht jetzt der notification manager aus?

        System.out.println("Starte grafische Benutzeroberfläche...");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GuiManager(eventManager); // TODO warum hat diese klasse auch den eventManager?
            }
        });

        System.out.println("Anwendung erfolgreich gestartet.");
    }
}
