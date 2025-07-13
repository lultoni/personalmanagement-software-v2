package core.events;

import core.Notification;
import gui.GuiManager;

import java.util.ArrayList;

import static core.Main.argsToString;

/**
 * Event für das Aktualisieren der Benachrichtigungsliste im GUI.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-07
 */
public class Event_UpdateNotification extends Event {


    /**
     * Konstruktor für Event_Notification.
     *
     * @param args Erwartet ein Array mit der Größe 2, wo das erste Objekt ein GuiManager ist und das zweite eine ArrayList der Art Notification.
     * @author Elias Glauert
     */
    public Event_UpdateNotification(Object[] args) {
        super(args);

        // if (args.length != 2) throw new ArrayWrongSizeException(2, args.length);

        System.out.println(" ~ db ~ Event_ChangeView Konstruktor mit args: " + argsToString(args));

        GuiManager guiManager = (GuiManager) args[0];
        guiManager.updateNotificationList((ArrayList<Notification>) args[1], (boolean) args[2]);
    }

}
