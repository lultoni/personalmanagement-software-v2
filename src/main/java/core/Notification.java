package core;

import gui.views.View;

/**
 * Fachklasse für Benachrichtigungen.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-07
 */
public class Notification {

    private String notification_titel;
    private String notification_description;
    private View connected_view;
    private int notification_id;

    /**
     * Konstruktor für die Benachrichtigungen.
     *
     * @param -
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public Notification(String notification_titel, String notification_description, View connected_view, int notification_id) {

        this.notification_titel = notification_titel;
        this.notification_description = notification_description;
        this.connected_view = connected_view;
        this.notification_id = notification_id;

    }

    public int getNotification_id() {
        return notification_id;
    }

    public String getNotification_titel() {
        return notification_titel;
    }

    public String getNotification_description() {
        return notification_description;
    }

    public View getConnected_view() {
        return connected_view;
    }
}
