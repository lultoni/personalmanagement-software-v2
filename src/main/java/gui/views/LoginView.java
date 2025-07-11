package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Login View für das GUI.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-11
 */
public class LoginView extends View {

    public LoginView() {

        setView_id("view-login");
        setView_name("Login Fenster");

        // Hauptpanel mit zentriertem Layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // Heller Wolkenhintergrund als Platzhalter

        // Container für Login-Box
        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(350, 450));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(true);

        // Icon (Platzhalter)
        JLabel iconLabel = new JLabel("\u2794", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Überschrift
        JLabel titleLabel = new JLabel("Sign in with email");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Make a new doc to bring your words, data, and teams together. For free</div></html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // E-Mail Feld
        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(280, 35));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        // Passwort Feld
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(280, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        // Vergessen-Link
        JPanel forgotPanel = new JPanel(new BorderLayout());
        forgotPanel.setMaximumSize(new Dimension(280, 20));
        forgotPanel.setOpaque(false);
        JLabel forgotLabel = new JLabel("Forgot password?");
        forgotLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        forgotLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        forgotPanel.add(forgotLabel, BorderLayout.EAST);

        // Login Button
        JButton loginButton = new JButton("Get Started");
        loginButton.setMaximumSize(new Dimension(280, 40));
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator
        JLabel separator = new JLabel("────────── Or sign in with ──────────", SwingConstants.CENTER);
        separator.setFont(new Font("SansSerif", Font.PLAIN, 10));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Social Buttons (als Platzhalter)
        JPanel socialPanel = new JPanel();
        socialPanel.setOpaque(false);
        socialPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton googleBtn = new JButton("G");
        JButton fbBtn = new JButton("f");
        JButton appleBtn = new JButton("\uF8FF"); // Apple Icon simuliert

        Dimension socialSize = new Dimension(45, 30);
        for (JButton b : new JButton[]{googleBtn, fbBtn, appleBtn}) {
            b.setPreferredSize(socialSize);
        }

        socialPanel.add(googleBtn);
        socialPanel.add(fbBtn);
        socialPanel.add(appleBtn);

        // Aufbau der Login-Box
        loginPanel.add(iconLabel);
        loginPanel.add(titleLabel);
        loginPanel.add(subtitleLabel);
        loginPanel.add(emailField);
        loginPanel.add(passwordField);
        loginPanel.add(forgotPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(loginButton);
        loginPanel.add(separator);
        loginPanel.add(socialPanel);

        // Ins Hauptpanel einfügen
        mainPanel.add(loginPanel);

        // Panel zur View hinzufügen
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Gives back the View as a String.
     * @return All describing characteristics of the object with its hex code in the form of a string.
     * @author Elias Glauert
     */
    @Override
    public String toString() {
        String idHex = Integer.toHexString(System.identityHashCode(this));
        return "LoginView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "')";
    }
}
