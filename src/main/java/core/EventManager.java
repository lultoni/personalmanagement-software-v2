package core;

import core.events.*;
import gui.GuiManager;

import static core.Main.argsToString;

/**
 * EventManager-Klasse der HR-Management-Software.
 * Diese Klasse verwaltet alle Events, die im Programm stattfinden.
 *
 * @author Elias Glauert
 * @version 1.4
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
        System.out.println(" ~ db ~ callEvent('" + event_id + "', " + argsToString(args) + ")");
        switch (event_id) {
            case "updateNotification" -> {
                System.out.println("   | updateNotification Event Creation");
                new Event_UpdateNotification(new Object[]{guiManager, notificationManager.getNotification_list()});
            }
            case "changeView" -> {
                System.out.println("   | changeView Event Creation");
                new Event_ChangeView(new Object[]{guiManager, args[0]});
            }
            case "popNotification" -> {
                System.out.println("   | popNotification Event Creation");
                new Event_PopNotification(new Object[]{notificationManager, args[0]});
            }
            case "createNotification" -> {
                System.out.println("   | createNotification Event Creation");
                new Event_CreateNotification(new Object[]{notificationManager, args[0], args[1], args[2]});
            }
            case "moveBackView" -> {
                System.out.println("   | moveBackView Event Creation");
                new Event_MoveBackView(new Object[]{guiManager});
            }
        }
    }

    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

}