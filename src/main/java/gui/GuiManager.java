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
     */
    private ArrayList<View> view_history;

    /**
     * Das Haupt JFrame, in welches alle GUI Elemente angezeigt werden.
     */
    private MainFrame mainFrame;

    /**
     * EventManager Verbindung des GuiManagers.
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

    /**
     * Konstruktor für den GuiManager.
     *
     * @param notifications Die Liste der Benachrichtigungen die angezeigt werden sollen im GUI.
     * @author Elias Glauert
     */
    public void updateNotificationList(ArrayList<Notification> notifications) {
        // TODO call the mainframe and tell it to update the notification list based on the given notifications
    }

    /**
     * Wechselt den View und fügt den neuen auf den view_history-Stapel hinzu.
     *
     * @param view View auf den gewechselt wird.
     * @author Elias Glauert
     */
    public void changeView(View view) {
        view_history.add(view);
        mainFrame.changeView(view);
    }

    /**
     * Wechselt zum View vor dem aktuellen und entfernt den aktuellen aus der history.
     * Wenn die History zu klein ist, dann wird nichts getan.
     *
     * @author Elias Glauert
     */
    public void goToLastView() {
        if (view_history.size() <= 1) return;
        view_history.removeLast();
        mainFrame.changeView(view_history.getLast());
    }

}
