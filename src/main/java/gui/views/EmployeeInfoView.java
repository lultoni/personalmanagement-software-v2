package gui.views;

import javax.swing.*;
        import java.awt.*;

/**
 * Ansicht für persönliche Informationen des Mitarbeiters.
 * @author Joshua Sperber
 */
public class EmployeeInfoView extends View {

    public EmployeeInfoView() {

        setView_id("view-employee_info_view");
        setView_name("Mitarbeiter Informationen");

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Persönliche Informationen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JTextField());

        infoPanel.add(new JLabel("Geburtstag:"));
        infoPanel.add(new JTextField());

        infoPanel.add(new JLabel("Adresse:"));
        infoPanel.add(new JTextField());

        infoPanel.add(new JLabel("Gehalt:"));
        infoPanel.add(new JTextField());

        infoPanel.add(new JLabel("Rolle:"));
        infoPanel.add(new JTextField());

        infoPanel.add(new JLabel("Vorgesetzter:"));
        infoPanel.add(new JTextField());

        add(infoPanel, BorderLayout.CENTER);
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

