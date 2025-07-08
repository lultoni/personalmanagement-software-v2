package gui.views;

import core.EventManager;

import javax.swing.*;
import java.awt.*;

/**
 * Test-View für das GUI.
 * Wurde verwendet, um das Benachrichtigungssystem zu testen.
 *
 * @author Elias Glauert
 * @version 1.1
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
            eventManager.callEvent("createNotification", new Object[]{"Notif_This_View_" + counter, "This is a test notification", new TestNotificationView(eventManager)});
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

    @Override
    public String toString() {
        return "TestNotificationView('" + getView_id() + "', '" + getView_name() + "')";
    }
}
