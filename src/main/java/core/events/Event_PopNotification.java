package core.events;

import core.EventManager;
import core.NotificationManager;
import core.events.Event;

/**
 * Event für das Löschen von Benachrichtigungen.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-07
 */
public class Event_PopNotification extends Event {


    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_PopNotification(Object[] args) {
        super(args);

        NotificationManager notificationManager = (NotificationManager) args[0];
        notificationManager.popNotification((int) args[1]);
        EventManager eventManager = (EventManager) args[2];
        eventManager.callEvent("updateNotification", null);
    }
}
