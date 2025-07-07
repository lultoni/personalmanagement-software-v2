package gui.views;

import core.EventManager;

import javax.swing.*;
import java.awt.*;

public class TestNotificationView extends View {
    /**
     * Konstruktor für den View. Initialisiert die view_id und den view_name.
     *
     * @author Elias Glauert
     */
    public TestNotificationView(EventManager eventManager) {
        setView_id("view-test_notifications");
        setView_name("Notifications Test");

        setLayout(new GridLayout());
        JButton createNotification1 = new JButton("This view notif");
        createNotification1.addActionListener(_ -> {eventManager.callEvent("createNotification", new Object[]{"Notif_This_View", "This is a test notification", new TestNotificationView(eventManager)});});
        add(createNotification1);

        JButton createNotification2 = new JButton("default view notif");
        createNotification2.addActionListener(_ -> {eventManager.callEvent("createNotification", new Object[]{"Notif_Default_View", "This is a test notification", new DefaultView()});});
        add(createNotification2);

    }
}
