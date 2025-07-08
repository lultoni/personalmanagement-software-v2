package gui;

import core.EventManager;
import core.Notification;
import gui.elements.FeatureBar;
import gui.elements.NotificationHub;
import gui.elements.TitleBar;
import gui.views.DefaultView;
import gui.views.View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Diese Klasse ist das Hauptfenster für das GUI.
 *
 * @author Elias Glauert
 * @version 1.3
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

    /**
     * Öffnet bei Klick den da vorigen View.
     */
    private JButton backButton;

    /**
     * EventManager Verbindung für die MainFrame.
     */
    private EventManager eventManager;

    /**
     * Konstruktor für die MainFrame.
     * Initialisiert die Core Features, welche im GUI gefunden werden und immer, View-Unabhängig, angezeigt werden.
     *
     * @author Elias Glauert
     */
    public MainFrame(ArrayList<Notification> notifications, EventManager eventManager) {
        this.notifications = notifications;
        this.eventManager = eventManager;

        setTitle("Personalmanagement Software");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 720);
        setLayout(new BorderLayout());

        currentView = new DefaultView();

        featureBar = new FeatureBar();

        titleBar = new TitleBar();
        notificationHub = new NotificationHub(notifications, eventManager);
        backButton = new JButton("<-- Zurück"); // TODO move this to a separated class later on for functionality like seeing if it has to be disabled
        backButton.addActionListener(_ -> {
            eventManager.callEvent("moveBackView", null);
        });

        JPanel southBar = new JPanel(new BorderLayout());
        southBar.add(backButton, BorderLayout.WEST);
        southBar.add(titleBar, BorderLayout.CENTER);
        southBar.add(notificationHub, BorderLayout.EAST);

        add(currentView, BorderLayout.CENTER);
        add(featureBar, BorderLayout.WEST);
        add(southBar, BorderLayout.SOUTH);

        setVisible(true);

    }

    /**
     * Ändert den View, welcher im Center des BorderLayouts ist.
     *
     * @author Elias Glauert
     */
    public void changeView(View view) {

        Component activeViewComponent = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (activeViewComponent != null) {
            remove(activeViewComponent);
        }

        currentView = view;
        add(currentView, BorderLayout.CENTER);

        titleBar.changeText(view.getView_name());

        revalidate();
        repaint();
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        System.out.println(" ~ db ~ gui.MainFrame.setNotifications()");
        this.notifications = notifications;
        notificationHub.setNotifications(notifications);
    }
}
