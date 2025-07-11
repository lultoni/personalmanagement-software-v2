package gui.views;

import core.EventManager;

import javax.swing.*;
import java.awt.*;

/**
 * Test-View für das GUI.
 * Wurde verwendet, um das Benachrichtigungssystem zu testen.
 *
 * @author Elias Glauert
 * @version 1.3
 * @since 2025-07-07
 */
public class TestNotificationView extends View {

    private int counter;

    /**
     * Konstruktor für den View. Initialisiert die view_id und den view_name.
     *
     * @author Elias Glauert
     */
    public TestNotificationView(EventManager eventManager) {
        counter = 0;
        setView_id("view-test_notifications");
        setView_name("Notifications Test");

        setLayout(new GridLayout());
        JButton createNotification1 = new JButton("This view notif");
        createNotification1.addActionListener(_ -> {
            eventManager.callEvent("createNotification", new Object[]{"Notif_This_View_" + counter, "This is a test notification", this});
            counter++;
        });
        add(createNotification1);

        JButton createNotification2 = new JButton("default view notif");
        createNotification2.addActionListener(_ -> {
            eventManager.callEvent("createNotification", new Object[]{"Notif_Default_View_" + counter, "This is a test notification", new DefaultView()});
            counter++;
        });
        add(createNotification2);

    }

    /**
     * Gives back the View as a String.
     * @return All describing characteristics of the object with its hex code in the form of a string.
     * @author Elias Glauert
     */
    @Override
    public String toString() {
        String idHex = Integer.toHexString(System.identityHashCode(this));
        return "TestNotificationView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "', counter:" + counter + ")";
    }
}
