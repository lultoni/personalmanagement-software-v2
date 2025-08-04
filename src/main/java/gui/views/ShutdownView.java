package gui.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Ansicht zur Systemabschaltung für Admins.
 * @author Joshua Sperber
 * @version 2.0
 */
public class ShutdownView extends View {

    private static final int COUNTDOWN_DURATION = 10; // Sekunden
    private Timer countdownTimer;
    private int remainingTime = COUNTDOWN_DURATION;
    private JLabel countdownLabel;
    private JButton cancelButton;

    public ShutdownView() {
        setView_id("view-shutdown");
        setView_name("Systemabschaltung");
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Header
        JLabel titleLabel = new JLabel("Systemabschaltung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Hauptinhalt
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Warnhinweis
        JLabel warningLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "Diese Aktion wird das System sofort herunterfahren.<br>" +
                        "Alle nicht gespeicherten Daten gehen verloren!" +
                        "</div></html>");
        warningLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 0;
        contentPanel.add(warningLabel, gbc);

        // Countdown-Anzeige (initial versteckt)
        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        countdownLabel.setForeground(Color.RED);
        gbc.gridy = 1;
        contentPanel.add(countdownLabel, gbc);

        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        // Abschalt-Button
        JButton shutdownButton = new JButton("System abschalten");
        shutdownButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        shutdownButton.setBackground(new Color(255, 80, 80));
        shutdownButton.setForeground(Color.WHITE);
        shutdownButton.addActionListener(this::startShutdownCountdown);

        // Abbrechen-Button (initial versteckt)
        cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(this::cancelShutdown);

        buttonPanel.add(shutdownButton);
        buttonPanel.add(cancelButton);
        gbc.gridy = 2;
        contentPanel.add(buttonPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void startShutdownCountdown(ActionEvent e) {
        // UI für Countdown vorbereiten
        countdownLabel.setText("System wird heruntergefahren in " + remainingTime + "s...");
        countdownLabel.setVisible(true);
        ((JButton)e.getSource()).setEnabled(false);
        cancelButton.setVisible(true);

        // Countdown starten
        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    remainingTime--;
                    countdownLabel.setText("System wird heruntergefahren in " + remainingTime + "s...");

                    if (remainingTime <= 0) {
                        countdownTimer.cancel();
                        performShutdown();
                    }
                });
            }
        }, 1000, 1000); // Start nach 1s, Wiederholung alle 1s
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

        // Abschalt-Button wieder aktivieren
        for (Component comp : ((JPanel)countdownLabel.getParent()).getComponents()) {
            if (comp instanceof JButton && "System abschalten".equals(((JButton)comp).getText())) {
                comp.setEnabled(true);
                break;
            }
        }
    }

    private void performShutdown() {
        // TODO: Eigentliche Abschaltlogik hier implementieren
        // Z.B.: Event an den EventManager senden: "shutdownSystem"

        // Beispielhafte UI-Reaktion
        countdownLabel.setText("System wird jetzt heruntergefahren...");
        cancelButton.setVisible(false);

        // Simuliertes Herunterfahren nach kurzer Verzögerung
        Timer shutdownTimer = new Timer();
        shutdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    countdownLabel.setText("System ist offline");
                    // Hier könnte man die gesamte UI deaktivieren
                });
            }
        }, 2000);
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