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
 * @version 1.2
 * @since 2025-07-07
 */
public class Gui_Notification extends JPanel {

    // TODO add a currentView param, so you can compare that to the connected view, disabling the view button if they are the same
    //  close the pop-up menu on change view
    //  update the pop-up menu on delete notification (or just delete the gui-notif?)
    //  --> maybe do both over events
    public Gui_Notification(Notification notification, EventManager eventManager) {

        boolean has_connected_view = notification.getConnected_view() != null;

        setLayout(new GridLayout(2, 2));

        setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

        JLabel titleLabel = new JLabel(notification.getNotification_titel());
        JLabel descriptionLabel = new JLabel(notification.getNotification_description());

        JButton viewButton = new JButton("View");
        if (has_connected_view) viewButton.addActionListener(_ -> eventManager.callEvent("changeView", new Object[]{notification.getConnected_view()}));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(_ -> eventManager.callEvent("popNotification", new Object[]{notification.getNotification_id()}));

        add(titleLabel);
        if (has_connected_view) add(viewButton);
        add(descriptionLabel);
        add(deleteButton);
    }
}