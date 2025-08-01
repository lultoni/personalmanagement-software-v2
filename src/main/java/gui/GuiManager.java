package gui;

import core.EmployeeManager;
import core.EventManager;
import core.LoginManager;
import core.Notification;
import gui.views.View;
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import gui.views.LoginView;


/**
 * Diese Klasse verwaltet das GUI.
 *
 * @author Elias Glauert
 * @version 1.8
 * @since 2025-07-05
 */
public class GuiManager {

    private ArrayList<View> view_history;
    private int currentViewIndex;
    private final int fallbackCurrentViewIndex = -1;

    private MainFrame mainFrame;

    private static GuiManager instance;


    public GuiManager(EventManager eventManager, LoginManager loginManager, EmployeeManager employeeManager) {
        this.view_history = new ArrayList<>();
        this.currentViewIndex = fallbackCurrentViewIndex;

        mainFrame = new MainFrame(new ArrayList<>(), eventManager, loginManager, this, employeeManager);
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
            resetViewHistory();
            System.out.println("   | Move to Login Screen");
            mainFrame.changeView(view, false, false, true);
            return;
        } else if (view.getView_id().equals("view-blocked")) {
            System.out.println("   | System is not Blocked. No Block Screen allowed, returning.");
            return;
        }

        if (!isCurrentView(view)) {
            System.out.println("   | Usual Case, working as intended.");
            logicOfViewChange(view);
        } else {
            System.out.println("   | Call tries to change to the already shown view, returning.");
        }
    }

    private void handleBlockedStates(View view) {
        if (view.getView_id().equals("view-login")) {
            resetViewHistory();
            System.out.println("   | Move to Login Screen");
            mainFrame.changeView(view, false, false, true);
            printViewHistory();
        } else if (view.getView_id().equals("view-blocked")) {
            System.out.println("   | Move to Blocked-System Screen is still allowed, acting as usual.");
            logicOfViewChange(view);
        } else {
            System.out.println("   | System is Blocked. No other view is allowed, returning.");
        }
    }

    private void resetViewHistory() {
        System.out.println("   | Resetting the View History.");
        view_history.clear();
        currentViewIndex = fallbackCurrentViewIndex;  // Kein aktueller View, da history geleert
    }

    private boolean isCurrentView(View view) {
        return currentViewIndex >= 0 && view.equals(getActiveView());
    }

    private void logicOfViewChange(View view) {
        while (view_history.size() > currentViewIndex + 1) {
            view_history.removeLast();
        }

        view_history.add(view);
        currentViewIndex = view_history.size() - 1;
        printViewHistory();
        mainFrame.changeView(view, shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled(), shouldHideFeatureBar(view));
    }

    private boolean shouldForwardButtonBeEnabled() {
        return currentViewIndex < view_history.size() - 1;
    }

    private boolean shouldBackButtonBeEnabled() {
        return currentViewIndex > 0;
    }

    private boolean shouldHideFeatureBar(View view) {
        return view.getView_id().equals("view-login");
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
        mainFrame.changeView(getActiveView(), shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled(), shouldHideFeatureBar(getActiveView()));
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
        mainFrame.changeView(getActiveView(), shouldBackButtonBeEnabled(), shouldForwardButtonBeEnabled(), shouldHideFeatureBar(getActiveView()));
    }

    public View getActiveView() {
        if (currentViewIndex >= 0 && currentViewIndex < view_history.size()) {
            return view_history.get(currentViewIndex);
        }
        return null;
    }

    public ArrayList<View> getView_history() {
        return view_history;
    }

    public int getCurrentViewIndex() {
        return currentViewIndex;
    }


    /**
     * Zeigt den LoginView an und leert ggf. den View-Verlauf.
     */
    public void showLoginView() {
        resetViewHistory();
        System.out.println("   | Forced Move to Login Screen via showLoginView()");
        mainFrame.changeView(new LoginView(mainFrame.getLoginManager()), false, false, true);

    }
    /**
     * Fügt dem angegebenen JButton einen Hover Effect hinzu.
     * @author Elias Glauert
     */
    public void applyHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isEnabled()) return;
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                button.setContentAreaFilled(true);
                button.setBackground(new Color(220, 220, 220, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
            }
        });
    }
    public static GuiManager getInstance() {
        return instance;
    }

    public static void setInstance(GuiManager guiManager) {
        instance = guiManager;
    }
}