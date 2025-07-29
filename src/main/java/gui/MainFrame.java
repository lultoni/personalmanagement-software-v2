package gui;

import core.EventManager;
import core.LoginManager;
import core.Main;
import core.Notification;
import gui.elements.FeatureBar;
import gui.elements.NotificationHub;
import gui.elements.TitleBar;
import gui.views.DefaultView;
import gui.views.View;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


/**
 * Diese Klasse ist das Hauptfenster für das GUI.
 *
 * @author Elias Glauert
 * @version 1.7
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
     * Öffnet bei Klick den nächsten View.
     */
    private JButton forwardButton;

    /**
     * EventManager Verbindung für die MainFrame.
     */
    private EventManager eventManager;

    /**
     * GuiManager Verbindung für die MainFrame.
     */
    private GuiManager guiManager;

    /**
     * Konstruktor für die MainFrame.
     * @author Elias Glauert
     */
    public MainFrame(ArrayList<Notification> notifications, EventManager eventManager, LoginManager loginManager, GuiManager guiManager) {
        this.notifications = notifications;
        this.eventManager = eventManager;
        this.guiManager = guiManager;

        initFrameSettings();
        initComponents(loginManager, guiManager);
        setVisible(true);
    }

    /**
     * Passt die JFrame Variablen passend an.
     * @author Elias Glauert
     */
    private void initFrameSettings() {
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
    }

    /**
     * Initialisiert die Komponenten, welche in die JFrame kommen.
     * @author Elias Glauert
     */
    private void initComponents(LoginManager loginManager, GuiManager guiManager) {
        currentView = new DefaultView();
        featureBar = new FeatureBar(loginManager, eventManager);
        titleBar = new TitleBar(guiManager);
        notificationHub = new NotificationHub(notifications, eventManager, guiManager);

        backButton = createNavigationButton("src/main/resources/icons/backButton.png", () ->
                eventManager.callEvent("moveBackView", null)
        );

        forwardButton = createNavigationButton("src/main/resources/icons/forwardButton.png", () ->
                eventManager.callEvent("moveForwardView", null)
        );

        JPanel viewChangeButtonPanel = new JPanel(new GridLayout(1, 0));
        viewChangeButtonPanel.add(backButton);
        viewChangeButtonPanel.add(forwardButton);

        JPanel southBar = new JPanel(new BorderLayout());
        southBar.setBackground(Color.WHITE);
        southBar.add(viewChangeButtonPanel, BorderLayout.WEST);
        southBar.add(titleBar, BorderLayout.CENTER);
        southBar.add(notificationHub, BorderLayout.EAST);
        // JPanel colorBorderPanel = new JPanel();
        // colorBorderPanel.setBackground(new Color(89, 88, 88));
        // southBar.add(colorBorderPanel, BorderLayout.NORTH);

        viewChangeButtonPanel.setOpaque(false);
        southBar.setOpaque(true);

        add(currentView, BorderLayout.CENTER);
        add(featureBar, BorderLayout.WEST);
        add(southBar, BorderLayout.SOUTH);
    }

    /**
     * Erstellt die Forward- und Back-Buttons.
     * @author Elias Glauert
     */
    private JButton createNavigationButton(String iconPath, Runnable onClick) {
        ImageIcon rawIcon = new ImageIcon(iconPath);
        Image scaledImage = rawIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));

        button.setText(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);

        // Hier: Hintergrund explizit auf transparent setzen um sicherzustellen, dass die Umgebung nicht überschrieben wird
        button.setBackground(new Color(0, 0, 0, 0)); // Setzt den Hintergrund auf transparent

        button.addActionListener(_ -> onClick.run());

        button.setPreferredSize(new Dimension(56, 56));

        guiManager.applyHoverEffect(button);

        return button;
    }

    /**
     * Ändert den View, welcher im Center des BorderLayouts ist.
     * @author Elias Glauert
     */
    public void changeView(View view, boolean backButtonEnabled, boolean forwardButtonEnabled) {

        Component activeViewComponent = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (activeViewComponent != null) {
            remove(activeViewComponent);
        }

        currentView = view;
        add(currentView, BorderLayout.CENTER);

        titleBar.updateTitleBar();

        backButton.setEnabled(backButtonEnabled);
        ImageIcon rawIcon = new ImageIcon("src/main/resources/icons/backButton" + (backButtonEnabled ? ".png" : "Disabled.png"));
        Image scaledImage = rawIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        backButton.setIcon(new ImageIcon(scaledImage));

        forwardButton.setEnabled(forwardButtonEnabled);
        rawIcon = new ImageIcon("src/main/resources/icons/forwardButton" + (forwardButtonEnabled ? ".png" : "Disabled.png"));
        scaledImage = rawIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        forwardButton.setIcon(new ImageIcon(scaledImage));

        featureBar.updateButtonEnabled();

        revalidate();
        repaint();
    }

    /**
     * Setzt die Benachrichtigungen auf die übergebene Liste.
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
