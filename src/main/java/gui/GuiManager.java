package gui;


import core.EventManager;
import core.Notification;
import gui.views.View;
import util.PersistentInformationReader;

import java.util.ArrayList;

/**
 * Diese Klasse verwaltet das GUI.
 *
 * @author Elias Glauert
 * @version 1.4
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
     * @param eventManager EventManager Verbindung für den GuiManager.
     * @author Elias Glauert
     */
    public GuiManager(EventManager eventManager) {

        this.eventManager = eventManager;

        view_history = new ArrayList<>();

        mainFrame = new MainFrame(new ArrayList<>(), eventManager);

    }

    /**
     * Gibt der mainFrame den Call, dass die Notifications aktualisiert werden soll.
     * @param notifications Die Liste der Benachrichtigungen die angezeigt werden sollen im GUI.
     * @author Elias Glauert
     */
    public void updateNotificationList(ArrayList<Notification> notifications, boolean closePopUp) {
        System.out.println(" ~ db ~ gui.GuiManager.updateNotificationList()");
        System.out.println("   | closePopUp: " + closePopUp);
        mainFrame.setNotifications(notifications, closePopUp);
    }

    /**
     * Gibt der mainFrame den Call, dass die Notifications aktualisiert werden soll.
     * @author Elias Glauert
     */
    public void updateNotifications() {
        System.out.println(" ~ db ~ gui.GuiManager.updateNotifications()");
        mainFrame.updateNotifications();
    }

    /**
     * Wechselt den View und fügt den neuen auf den view_history-Stapel hinzu.
     * @param view View auf den gewechselt wird.
     * @author Elias Glauert
     */
    public void changeView(View view) {
        System.out.println(" ~ db ~ gui.GuiManager.changeView(" + view.toString() + ")");

        if (PersistentInformationReader.isSystemBlocked()) {
            System.out.println("   | System is Blocked");
            if (view.getView_id().equals("view-login")) {
                System.out.println("   | Move to Login Screen is still allowed");
                view_history = new ArrayList<>();
                // Reset, weil wir 'Logged Out' sind und damit keine Views in der Session wollen
                mainFrame.changeView(view, false);
            } else if (view.getView_id().equals("view-blocked")) {
                System.out.println("   | Move to Blocked-System Screen is still allowed");

                view_history.add(view);
                printViewHistory();
                mainFrame.changeView(view, false);
            } else {
                System.out.println("   | System is Blocked. No other view is allowed, returning.");
                return;
            }
        }

        if (view.getView_id().equals("view-login")) {
            view_history = new ArrayList<>();
            mainFrame.changeView(view, false);
            return;
        } else if (view.getView_id().equals("view-blocked")) {
            System.out.println("   | System is not Blocked. No Block Screen allowed, returning.");
            return;
        }

        if (!view_history.isEmpty() && view.equals(view_history.getLast())) {
            System.out.println("   | Call tries to change to the already shown view, returning.");
            return;
        }

        view_history.add(view);
        printViewHistory();
        mainFrame.changeView(view, view_history.size() > 1);
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
        mainFrame.changeView(view_history.getLast(), view_history.size() > 1);

    }
}
