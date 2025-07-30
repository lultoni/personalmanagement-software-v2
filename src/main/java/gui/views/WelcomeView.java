package gui.views;

import javax.swing.*;
        import java.awt.*;

/**
 * Die Startseite / Willkommensansicht für das IT-Admin-System.
 * @author —
 */
public class WelcomeView extends JPanel {

    public WelcomeView() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Willkommen im System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel("BOB the Builder Company – IT-Admin", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        centerPanel.add(titleLabel);
        centerPanel.add(subtitleLabel);

        add(centerPanel, BorderLayout.CENTER);
    }
}
