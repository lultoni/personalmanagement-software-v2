package gui.views;

import core.EventManager;
import core.LoginManager;
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Login View für das GUI.
 * @author Elias Glauert
 * @version 1.3
 * @since 2025-07-11
 */
public class LoginView extends View {

    private final LoginManager loginManager;
    private JLabel feedbackLabel;

    /**
     * Konstruktor für die LoginView Klasse.
     *
     * @param eventManager
     * @param loginManager LoginManager für das erzeugte Objekt.
     * @author Elias Glauert
     */
    // TODO rewrite this constructor, so that it does not look like it was written by chatgpt
    public LoginView(EventManager eventManager, LoginManager loginManager) {
        this.loginManager = loginManager;
        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }


        setView_id("view-login");
        setView_name("Login Fenster");


        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f)); // Transparenz
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false);


        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false); // Transparent, damit Hintergrund durchscheint

        if (PersistentInformationReader.isSystemBlocked()) {
            JLabel blockedLabel = new JLabel("<html><div style='text-align: center;'>"
                    + "Das System befindet sich derzeit im Wartungsmodus.<br>"
                    + "Anmeldungen sind vorübergehend deaktiviert."
                    + "</div></html>", SwingConstants.CENTER);
            blockedLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            blockedLabel.setForeground(Color.RED);
            mainPanel.add(blockedLabel);
            backgroundPanel.add(mainPanel, BorderLayout.CENTER);
            setLayout(new BorderLayout());
            add(backgroundPanel, BorderLayout.CENTER);
            return;
        }

        // Login-Box
        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(350, 400));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(true);

        // Icon
        JLabel iconLabel = new JLabel("➔", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Titel
        JLabel titleLabel = new JLabel("Anmeldung");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Bitte melden Sie sich mit Ihren Mitarbeiter-Zugangsdaten an, um fortzufahren.</div></html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Benutzername
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(280, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createTitledBorder("Benutzername"));

        // Passwort
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(280, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Passwort"));

        // Feedback-Label
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Login Button
        JButton loginButton = new JButton("Anmelden");
        loginButton.setMaximumSize(new Dimension(280, 40));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ActionListener für Login-Versuch
        ActionListener loginAction = _ -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            int loginStatus = loginManager.attemptLogin(username, password);

            switch (loginStatus) {
                case LoginManager.LOGIN_SUCCESS -> {
                    showFeedback("Anmeldung erfolgreich.", new Color(0, 128, 0));

                    // Leichte Verzögerung um User Feedback zu maximieren
                    new Timer(1000, _ -> loginManager.proceedToSoftware()) {{
                        setRepeats(false);
                    }}.start();
                }
                case LoginManager.USERNAME_NOT_FOUND -> {
                    String variation_1 = "Benutzername ist nicht korrekt.";
                    String variation_2 = "Benutzername war erneut inkorrekt.";
                    showFeedback((!feedbackLabel.getText().equals(variation_1)) ? variation_1 : variation_2, Color.RED);
                }
                case LoginManager.PASSWORD_INCORRECT -> {
                    String variation_1 = "Passwort ist falsch.";
                    String variation_2 = "Passwort war erneut falsch.";
                    showFeedback((!feedbackLabel.getText().equals(variation_1)) ? variation_1 : variation_2, Color.RED);
                }
            }
        };

        loginButton.addActionListener(loginAction);
        usernameField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);

        // Aufbau
        loginPanel.add(iconLabel);
        loginPanel.add(titleLabel);
        loginPanel.add(subtitleLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(feedbackLabel);

        mainPanel.add(loginPanel);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    public LoginView(EventManager eventManager, LoginManager loginManager, LoginManager loginManager1) {
        this.loginManager = loginManager1;
    }

    /**
     * Zeigt eine Feedback-Nachricht im Label an.
     */
    private void showFeedback(String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(color);
    }

    @Override
    public String toString() {
        String idHex = Integer.toHexString(System.identityHashCode(this));
        return "LoginView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        return obj.getClass() == this.getClass();
    }
}
