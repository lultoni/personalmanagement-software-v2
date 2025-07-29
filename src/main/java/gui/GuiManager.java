package gui;

import core.EventManager;
import core.LoginManager;
import core.Notification;
import gui.views.View;
import util.PersistentInformationReader;

import java.util.ArrayList;

/**
 * Diese Klasse verwaltet das GUI.
 *
 * @author Elias Glauert
 * @version 1.7
 * @since 2025-07-05
 */
public class GuiManager {

    private ArrayList<View> view_history;
    private int currentViewIndex;
    private final int fallbackCurrentViewIndex = -1;

    private MainFrame mainFrame;
    private EventManager eventManager;

    public GuiManager(EventManager eventManager, LoginManager loginManager) {
        this.eventManager = eventManager;
        this.view_history = new ArrayList<>();
        this.currentViewIndex = fallbackCurrentViewIndex;

        mainFrame = new MainFrame(new ArrayList<>(), eventManager, loginManager, this);
    }

    public void updateNotificationList(ArrayList<Notification> notifications, boolean closePopUp) {
        System.out.println(" ~ db ~ gui.GuiManager.updateNotificationList()");
        System.out.println("   | closePopUp: " + closePopUp);
        mainFrame.setNotifications(notifications, closePopUp);
    }

    public void updateNotifications() {
        System.out.println(" ~ db ~ gui.GuiManager.updateNotifications()");
        mainFrame.updateNotifications();
    }

    public void changeView(View view) {
        System.out.println(" ~ db ~ gui.GuiManager.changeView(" + view.toString() + ")");

        if (PersistentInformationReader.isSystemBlocked()) {
            System.out.println("   | System is Blocked");
            handleBlockedStates(view);
            return;
        }

        if (view.getView_id().equals("view-login")) {
            loginViewChangeLogic(view);
            return;
        } else if (view.getView_id().equals("view-blocked")) {
            System.out.println("   | System is not Blocked. No Block Screen allowed, returning.");
            return;
        }

        if (!isCurrentView(view)) {
            System.out.println("   | Usual Case, working as intended.");
            viewChangeLogic(view);
        } else {
            System.out.println("   | Call tries to change to the already shown view, returning.");
        }
    }

    private void handleBlockedStates(View view) {
        if (view.getView_id().equals("view-login")) {
            loginViewChangeLogic(view);
        } else if (view.getView_id().equals("view-blocked")) {
            System.out.println("   | Move to Blocked-System Screen is still allowed, acting as usual.");
            viewChangeLogic(view);
        } else {
            System.out.println("   | System is Blocked. No other view is allowed, returning.");
        }
    }

    private void loginViewChangeLogic(View view) {
        System.out.println("   | Going to Login Screen, resetting the View Tree.");
        view_history.clear();
        view_history.add(view); // fühle ich nicht, wenn man durch die back buttons zum login screen wieder kommt …
        currentViewIndex = 0;
        mainFrame.changeView(view, shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled());
    }

    private boolean isCurrentView(View view) {
        return currentViewIndex >= 0 && view.equals(getActiveView());
    }

    private void viewChangeLogic(View view) {
        // da hier zu einem neuen view gegangen wird, werden alle 'zukünftigen' views entfernt
        while (view_history.size() > currentViewIndex + 1) {
            view_history.removeLast();
        }

        view_history.add(view);
        currentViewIndex = view_history.size() - 1;
        printViewHistory();
        mainFrame.changeView(view, shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled());
    }

    private boolean shouldForwardButtonBeEnabled() {
        return currentViewIndex < view_history.size() - 1;
    }

    private boolean shouldBackButtonBeEnabled() {
        return currentViewIndex > 0;
    }

    private void printViewHistory() {
        System.out.println("   | GuiManager - View History (Newest at the bottom):");
        for (int i = 0; i < view_history.size(); i++) {
            View view = view_history.get(i);
            String indicator = (i == currentViewIndex) ? " * " : "   ";
            System.out.println("      |" + indicator + view.toString());
        }
    }

    public void goToLastView() {
        System.out.println(" ~ db ~ gui.GuiManager.goToLastView()");
        System.out.println("   | view_history.size()=" + view_history.size());

        if (currentViewIndex <= 0) {
            System.out.println("   | view_history.size() too small! No further back-steps possible!");
            return;
        }

        currentViewIndex--;
        printViewHistory();
        mainFrame.changeView(getActiveView(), shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled());
    }

    public void goToNextView() {
        System.out.println(" ~ db ~ gui.GuiManager.goToNextView()");
        System.out.println("   | view_history.size()=" + view_history.size());

        if (currentViewIndex >= view_history.size() - 1) {
            System.out.println("   | Already on the most recent view, no forward steps possible.");
            return;
        }

        currentViewIndex++;
        printViewHistory();
        mainFrame.changeView(getActiveView(), shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled());
    }

    public View getActiveView() {
        return view_history.get(currentViewIndex);
    }
}