package core;

import core.events.*;
import db.DatabaseManager;
import gui.GuiManager;
import gui.views.TestNotificationView;
import gui.views.WelcomeView;

import static core.Main.argsToString;

/**
 * EventManager-Klasse der HR-Management-Software.
 * Diese Klasse verwaltet alle Events, die im Programm stattfinden.
 *
 * @author Elias Glauert
 * @version 1.6
 * @since 2025-07-05
 */
public class EventManager {

    private GuiManager guiManager;
    private NotificationManager notificationManager;
    private DatabaseManager mainManager;
    private DatabaseManager backupManager;
    private EmployeeManager employeeManager;

    /**
     * Konstruktor für den EventManager.
     *
     * @param guiManager Verbindung zum GuiManager.
     * @param notificationManager Verbindung zum Notification Manager.
     * @author Elias Glauert
     */
    public EventManager(GuiManager guiManager, NotificationManager notificationManager, DatabaseManager mainManager, DatabaseManager backupManager, EmployeeManager employeeManager) {

        this.guiManager = guiManager;
        this.notificationManager = notificationManager;
        this.mainManager = mainManager;
        this.backupManager = backupManager;
        this.employeeManager = employeeManager;

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
                System.out.println("   | " + event_id + " Event Creation");
                new Event_UpdateNotification(new Object[]{guiManager, notificationManager.getNotification_list(), args[0]});
            }
            case "changeView" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_ChangeView(new Object[]{guiManager, args[0]});
            }
            case "popNotification" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_PopNotification(new Object[]{notificationManager, args[0]});
            }
            case "createNotification" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_CreateNotification(new Object[]{notificationManager, args[0], args[1], args[2]});
            }
            case "moveBackView" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_MoveBackView(new Object[]{guiManager});
            }
            case "moveForwardView" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_MoveForwardView(new Object[]{guiManager});
            }
            case "createBackup" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_CreateBackup(new Object[]{mainManager, backupManager});
            }
            case "loadBackup" -> {
                System.out.println("   | " + event_id + " Event Creation");
                new Event_LoadBackup(new Object[]{mainManager, backupManager});
            }
            case "moveToHomeScreen" -> {
                callEvent("changeView", new Object[]{new WelcomeView(employeeManager)});
            }
            default -> System.out.println("   | Unexpected event_id '" + event_id + "'.");
        }
    }

    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

}