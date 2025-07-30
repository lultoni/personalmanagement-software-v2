package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Ansicht zur Systemabschaltung für Admins.
 */
public class ShutdownView extends JPanel {

    public ShutdownView() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Systemabschaltung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JButton shutdownButton = new JButton("System abschalten");
        shutdownButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        shutdownButton.addActionListener(_ -> {
            // TODO: Abschaltlogik einfügen
            JOptionPane.showMessageDialog(this, "System wird heruntergefahren...", "Hinweis", JOptionPane.WARNING_MESSAGE);
        });

        JPanel centerPanel = new JPanel();
        centerPanel.add(shutdownButton);
        add(centerPanel, BorderLayout.CENTER);
    }
}

