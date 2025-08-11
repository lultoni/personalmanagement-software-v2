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
 * Die ShutdownView ist eine grafische Benutzeroberfläche, die es IT-Administratoren ermöglicht,
 * das gesamte System zu sperren. Sie zeigt eine Warnung vor Datenverlust und startet einen
 * Countdown, nach dessen Ablauf das System gesperrt und der aktuelle Benutzer ausgeloggt wird.
 *
 * @author joshuasperber, Elias Glauert
 * @version 3.0
 * @since 2025-07-28
 */
public class ShutdownView extends View {

    // Konstante für die Dauer des Countdowns in Sekunden.
    private static final int COUNTDOWN_DURATION = 10;
    // Timer-Objekt, das den Countdown steuert.
    private Timer countdownTimer;
    // Zähler für die verbleibende Zeit.
    private int remainingTime = COUNTDOWN_DURATION;
    // Label zur Anzeige des Countdowns.
    private JLabel countdownLabel;
    // Button zum Abbrechen der Systemabschaltung.
    private JButton cancelButton;
    // Manager für die Handhabung von Events und Navigation.
    private final EventManager eventManager;
    // Manager für die An- und Abmeldung von Benutzern.
    private final LoginManager loginManager;
    // Der aktuell eingeloggte Benutzer, der die Aktion ausführt.
    private final Employee currentUser;

    /**
     * Konstruktor für die ShutdownView.
     * Initialisiert die notwendigen Manager und die Benutzeroberfläche.
     *
     * @param loginManager Instanz des LoginManagers zur Verwaltung der Anmeldeaktionen.
     * @param eventManager Instanz des EventManagers zur Steuerung des Ansichtswechsels.
     * @param currentUser Das Employee-Objekt des aktuell eingeloggten Benutzers.
     */
    public ShutdownView(LoginManager loginManager, EventManager eventManager, Employee currentUser) {
        this.eventManager = eventManager;
        this.loginManager = loginManager;
        this.currentUser = currentUser;

        setView_id("view-shutdown");
        setView_name("Systemabschaltung");


        // es wird ein Hintergrundbild aus den resources, icons geladen
        BufferedImage backgroundImage;
        try {
            // Lade das Hintergrundbild aus den Ressourcen.
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            // Wirft eine RuntimeException, wenn das Bild nicht gefunden oder geladen werden kann.
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Erstelle ein JPanel, das das Hintergrundbild mit einer Transparenz von 20% übermalt.
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

        // Erstelle und konfiguriere das Titel-Label.
        JLabel titleLabel = new JLabel("Systemabschaltung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Hauptpanel für den Inhalt mit GridBagLayout zur zentrierten Anordnung der Komponenten.
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Label für den Warnhinweis.
        JLabel warningLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "Diese Aktion sperrt das System.<br>" +
                        "Alle nicht gespeicherten Daten gehen verloren!" +
                        "</div></html>");
        warningLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 0;
        contentPanel.add(warningLabel, gbc);

        // Label für den Countdown, initial nicht sichtbar.
        countdownLabel = new JLabel("System abschalten", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        countdownLabel.setForeground(Color.RED);
        countdownLabel.setVisible(false);
        gbc.gridy = 1;
        contentPanel.add(countdownLabel, gbc);

        // Panel für die Aktions-Buttons.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // "System sperren"-Button, startet den Countdown.
        JButton shutdownButton = new JButton("System sperren");
        shutdownButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        shutdownButton.setBackground(new Color(31, 32, 35));
        shutdownButton.setForeground(Color.WHITE);
        shutdownButton.addActionListener(this::startShutdownCountdown);

        // "Abbrechen"-Button, initial nicht sichtbar.
        cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(this::cancelShutdown);

        buttonPanel.add(shutdownButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 2;
        contentPanel.add(buttonPanel, gbc);

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        // Setze das Hauptlayout der View.
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Startet den Countdown für die Systemabschaltung. Der "Sperren"-Button
     * wird deaktiviert und der "Abbrechen"-Button wird sichtbar.
     * Ein Timer wird aufgesetzt, der das Countdown-Label jede Sekunde aktualisiert.
     *
     * @param e Das ActionEvent, das den Klick auf den "System sperren"-Button darstellt.
     * @author joshuasperber
     */
    private void startShutdownCountdown(ActionEvent e) {
        countdownLabel.setText("System wird gesperrt in " + remainingTime + "s...");
        countdownLabel.setVisible(true);
        ((JButton)e.getSource()).setEnabled(false);
        cancelButton.setVisible(true);

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Die UI-Aktualisierung muss auf dem Event Dispatch Thread erfolgen.
                SwingUtilities.invokeLater(() -> {
                    remainingTime--;
                    countdownLabel.setText("System wird gesperrt in " + remainingTime + "s...");

                    // Wenn der Countdown null erreicht, wird die Abschaltung durchgeführt.
                    if (remainingTime <= 0) {
                        countdownTimer.cancel();
                        performShutdown();
                    }
                });
            }
        }, 1000, 1000); // Verzögerung von 1000ms, Wiederholung alle 1000ms.
    }

    /**
     * Bricht den laufenden Countdown ab, setzt die UI zurück und zeigt eine
     * Bestätigungsmeldung an.
     *
     * @param e Das ActionEvent, das den Klick auf den "Abbrechen"-Button darstellt.
     * @author joshuasperber
     */
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

    /**
     * Setzt die Benutzeroberfläche der ShutdownView in ihren Ausgangszustand zurück.
     * Der Countdown wird auf null gesetzt, die Labels und Buttons werden
     * entsprechend ihrer Initialkonfiguration (sichtbar/unsichtbar, aktiviert/deaktiviert) angepasst.
     * @author joshuasperber
     */
    private void resetShutdownUI() {
        remainingTime = COUNTDOWN_DURATION;
        countdownLabel.setVisible(false);
        cancelButton.setVisible(false);

        // Suche und reaktiviere den "System sperren"-Button.
        for (Component comp : cancelButton.getParent().getComponents()) {
            if (comp instanceof JButton && "System sperren".equals(((JButton)comp).getText())) {
                comp.setEnabled(true);
                break;
            }
        }
    }

    /**
     * Führt die eigentliche Systemabschaltung durch.
     * Sie setzt den Systemstatus auf "gesperrt", zeigt einen Dialog an und
     * navigiert zur Login-Ansicht zurück, um den aktuellen Benutzer auszuloggen.
     * @author joshuasperber
     */
    private void performShutdown() {
        // Setze den Systemzustand auf "gesperrt".
        SystemStateManager.getInstance().setSystemLocked(true);
        JOptionPane.showMessageDialog(this,
                "Das System wurde gesperrt. Nur IT-Administratoren können sich jetzt anmelden.",
                "System gesperrt",
                JOptionPane.INFORMATION_MESSAGE);

        // Wechsle zur LoginView, um den Benutzer auszuloggen.
        eventManager.callEvent("changeView", new Object[]{new LoginView(eventManager, loginManager)});
    }

    /**
     * Gibt eine String-Repräsentation dieser View zurück.
     * @return Ein String, der die ID und den Namen der View enthält.
     */
    @Override
    public String toString() {
        return "ShutdownView{" +
                "view_id='" + getView_id() + '\'' +
                ", view_name='" + getView_name() + '\'' +
                '}';
    }

    /**
     * Vergleicht dieses Objekt mit einem anderen auf Gleichheit.
     * @param obj Das zu vergleichende Objekt.
     * @return True, wenn die Objekte gleich sind, ansonsten False.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShutdownView that = (ShutdownView) obj;
        return getView_id().equals(that.getView_id());
    }
}