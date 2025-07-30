package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Ansicht zur Systemabschaltung für Admins.
 * @author Joshua Sperber
 */
public class ShutdownView extends View {

    public ShutdownView() {
        setView_id("view-shutdown");
        setView_name("Systemabschaltung");

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Systemabschaltung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JButton shutdownButton = new JButton("System abschalten");
        shutdownButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        shutdownButton.addActionListener(_ -> {
            // TODO: Abschaltlogik einfügen
            JOptionPane.showMessageDialog(this, "System wird heruntergefahren...", "Hinweis", JOptionPane.WARNING_MESSAGE);
            // TODO popups sind nicht unsere design language wirklich bis jetzt gewesen
            //  man könnte auch einfach so einen countdown machen im panel selber (guck auf den loginView)
            //  + dort könnte ein cancel button dann sein (falls man aus Versehen drauf gekommen ist)
            //  aber können das auch so machen, ist nur so eine idee
        });

        JPanel centerPanel = new JPanel();
        centerPanel.add(shutdownButton);
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

