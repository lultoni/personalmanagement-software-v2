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
 * @version 1.1
 * @since 2025-07-07
 */
public class NotificationHub extends JPanel {

    private JButton notificationButton;
    private ArrayList<Notification> notifications;
    private EventManager eventManager;

    public NotificationHub(ArrayList<Notification> notifications, EventManager eventManager) {
        this.notifications = notifications;
        this.eventManager = eventManager;

        setLayout(new BorderLayout());

        notificationButton = new JButton("Notifications");
        notificationButton.addActionListener(e -> showPopupMenu());

        updateButtonIcon();

        add(notificationButton, BorderLayout.NORTH);
    }

    private void updateButtonIcon() {
        // Set icon based on number of notifications
        int notificationCount = notifications.size();
        ImageIcon icon = new ImageIcon("icon_path"); // Replace with actual icon path
        notificationButton.setIcon(icon);

        // Custom label showing the count of notifications
        notificationButton.setText("Notifications (" + notificationCount + ")");
    }

    private void showPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        for (Notification notification : notifications) {
            Gui_Notification notificationPanel = new Gui_Notification(notification, eventManager);
            popupMenu.add(notificationPanel);
        }
        popupMenu.show(notificationButton, notificationButton.getWidth(), notificationButton.getHeight());
    }

}