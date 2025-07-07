package core;

import gui.views.View;

import java.util.ArrayList;

/**
 * Diese Klasse verwaltet die Benachrichtigungen, die der Benutzer erhält.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class NotificationManager {

    /**
     * EventManager Verbindung des NotificationManagers.
     * @author Elias Glauert
     */
    private EventManager eventManager;

    /**
     * Liste aller Benachrichtigungen die den User betreffen.
     * @author Elias Glauert
     */
    private ArrayList<Notification> notification_list;

    /**
     * Used for notification_id's. Counts up for every created Notification.
     */
    private int notification_counter;

    /**
     * Konstruktor für den NotificationManager.
     *
     * @param eventManager EventManager Verbindung für den NotificationManager.
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public NotificationManager(EventManager eventManager) {

        this.eventManager = eventManager;
        notification_list = new ArrayList<>();

    }

    /**
     * Erstellt eine Benachrichtigung und schickt das entsprechende Event raus.
     *
     * @param -
     * @author Elias Glauert
     */
    public void createNotification(String title, String description, View view) {

        Notification notification = new Notification(title, description, view, notification_counter);
        notification_counter++;
        notification_list.add(notification);

        eventManager.callEvent("updateNotification", null);

    }

    /**
     * Löscht eine Benachrichtigung und schickt das entsprechende Event raus.
     *
     * @param -
     * @author Elias Glauert
     */
    public void popNotification(int notification_id) {

        for (int index = 0; index < notification_list.size(); index++) {
            if (notification_list.get(index).getNotification_id() == notification_id) {
                notification_list.remove(index);
                break;
            }
        }

        eventManager.callEvent("updateNotification", null);

    }

    public ArrayList<Notification> getNotification_list() {
        return notification_list;
    }
}
