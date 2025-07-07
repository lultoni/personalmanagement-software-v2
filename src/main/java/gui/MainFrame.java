package gui;

import core.EventManager;
import core.Notification;
import gui.elements.FeatureBar;
import gui.elements.NotificationHub;
import gui.elements.TitleBar;
import gui.views.DefaultView;
import gui.views.View;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;


/**
 * Diese Klasse ist das Hauptfenster für das GUI.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class MainFrame extends JFrame {

    /**
     * Umfängt den gerade angezeigten View.
     */
    private View currentView;

    /**
     * Umfängt alle Feature-Knöpfe.
     */
    private FeatureBar featureBar;

    /**
     * Umfängt den Knopf für die Benachrichtigungen des Users.
     */
    private NotificationHub notificationHub;

    /**
     * Umfängt den Titel des gerade angezeigten Views.
     */
    private TitleBar titleBar;

    /**
     * Umfängt die gerade vorhandenen Benachrichtigungen.
     */
    private ArrayList<Notification> notifications;

    private EventManager eventManager;

    /**
     * Konstruktor für die MainFrame.
     * Initialisiert den currentView, die featureBar, das notificationBar und die titleBar.
     *
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public MainFrame(ArrayList<Notification> notifications, EventManager eventManager) {
        this.eventManager = eventManager;

        setTitle("test title");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 720);
        setLayout(new BorderLayout());

        currentView = new DefaultView();
        add(currentView, BorderLayout.CENTER);

        featureBar = new FeatureBar();
        add(featureBar, BorderLayout.WEST);

        titleBar = new TitleBar();
        notificationHub = new NotificationHub(notifications, eventManager);

        JPanel southBar = new JPanel(new BorderLayout());

        southBar.add(titleBar, BorderLayout.CENTER);
        southBar.add(notificationHub, BorderLayout.EAST);

        add(southBar, BorderLayout.SOUTH);

        setVisible(true);

    }

    public void changeView(View view) {
        currentView = view;
        titleBar.changeText(view.getView_name());
    }
}
