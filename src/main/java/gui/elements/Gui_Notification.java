package gui.elements;

import core.EventManager;
import core.Notification;
import gui.views.View;
import javax.swing.*;
import java.awt.*;

/**
 * GUI-Komponente einer einzelnen Benachrichtigung.
 * Beinhaltet den Titel, die Beschreibung, den Knopf zum View und den Knopf zum Löschen.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-07
 */
public class Gui_Notification extends JPanel {

    private Notification notification;

    public Gui_Notification(Notification notification, EventManager eventManager) {
        this.notification = notification;

        setLayout(new GridLayout(2, 2));

        JLabel titleLabel = new JLabel(notification.getNotification_titel());
        JLabel descriptionLabel = new JLabel(notification.getNotification_description());

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(_ -> eventManager.callEvent("changeView", new Object[]{notification.getConnected_view()}));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(_ -> eventManager.callEvent("popNotification", new Object[]{notification.getNotification_id()}));

        add(titleLabel);
        add(descriptionLabel);
        add(viewButton);
        add(deleteButton);
    }
}