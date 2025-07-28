package gui;

import core.EventManager;
import core.LoginManager;
import core.Main;
import core.Notification;
import gui.elements.FeatureBar;
import gui.elements.Gui_Notification;
import gui.elements.NotificationHub;
import gui.elements.TitleBar;
import gui.views.DefaultView;
import gui.views.View;
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Diese Klasse ist das Hauptfenster für das GUI.
 *
 * @author Elias Glauert
 * @version 1.5
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
    public MainFrame(ArrayList<Notification> notifications, EventManager eventManager, LoginManager loginManager, GuiManager guiManager) {
        this.notifications = notifications;
        this.eventManager = eventManager;

        setTitle("Personalmanagement Software");

        ImageIcon rawIcon = new ImageIcon("src/main/resources/icons/softwareIcon.png");
        Image scaledImage = rawIcon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        setIconImage(scaledImage);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.exitProgram();
            }
        });

        setSize(1000, 720);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        currentView = new DefaultView();

        featureBar = new FeatureBar(loginManager, eventManager);

        titleBar = new TitleBar();
        notificationHub = new NotificationHub(notifications, eventManager, guiManager);
        backButton = new JButton("<-- Zurück");
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
    public void changeView(View view, boolean backButtonEnabled) {

        Component activeViewComponent = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (activeViewComponent != null) {
            remove(activeViewComponent);
        }

        currentView = view;
        add(currentView, BorderLayout.CENTER);

        titleBar.changeText(view.getView_name());

        backButton.setEnabled(backButtonEnabled);

        featureBar.updateButtonEnabled();

        revalidate();
        repaint();
    }

    /**
     * Setzt die Benachrichtigungen auf die übergebene Liste.
     * @param notifications
     * @param closePopUp
     * @author Elias Glauert
     */
    public void setNotifications(ArrayList<Notification> notifications, boolean closePopUp) {
        System.out.println(" ~ db ~ gui.MainFrame.setNotifications()");
        System.out.println("   | closePopUp: " + closePopUp);
        this.notifications = notifications;
        notificationHub.setNotifications(notifications, closePopUp);
    }

    /**
     * Aktualisiert die Benachrichtigungen.
     * @author Elias Glauert
     */
    public void updateNotifications() {
        System.out.println(" ~ db ~ gui.MainFrame.updateNotifications()");
        setNotifications(notifications, true);
    }
}
