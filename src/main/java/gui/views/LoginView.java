package gui.views;

import core.LoginManager;
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.Timer;

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
     * @param loginManager LoginManager für das erzeugte Objekt.
     * @author Elias Glauert
     */
    // TODO rewrite this constructor, so that it does not look like it was written by chatgpt
    public LoginView(LoginManager loginManager) {
        this.loginManager = loginManager;

        setView_id("view-login");
        setView_name("Login Fenster");

        // Hauptpanel mit zentriertem Layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // Hintergrundfarbe

        if (PersistentInformationReader.isSystemBlocked()) {
            JLabel blockedLabel = new JLabel("<html><div style='text-align: center;'>"
                    + "Das System befindet sich derzeit im Wartungsmodus.<br>"
                    + "Anmeldungen sind vorübergehend deaktiviert."
                    + "</div></html>", SwingConstants.CENTER);
            blockedLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            blockedLabel.setForeground(Color.RED);
            mainPanel.add(blockedLabel);
            setLayout(new BorderLayout());
            add(mainPanel, BorderLayout.CENTER);
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
        JLabel titleLabel = new JLabel("Anmeldung mit Benutzername und Passwort");
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

            boolean loginSuccess = loginManager.attemptLogin(username, password);

            if (loginSuccess) {
                showFeedback("Anmeldung erfolgreich.", new Color(0, 128, 0));

                // Leichte Verzögerung um mehr User Feedback zu zeigen
                new Timer(1000, _ -> loginManager.proceedToSoftware()) {{
                    setRepeats(false);
                }}.start();

            } else {
                String variation_1 = "Benutzername oder Passwort ist falsch.";
                String variation_2 = "Eingaben waren erneut falsch.";
                showFeedback((feedbackLabel.getText().equals(variation_1)) ? variation_2 : variation_1, Color.RED);
            }
        };

        loginButton.addActionListener(loginAction);
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

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
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
