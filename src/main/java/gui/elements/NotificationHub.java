package gui.elements;

import core.EventManager;
import core.Notification;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Beinhaltet die Benachrichtigungen für den User.
 * Öffnet ein Pop-Up-Menü, wenn der Knopf gedrückt wird.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-07
 */
public class NotificationHub extends JPanel {

    private JButton notificationButton;
    private ArrayList<Notification> notifications;
    private EventManager eventManager;
    private JPopupMenu popupMenu;

    public NotificationHub(ArrayList<Notification> notifications, EventManager eventManager) {
        this.notifications = notifications;
        this.eventManager = eventManager;

        setLayout(new BorderLayout());

        notificationButton = new JButton("Notifications");
        notificationButton.addActionListener(_ -> showPopupMenu());

        updateButtonIcon();

        add(notificationButton, BorderLayout.NORTH);
    }

    private void updateButtonIcon() {
        System.out.println(" ~ db ~ gui.elements.NotificationHub.updateButtonIcon()");

        int notificationCount = notifications.size();
        notificationButton.setText((notificationCount > 0 ? "(" + notificationCount + ") " : "") + "Notifications");
    }

    private void showPopupMenu() {
        popupMenu = new JPopupMenu();
        for (Notification notification : notifications) {
            Gui_Notification notificationPanel = new Gui_Notification(notification, eventManager);
            popupMenu.add(notificationPanel);
        }
        popupMenu.show(notificationButton, notificationButton.getWidth(), notificationButton.getHeight());
    }

    public void updatePopupMenu(boolean closePopUp) {
        if (popupMenu != null) {
            boolean wasPopUpOpen = popupMenu.isVisible();
            popupMenu.removeAll();
            for (Notification notification : notifications) {
                Gui_Notification notificationPanel = new Gui_Notification(notification, eventManager);
                popupMenu.add(notificationPanel);
            }

            popupMenu.revalidate();
            popupMenu.repaint();
            popupMenu.pack();

            popupMenu.setVisible(wasPopUpOpen && !closePopUp);
        }
    }

    public void setNotifications(ArrayList<Notification> notifications, boolean closePopUp) {
        System.out.println(" ~ db ~ gui.elements.NotificationHub.setNotifications()");
        this.notifications = notifications;
        updateButtonIcon();
        updatePopupMenu(closePopUp);
    }
}