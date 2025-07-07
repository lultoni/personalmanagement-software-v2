package gui;


import core.EventManager;
import core.Notification;
import gui.views.View;

import java.util.ArrayList;

/**
 * Diese Klasse verwaltet das GUI.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class GuiManager {

    /**
     * Enthält den View-Stack der Session. Wird für den Zurück-Knopf genutzt.
     * @author Elias Glauert
     */
    private ArrayList<View> view_history;

    /**
     * Das Haupt JFrame, in welches alle GUI Elemente angezeigt werden.
     * @author Elias Glauert
     */
    private MainFrame mainFrame;

    /**
     * EventManager Verbindung des GuiManagers.
     * @author Elias Glauert
     */
    private EventManager eventManager;

    /**
     * Konstruktor für den GuiManager.
     *
     * @param eventManager EventManager Verbindung für den GuiManager.
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public GuiManager(EventManager eventManager) {

        this.eventManager = eventManager;

        view_history = new ArrayList<>();

        mainFrame = new MainFrame();

    }

    public void updateNotificationList(ArrayList<Notification> notifications) {
        // TODO call the mainframe and tell it to update the notification list based on the given notifications
    }

}
