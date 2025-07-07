package core;

import core.events.Event_ChangeView;
import core.events.Event_PopNotification;
import core.events.Event_UpdateNotification;
import gui.GuiManager;

/**
 * EventManager-Klasse der HR-Management-Software.
 * Diese Klasse verwaltet alle Events, die im Programm stattfinden.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-05
 */
public class EventManager {

    private GuiManager guiManager;
    private NotificationManager notificationManager;

    /**
     * Konstruktor für den EventManager.
     *
     * @param guiManager Verbindung zum GuiManager.
     * @param notificationManager Verbindung zum Notification Manager.
     * @author Elias Glauert
     */
    public EventManager(GuiManager guiManager, NotificationManager notificationManager) {

        this.guiManager = guiManager;
        this.notificationManager = notificationManager;

    }

    /**
     * Hauptfunktion um ein Event aufzurufen. Passt sich flexibel nach den Parametern an, was genau es macht.
     * @param event_id Die ID vom Event, welches aufgerufen werden soll.
     * @param args Weiterführende Einstellungen für den Event-Call, welche pro Szenario anders genutzt werden können.
     * @author Elias Glauert
     */
    public void callEvent(String event_id, Object[] args) {
        switch (event_id) {
            case "updateNotification" -> new Event_UpdateNotification(new Object[]{guiManager, notificationManager.getNotification_list()});
            case "changeView" -> new Event_ChangeView(new Object[]{guiManager, args[0]});
            case "popNotification" -> new Event_PopNotification(new Object[]{notificationManager, args[0], this});
        }
    }

    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

}