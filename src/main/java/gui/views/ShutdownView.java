package gui.views;

import core.EventManager;
import core.LoginManager;
import core.SystemStateManager;
import model.db.Employee;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Ansicht zur Systemabschaltung für Admins.
 * @author Joshua Sperber
 * @version 3.0
 */
public class ShutdownView extends View {

    private static final int COUNTDOWN_DURATION = 10; // Sekunden
    private Timer countdownTimer;
    private int remainingTime = COUNTDOWN_DURATION;
    private JLabel countdownLabel;
    private JButton cancelButton;
    private final EventManager eventManager;
    private final LoginManager loginManager; // Hinzugefügt: Attribut für den LoginManager
    private final Employee currentUser;

    public ShutdownView(LoginManager loginManager, EventManager eventManager, Employee currentUser) {
        this.eventManager = eventManager;
        this.loginManager = loginManager; // Hinzugefügt: Initialisierung des LoginManagers
        this.currentUser = currentUser;

        setView_id("view-shutdown");
        setView_name("Systemabschaltung");

        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Systemabschaltung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel warningLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "Diese Aktion sperrt das System.<br>" +
                        "Alle nicht gespeicherten Daten gehen verloren!" +
                        "</div></html>");
        warningLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 0;
        contentPanel.add(warningLabel, gbc);

        countdownLabel = new JLabel("System abschalten", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        countdownLabel.setForeground(Color.RED);
        gbc.gridy = 1;
        contentPanel.add(countdownLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton shutdownButton = new JButton("System sperren");
        shutdownButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        shutdownButton.setBackground(new Color(31, 32, 35));
        shutdownButton.setForeground(Color.WHITE);
        shutdownButton.addActionListener(this::startShutdownCountdown);

        cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(this::cancelShutdown);

        buttonPanel.add(shutdownButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 2;
        contentPanel.add(buttonPanel, gbc);

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void startShutdownCountdown(ActionEvent e) {
        countdownLabel.setText("System wird gesperrt in " + remainingTime + "s...");
        countdownLabel.setVisible(true);
        ((JButton)e.getSource()).setEnabled(false);
        cancelButton.setVisible(true);

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    remainingTime--;
                    countdownLabel.setText("System wird gesperrt in " + remainingTime + "s...");

                    if (remainingTime <= 0) {
                        countdownTimer.cancel();
                        performShutdown();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void cancelShutdown(ActionEvent e) {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        resetShutdownUI();
        JOptionPane.showMessageDialog(this,
                "Systemabschaltung abgebrochen",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetShutdownUI() {
        remainingTime = COUNTDOWN_DURATION;
        countdownLabel.setVisible(false);
        cancelButton.setVisible(false);

        for (Component comp : cancelButton.getParent().getComponents()) {
            if (comp instanceof JButton && "System sperren".equals(((JButton)comp).getText())) {
                comp.setEnabled(true);
                break;
            }
        }
    }

    private void performShutdown() {
        // System wird gesperrt und der Benutzer ausgeloggt
        SystemStateManager.getInstance().setSystemLocked(true);
        JOptionPane.showMessageDialog(this,
                "Das System wurde gesperrt. Nur IT-Administratoren können sich jetzt anmelden.",
                "System gesperrt",
                JOptionPane.INFORMATION_MESSAGE);

        // Der korrigierte Aufruf: Übergibt den eventManager und den loginManager an die LoginView
        eventManager.callEvent("changeView", new Object[]{new LoginView(eventManager, loginManager)});
    }

    @Override
    public String toString() {
        return "ShutdownView{" +
                "view_id='" + getView_id() + '\'' +
                ", view_name='" + getView_name() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShutdownView that = (ShutdownView) obj;
        return getView_id().equals(that.getView_id());
    }
}