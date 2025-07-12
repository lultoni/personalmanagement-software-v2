package gui.views;

import core.LoginManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Login View für das GUI.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-11
 */
public class LoginView extends View {

    private LoginManager loginManager;

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

        // Login Button
        JButton loginButton = new JButton("Anmelden");
        loginButton.setMaximumSize(new Dimension(280, 40));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ActionListener für Login-Versuch
        ActionListener loginAction = _ -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            loginManager.attemptLogin(username, password);
        };

        // Login beim Buttonklick oder Enter-Taste
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

        mainPanel.add(loginPanel);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Gives den View als String zurück.
     * @return All describing characteristics of the object with its hex code in the form of a string.
     * @author Elias Glauert
     */
    @Override
    public String toString() {
        String idHex = Integer.toHexString(System.identityHashCode(this));
        return "LoginView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "')";
    }
}
