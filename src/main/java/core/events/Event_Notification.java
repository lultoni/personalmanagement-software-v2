package core.events;

import core.Notification;
import core.exceptions.ArrayWrongSizeException;
import gui.GuiManager;

import java.util.ArrayList;

public class Event_Notification extends Event {


    /**
     * Konstruktor für Event_Notification.
     *
     *
     * @param args Erwartet ein Array mit der Größe 2, wo das erste Objekt ein GuiManager ist und das zweite eine ArrayList der Art Notification.
     * @author Elias Glauert
     */
    public Event_Notification(Object[] args) {
        super(args);

        // if (args.length != 2) throw new ArrayWrongSizeException(2, args.length);

        GuiManager guiManager = (GuiManager) args[0];
        guiManager.updateNotificationList((ArrayList<Notification>) args[1]);
    }

}
