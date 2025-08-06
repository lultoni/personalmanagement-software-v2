package gui.views;

import core.EmployeeManager;
import core.EventManager;
import model.db.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Die SystemSettingsView dient als Hauptmenü für HR-Verwaltungsaufgaben.
 * Sie enthält Buttons, um neue Mitarbeiter hinzuzufügen oder bestehende Mitarbeiter zu bearbeiten.
 * Die Navigation zu den entsprechenden Ansichten wird über den EventManager gesteuert.
 */
public class SystemSettingsView extends View {

    private final EventManager eventManager;

    public SystemSettingsView(EventManager eventManager, EmployeeManager employeeManager, Employee currentUser) {
        this.eventManager = eventManager;

        // Setze das Layout für diese Ansicht
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 20));

        // Button für das Hinzufügen von Mitarbeitern
        JButton addEmployeeButton = new JButton("Mitarbeiter hinzufügen");
        addEmployeeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        addEmployeeButton.setPreferredSize(new Dimension(300, 60));
        addEmployeeButton.addActionListener(this::onAddEmployeeButtonClicked);

        // Button für das Bearbeiten von Mitarbeitern
        JButton editEmployeeButton = new JButton("Mitarbeiter bearbeiten");
        editEmployeeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        editEmployeeButton.setPreferredSize(new Dimension(300, 60));
        editEmployeeButton.addActionListener(this::onEditEmployeeButtonClicked);

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(editEmployeeButton);

        // Füge den Button-Panel in die Mitte der Ansicht
        add(buttonPanel);
    }

    private void onAddEmployeeButtonClicked(ActionEvent e) {
        // Rufe ein Event auf, das die AddEmployeeView anzeigt
        eventManager.callEvent("showAddEmployeeView", null);
    }

    private void onEditEmployeeButtonClicked(ActionEvent e) {
        // Rufe ein Event auf, das die EditEmployeeView anzeigt
        eventManager.callEvent("showEditEmployeeView", null);
    }
}
