package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Schulungsübersicht für Mitarbeiter.
 * @author Joshua Sperber
 */
public class TrainingView extends View {

    public TrainingView() {
        setView_id("view-training");
        setView_name("Schulungsübersicht");

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Schulungsübersicht", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton completedTrainingsButton = new JButton("Absolvierte Schulungen");
        JButton openTrainingsButton = new JButton("Offene Schulungen");
        JButton potentialTrainingsButton = new JButton("Mögliche Schulungen");

        // TODO: Hier könnte man ActionListener ergänzen für weitere Views

        buttonPanel.add(completedTrainingsButton);
        buttonPanel.add(openTrainingsButton);
        buttonPanel.add(potentialTrainingsButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public String toString() {
        // TODO write this function
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        // TODO write this function
        return super.equals(obj);
    }
}
