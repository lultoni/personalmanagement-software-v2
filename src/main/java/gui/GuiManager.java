package gui;


import core.EventManager;
import core.Notification;
import gui.views.View;

import java.util.ArrayList;

/**
 * Diese Klasse verwaltet das GUI.
 *
 * @author Elias Glauert
 * @version 1.2
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

        mainFrame = new MainFrame(new ArrayList<>(), eventManager);

    }

    /**
     * Gibt der mainFrame den Call, dass die Notifications aktualisiert werden soll.
     *
     * @param notifications Die Liste der Benachrichtigungen die angezeigt werden sollen im GUI.
     * @author Elias Glauert
     */
    public void updateNotificationList(ArrayList<Notification> notifications) {
        System.out.println(" ~ db ~ gui.GuiManager.updateNotificationList()");
        mainFrame.setNotifications(notifications);
    }

    /**
     * Wechselt den View und fügt den neuen auf den view_history-Stapel hinzu.
     *
     * @param view View auf den gewechselt wird.
     * @author Elias Glauert
     */
    public void changeView(View view) {
        System.out.println(" ~ db ~ gui.GuiManager.changeView(" + view.toString() + ")");
        view_history.add(view);
        printViewHistory();
        mainFrame.changeView(view);
    }

    /**
     * Print the current View History into the console.
     * @author Elias Glauert
     */
    private void printViewHistory() {
        System.out.println("   | GuiManager - View History (Newest at the bottom):");
        for (View view: view_history) {
            System.out.println("   |  - " + view.toString());
        }
    }

    /**
     * Wechselt zum View vor dem aktuellen und entfernt den aktuellen aus der history.
     * Wenn die History zu klein ist, dann wird nichts getan.
     *
     * @author Elias Glauert
     */
    public void goToLastView() {

        System.out.println(" ~ db ~ gui.GuiManager.goToLastView()");
        System.out.println("   | view_history.size()=" + view_history.size());

        if (view_history.size() <= 1) {
            System.out.println("   | view_history.size() too small! No further back-steps possible!");
            return;
        }

        view_history.removeLast();
        printViewHistory();
        mainFrame.changeView(view_history.getLast());

    }

}
