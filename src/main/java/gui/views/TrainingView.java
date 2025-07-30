package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Schulungsübersicht für Mitarbeiter.
 */
public class TrainingView extends JPanel {

    public TrainingView() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Schulungsübersicht", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton absolvierteButton = new JButton("Absolvierte Schulungen");
        JButton offeneButton = new JButton("Offene Schulungen");
        JButton möglicheButton = new JButton("Mögliche Schulungen");

        // TODO: Hier könnte man ActionListener ergänzen für weitere Views

        buttonPanel.add(absolvierteButton);
        buttonPanel.add(offeneButton);
        buttonPanel.add(möglicheButton);

        add(buttonPanel, BorderLayout.CENTER);
    }
}
