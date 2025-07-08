package core.events;

import core.EventManager;
import core.NotificationManager;
import core.events.Event;
import gui.views.View;

/**
 * Event für das Löschen von Benachrichtigungen.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-07
 */
public class Event_CreateNotification extends Event {

    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_CreateNotification(Object[] args) {
        super(args);

        NotificationManager notificationManager = (NotificationManager) args[0];
        notificationManager.createNotification(args[1].toString(), args[2].toString(), (View) args[3]);
    }
}
