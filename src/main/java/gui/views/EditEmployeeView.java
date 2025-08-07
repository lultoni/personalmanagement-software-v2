package gui.views;

import core.CompanyStructureManager;
import core.EmployeeManager;
import core.EventManager;
import model.db.Employee;
import model.json.Department;
import model.json.Role;
import model.json.Team;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Diese Ansicht dient zum Bearbeiten und Löschen eines bestimmten Mitarbeiters.
 * Sie zeigt die Details eines Mitarbeiters an und bietet die Option, ihn zu entfernen.
 */
public class EditEmployeeView extends View {

    private final EmployeeManager employeeManager;
    private final EventManager eventManager;
    private final Employee employeeToEdit;
    private final Employee currentUser;
    private final Map<String, String> departmentIdToNameCache = new HashMap<>();
    private final Map<String, String> roleIdToNameCache = new HashMap<>();
    private final Map<String, String> teamIdToNameCache = new HashMap<>();


    /**
     * Konstruktor für die EditEmployeeView.
     *
     * @param currentUser       Der aktuell eingeloggte Benutzer (für Berechtigungsprüfungen).
     * @param employeeToEdit    Der Mitarbeiter, der bearbeitet oder gelöscht werden soll.
     * @param employeeManager   Die Instanz des EmployeeManager.
     * @param eventManager      Die Instanz des EventManager.
     */
    public EditEmployeeView(Employee currentUser, Employee employeeToEdit, EmployeeManager employeeManager, EventManager eventManager) {
        this.currentUser = currentUser;
        this.employeeToEdit = employeeToEdit;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        setView_id("view-edit-employee");
        setView_name("Mitarbeiter bearbeiten/löschen");

        initializeCaches();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Mitarbeiter bearbeiten/löschen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel für die Mitarbeiterinformationen
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Mitarbeiter Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Hinzufügen von Mitarbeiterdetails basierend auf der Employee-Klasse
        gbc.gridx = 0; gbc.gridy = row++;
        detailsPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(employeeToEdit.getId())), gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        detailsPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(employeeToEdit.getFirstName() + " " + employeeToEdit.getLastName()), gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        detailsPanel.add(new JLabel("E-Mail:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(employeeToEdit.getEmail()), gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        detailsPanel.add(new JLabel("Abteilung:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(departmentIdToNameCache.getOrDefault(employeeToEdit.getDepartmentId(), employeeToEdit.getDepartmentId())), gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        detailsPanel.add(new JLabel("Rolle:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(roleIdToNameCache.getOrDefault(employeeToEdit.getRoleId(), employeeToEdit.getRoleId())), gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        detailsPanel.add(new JLabel("Team:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(teamIdToNameCache.getOrDefault(employeeToEdit.getTeamId(), "Kein Team")), gbc);


        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Panel für die Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton deleteButton = new JButton("Mitarbeiter löschen");
        deleteButton.setBackground(new Color(31, 32, 35)); // Rote Farbe für Löschfunktion
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(this::onDeleteButtonClicked);
        buttonPanel.add(deleteButton);

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeCaches() {
        try {
            // Abteilungen
            List<Department> departments = (List<Department>) CompanyStructureManager.getInstance().getAllDepartments();
            if (departments != null) {
                for (Department dept : departments) {
                    departmentIdToNameCache.put(dept.getDepartmentId(), dept.getName());
                }
            }

            // Rollen
            List<Role> roles = (List<Role>) CompanyStructureManager.getInstance().getAllRoles();
            if (roles != null) {
                for (Role role : roles) {
                    roleIdToNameCache.put(role.getroleId(), role.getName());
                }
            }

            // Teams
            List<Team> teams = (List<Team>) CompanyStructureManager.getInstance().getAllTeams();
            if (teams != null) {
                for (Team team : teams) {
                    teamIdToNameCache.put(team.getTeamId(), team.getName());
                }
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Initialisieren der Caches fuer Abteilungen/Rollen/Teams: " + e.getMessage());
            // Fallback auf die IDs, falls der Ladevorgang fehlschlägt
        }
    }


    private void onDeleteButtonClicked(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Sind Sie sicher, dass Sie den Mitarbeiter " + employeeToEdit.getFirstName() + " " + employeeToEdit.getLastName() + " löschen möchten?\n" +
                        "Diese Aktion kann nicht rückgängig gemacht werden.",
                "Mitarbeiter löschen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeManager.removeEmployee(employeeToEdit.getId());
                JOptionPane.showMessageDialog(this,
                        "Mitarbeiter " + employeeToEdit.getFirstName() + " " + employeeToEdit.getLastName() + " wurde erfolgreich gelöscht.",
                        "Erfolg",
                        JOptionPane.INFORMATION_MESSAGE);
                // Zurück zur vorherigen Ansicht (SearchView) navigieren
                eventManager.callEvent("moveBackView", null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Fehler beim Löschen des Mitarbeiters: " + ex.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}