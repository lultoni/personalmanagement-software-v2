package core;

import core.events.Event_Notification;
import core.exceptions.ArrayWrongSizeException;
import gui.GuiManager;

/**
 * EventManager-Klasse der HR-Management-Software.
 * Diese Klasse verwaltet alle Events, die im Programm stattfinden.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class EventManager {

    private GuiManager guiManager;
    private NotificationManager notificationManager;

    /**
     * Konstruktor für den EventManager.
     *
     * @param -
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public EventManager(GuiManager guiManager, NotificationManager notificationManager) {

        this.guiManager = guiManager;
        this.notificationManager = notificationManager;

    }

    public void callEvent(String event_id) {
        switch (event_id) {
            case "notification" -> new Event_Notification(new Object[]{guiManager, notificationManager.getNotification_list()});
        }
    }

}