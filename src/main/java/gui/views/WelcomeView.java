package gui.views;

import core.EmployeeManager;
import db.dao.EmployeeDao;
import model.db.Employee;
import util.PersistentInformationReader;

import javax.swing.*;
        import java.awt.*;

/**
 * Die Startseite / Willkommensansicht für das IT-Admin-System.
 * @author Joshua Sperber
 */
public class WelcomeView extends View {

    public WelcomeView(EmployeeManager employeeManager) {
        setView_id("view-welcome");
        setView_name("Willkommensansicht");

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Willkommen im System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel("BOB the Builder Company – " + getRoleString(employeeManager), SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        centerPanel.add(titleLabel);
        centerPanel.add(subtitleLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private String getRoleString(EmployeeManager employeeManager) {
        String roleString = "Mitarbeiter";
        Employee loggedInEmployee = employeeManager.getEmployeeById(PersistentInformationReader.getLoggedInUserId());
        if (loggedInEmployee.isItAdmin()) return "IT-Admin";
        if (loggedInEmployee.isHrHead()) return "HR-Head";
        if (loggedInEmployee.isHr()) return "HR-Mitarbeiter";
        if (loggedInEmployee.isManager()) return "Manager";
        return roleString;
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
