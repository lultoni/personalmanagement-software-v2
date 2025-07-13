package gui.elements;

import core.EventManager;
import core.Notification;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;

/**
 * GUI-Komponente einer einzelnen Benachrichtigung.
 * Beinhaltet den Titel, die Beschreibung, den Knopf zum View und den Knopf zum Löschen.
 *
 * @author Elias Glauert
 * @version 1.3
 * @since 2025-07-07
 */
public class Gui_Notification extends JPanel {

    public Gui_Notification(Notification notification, EventManager eventManager) {

        boolean has_connected_view = notification.getConnected_view() != null;

        setLayout(new GridLayout(2, 2));

        setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

        JLabel titleLabel = new JLabel(notification.getNotification_titel());
        JLabel descriptionLabel = new JLabel(notification.getNotification_description());

        JButton viewButton = new JButton("View");
        if (has_connected_view) {
            viewButton.addActionListener(_ -> {
                System.out.println("CHANGE VIEW BUTTON CLICKED");
                eventManager.callEvent("changeView", new Object[]{notification.getConnected_view()});
                eventManager.callEvent("updateNotification", new Object[]{true});
            });
        }

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(_ -> {
            System.out.println("DELETE BUTTON CLICKED");
            eventManager.callEvent("popNotification", new Object[]{notification.getNotification_id()});
        });

        add(titleLabel);
        if (has_connected_view) add(viewButton);
        add(descriptionLabel);
        add(deleteButton);
    }
}