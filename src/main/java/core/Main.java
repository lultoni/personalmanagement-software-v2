package core;

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
        // DatabaseManager dbManager = new DatabaseManager();
        // NotificationManager notificationManager = new NotificationManager(eventManager);


        // 2. Grafische Benutzeroberfläche (GUI) starten
        // Es ist wichtig, GUI-Operationen immer im Event-Dispatch-Thread auszuführen,
        // um potenzielle Probleme mit der nebenläufigen Ausführung in Swing zu vermeiden.
        // SwingUtilities.invokeLater stellt sicher, dass der Code im richtigen Thread läuft.
        System.out.println("Starte grafische Benutzeroberfläche...");
        /*
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Der GuiManager ist verantwortlich für das Erstellen und Anzeigen
                // des Hauptfensters und der ersten Ansicht (LoginView).
                new GuiManager(eventManager);
            }
        });
        */

        System.out.println("Anwendung erfolgreich gestartet.");
    }
}
