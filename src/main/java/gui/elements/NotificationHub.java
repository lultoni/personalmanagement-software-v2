package gui.elements;

import core.EventManager;
import core.Notification;
import gui.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Beinhaltet die Benachrichtigungen für den User.
 * Öffnet ein Pop-Up-Menü, wenn der Knopf gedrückt wird.
 *
 * @author Elias Glauert
 * @version 1.4
 * @since 2025-07-07
 */
public class NotificationHub extends JPanel {

    private JButton notificationButton;
    private ArrayList<Notification> notifications;
    private EventManager eventManager;
    private JPopupMenu popupMenu;
    private GuiManager guiManager;

    private static final String iconBasePath = "src/main/resources/icons/notif/";
    private static final String notificationButtonBasePath = iconBasePath + "notificationButtonBase.png";
    private static final String notificationButtonMaxPath = iconBasePath + "notificationButtonMax.png";
    private static final int preferredWidth = 48;
    private static final int preferredHeight = 48;

    /**
     * Konstruktor für das NotificationHub.
     * @param notifications
     * @param eventManager
     * @param guiManager
     * @author Elias Glauert
     */
    public NotificationHub(ArrayList<Notification> notifications, EventManager eventManager, GuiManager guiManager) {
        this.notifications = notifications;
        this.eventManager = eventManager;
        this.guiManager = guiManager;

        setLayout(new BorderLayout());

        notificationButton = new JButton();
        notificationButton.addActionListener(_ -> showPopupMenu());

        updateButtonIcon();

        add(notificationButton, BorderLayout.NORTH);
    }

    private void updateButtonIcon() {
        System.out.println(" ~ db ~ gui.elements.NotificationHub.updateButtonIcon()");

        int notificationCount = notifications.size();

        String iconPath = notificationButtonBasePath;
        if (notificationCount >= 1 && notificationCount <= 9) {
            iconPath = iconBasePath + "notificationButton" + notificationCount + ".png";
        } else if (notificationCount > 9) {
            iconPath = notificationButtonMaxPath;
        }

        ImageIcon rawIcon = new ImageIcon(iconPath);
        Image scaledImage = rawIcon.getImage().getScaledInstance(preferredWidth, preferredHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        notificationButton.setIcon(scaledIcon);
    }

    private void showPopupMenu() {
        popupMenu = new JPopupMenu();
        for (Notification notification : notifications) {
            Gui_Notification notificationPanel = new Gui_Notification(notification, eventManager, guiManager.getActiveView());
            popupMenu.add(notificationPanel);
        }
        popupMenu.show(notificationButton, notificationButton.getWidth(), notificationButton.getHeight());
    }

    public void updatePopupMenu(boolean closePopUp) {
        if (popupMenu != null) {
            boolean wasPopUpOpen = popupMenu.isVisible();
            popupMenu.removeAll();
            for (Notification notification : notifications) {
                Gui_Notification notificationPanel = new Gui_Notification(notification, eventManager, guiManager.getActiveView());
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