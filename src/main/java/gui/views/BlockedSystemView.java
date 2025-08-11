package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * View, die angezeigt wird, wenn das System durch einen Administrator blockiert wurde.
 *
 * @author Elias Glauert, joshuasperber
 * @version 1.1
 * @since 2025-07-12
 */
public class BlockedSystemView extends View {

    public BlockedSystemView() {
        setView_id("view-blocked");
        setView_name("System Blockiert");

        // Hauptpanel mit zentrierter Nachricht
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Panel für Inhalt
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Icon
        JLabel iconLabel = new JLabel("\uD83D\uDEE0", SwingConstants.CENTER); // 🛠️
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Titel
        JLabel titleLabel = new JLabel("System derzeit nicht verfügbar");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Beschreibung
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>Das System wurde vorübergehend durch einen Administrator deaktiviert.<br>Bitte versuchen Sie es später erneut oder kontaktieren Sie Ihre IT-Abteilung.</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(Color.DARK_GRAY);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Inhalte hinzufügen
        messagePanel.add(iconLabel);
        messagePanel.add(titleLabel);
        messagePanel.add(messageLabel);

        // zentriert ins Hauptpanel einfügen
        mainPanel.add(messagePanel);

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
        return "BlockedView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (obj.getClass() != this.getClass()) return false;
        return true;
    }
}
