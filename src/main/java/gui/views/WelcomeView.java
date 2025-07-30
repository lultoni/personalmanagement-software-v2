package gui.views;

import javax.swing.*;
        import java.awt.*;

/**
 * Die Startseite / Willkommensansicht für das IT-Admin-System.
 * @author Joshua Sperber
 */
public class WelcomeView extends View {

    public WelcomeView() {
        setView_id("view-welcome");
        setView_name("Willkommensansicht");

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Willkommen im System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel("BOB the Builder Company – IT-Admin", SwingConstants.CENTER); // TODO flexibel im aus dem system die rolle zeigen
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        centerPanel.add(titleLabel);
        centerPanel.add(subtitleLabel);

        add(centerPanel, BorderLayout.CENTER);
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
