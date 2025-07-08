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
 * @version 1.1
 * @since 2025-07-07
 */
public class Gui_Notification extends JPanel {

    // TODO add a currentView param, so you can compare that to the connected view, disabling the view button if they are the same
    // TODO close the pop-up menu on change view
    // TODO update the pop-up menu on delete notification (or just delete the gui-notif?)
    // TODO --> maybe do both over events
    public Gui_Notification(Notification notification, EventManager eventManager) {

        setLayout(new GridLayout(2, 2));

        setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

        JLabel titleLabel = new JLabel(notification.getNotification_titel());
        JLabel descriptionLabel = new JLabel(notification.getNotification_description());

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(_ -> eventManager.callEvent("changeView", new Object[]{notification.getConnected_view()}));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(_ -> eventManager.callEvent("popNotification", new Object[]{notification.getNotification_id()}));

        add(titleLabel);
        add(viewButton);
        add(descriptionLabel);
        add(deleteButton);
    }
}