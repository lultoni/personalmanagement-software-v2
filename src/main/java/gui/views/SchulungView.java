package gui.views;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;


/**
 * Schulungsübersicht für Mitarbeiter.
 * @author Joshua Sperber
 */
public class SchulungView extends View {

    public SchulungView() {
        setView_id("view-training");
        setView_name("Schulungsübersicht");

        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Hintergrundpanel mit halbtransparentem Bild
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

        // Titel
        JLabel titleLabel = new JLabel("Schulungsübersicht", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setOpaque(false);

        JButton completedTrainingsButton = new JButton("Absolvierte Schulungen");
        JButton openTrainingsButton = new JButton("Offene Schulungen");
        JButton potentialTrainingsButton = new JButton("Mögliche Schulungen");

        for (JButton btn : new JButton[]{completedTrainingsButton, openTrainingsButton, potentialTrainingsButton}) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setFocusPainted(false);
        }

        buttonPanel.add(completedTrainingsButton);
        buttonPanel.add(openTrainingsButton);
        buttonPanel.add(potentialTrainingsButton);

        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Alles zum Haupt-View hinzufügen
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }
}