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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.LinkedHashSet;


public class EditEmployeeView extends View {

    private final EmployeeManager employeeManager;
    private final EventManager eventManager;
    private final Employee employeeToEdit;
    private final Employee currentUser;

    private boolean editMode = false;
    private Map<String, JComponent> editFields = new HashMap<>();
    private JPanel dataPanel;

    private final Map<String, String> departmentIdToNameCache = new HashMap<>();
    private final Map<String, String> departmentNameToIdCache = new HashMap<>();
    private final Map<String, String> roleIdToNameCache = new HashMap<>();
    private final Map<String, String> roleNameToIdCache = new HashMap<>();
    private final Map<String, String> teamIdToNameCache = new HashMap<>();
    private final Map<String, String> teamNameToIdCache = new HashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public EditEmployeeView(Employee currentUser, Employee employeeToEdit, EmployeeManager employeeManager, EventManager eventManager) {
        this.currentUser = currentUser;
        this.employeeToEdit = employeeToEdit;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        setView_id("view-edit-employee");
        setView_name("Mitarbeiter bearbeiten: " + getFullName(employeeToEdit));

        initializeCaches();
        setupUI();
    }

    private void initializeCaches() {
        try {
            // Abteilungen
            for (Object obj : CompanyStructureManager.getInstance().getAllDepartments()) {
                if (obj instanceof Department) {
                    Department dept = (Department) obj;
                    departmentIdToNameCache.put(dept.getDepartmentId(), dept.getName());
                    departmentNameToIdCache.put(dept.getName(), dept.getDepartmentId());
                } else if (obj instanceof Map) {
                    Map<String, Object> deptMap = (Map<String, Object>) obj;
                    String id = (String) deptMap.get("departmentId");
                    String name = (String) deptMap.get("name");
                    if (id != null && name != null) {
                        departmentIdToNameCache.put(id, name);
                        departmentNameToIdCache.put(name, id);
                    }
                }
            }

            // Rollen
            for (Object obj : CompanyStructureManager.getInstance().getAllRoles()) {
                if (obj instanceof Role) {
                    Role role = (Role) obj;
                    roleIdToNameCache.put(role.getroleId(), role.getName());
                    roleNameToIdCache.put(role.getName(), role.getroleId());
                } else if (obj instanceof Map) {
                    Map<String, Object> roleMap = (Map<String, Object>) obj;
                    String id = (String) roleMap.get("roleId");
                    String name = (String) roleMap.get("name");
                    if (id != null && name != null) {
                        roleIdToNameCache.put(id, name);
                        roleNameToIdCache.put(name, id);
                    }
                }
            }

            // Teams
            for (Object obj : CompanyStructureManager.getInstance().getAllTeams()) {
                if (obj instanceof Team) {
                    Team team = (Team) obj;
                    teamIdToNameCache.put(team.getTeamId(), team.getName());
                    teamNameToIdCache.put(team.getName(), team.getTeamId());
                } else if (obj instanceof Map) {
                    Map<String, Object> teamMap = (Map<String, Object>) obj;
                    String id = (String) teamMap.get("teamId");
                    String name = (String) teamMap.get("name");
                    if (id != null && name != null) {
                        teamIdToNameCache.put(id, name);
                        teamNameToIdCache.put(name, id);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Initialisieren der Caches: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Strukturdaten: " + e.getMessage(),
                    "Datenfehler",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Mitarbeiter bearbeiten/löschen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = setupButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        refreshDataDisplay();
    }

    private void refreshDataDisplay() {
        dataPanel.removeAll();
        editFields.clear();

        List<String> visibleFields = getVisibleFieldsForUser();
        JPanel gridPanel = createDataGrid(visibleFields);

        dataPanel.add(gridPanel);
        revalidate();
        repaint();
    }

    private JPanel createDataGrid(List<String> fields) {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            gbc.gridx = 0;
            gbc.gridy = i;
            gridPanel.add(createFieldLabel(field), gbc);

            gbc.gridx = 1;
            gridPanel.add(createFieldComponent(field), gbc);
        }

        return gridPanel;
    }

    private JLabel createFieldLabel(String field) {
        JLabel label = new JLabel(getFieldLabel(field));
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JComponent createFieldComponent(String field) {
        if (editMode && isFieldEditable(field)) {
            return createEditableField(field);
        }
        return createDisplayField(field);
    }

    private JComponent createEditableField(String field) {
        switch (field) {
            case "gender":
                JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"M", "F", "D"});
                genderComboBox.setSelectedItem(String.valueOf(employeeToEdit.getGender()));
                editFields.put(field, genderComboBox);
                return genderComboBox;
            case "departmentId":
                JComboBox<String> departmentComboBox = new JComboBox<>(departmentNameToIdCache.keySet().toArray(new String[0]));
                departmentComboBox.setSelectedItem(departmentIdToNameCache.getOrDefault(employeeToEdit.getDepartmentId(), employeeToEdit.getDepartmentId()));
                editFields.put(field, departmentComboBox);
                return departmentComboBox;
            case "roleId":
                JComboBox<String> roleComboBox = new JComboBox<>(roleNameToIdCache.keySet().toArray(new String[0]));
                roleComboBox.setSelectedItem(roleIdToNameCache.getOrDefault(employeeToEdit.getRoleId(), employeeToEdit.getRoleId()));
                editFields.put(field, roleComboBox);
                return roleComboBox;
            case "teamId":
                String[] teamNames = teamNameToIdCache.keySet().toArray(new String[0]);
                Arrays.sort(teamNames);
                JComboBox<String> teamComboBox = new JComboBox<>(teamNames);
                teamComboBox.setSelectedItem(teamIdToNameCache.getOrDefault(employeeToEdit.getTeamId(), ""));
                editFields.put(field, teamComboBox);
                return teamComboBox;
            default:
                JTextField textField = new JTextField(getFieldValue(field), 20);
                editFields.put(field, textField);
                return textField;
        }
    }

    private JComponent createDisplayField(String field) {
        JLabel label = new JLabel(getFieldValue(field));
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton editButton = new JButton("Bearbeiten");
        editButton.addActionListener(this::toggleEditMode);
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Mitarbeiter löschen");
        deleteButton.setBackground(new Color(31, 32, 35));
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(this::onDeleteButtonClicked);
        buttonPanel.add(deleteButton);

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void toggleEditMode(ActionEvent e) {
        if (editMode) {
            if (!saveChanges()) {
                return;
            }
        }
        editMode = !editMode;
        JButton button = (JButton) e.getSource();
        button.setText(editMode ? "Speichern" : "Bearbeiten");
        refreshDataDisplay();
    }

    private boolean saveChanges() {
        try {
            updateEmployeeFromFields();
            employeeManager.updateEmployee(employeeToEdit);
            JOptionPane.showMessageDialog(this, "Änderungen erfolgreich gespeichert", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    private void updateEmployeeFromFields() {
        editFields.forEach((field, component) -> {
            if (component instanceof JTextField) {
                String value = ((JTextField) component).getText();
                updateEmployeeField(field, value);
            } else if (component instanceof JComboBox) {
                String selectedName = (String) ((JComboBox<?>) component).getSelectedItem();
                updateEmployeeField(field, selectedName);
            }
        });
    }

    private void updateEmployeeField(String field, String value) {
        switch (field) {
            case "firstName":
                employeeToEdit.setFirstName(value);
                break;
            case "lastName":
                employeeToEdit.setLastName(value);
                break;
            case "email":
                employeeToEdit.setEmail(value);
                break;
            case "phoneNumber":
                employeeToEdit.setPhoneNumber(value);
                break;
            case "address":
                employeeToEdit.setAddress(value);
                break;
            case "gender":
                employeeToEdit.setGender(value.charAt(0));
                break;
            case "dateOfBirth":
                try {
                    employeeToEdit.setDateOfBirth(dateFormat.parse(value));
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ungültiges Datumsformat für Geburtsdatum.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "hireDate":
                try {
                    employeeToEdit.setHireDate(dateFormat.parse(value));
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ungültiges Datumsformat für Einstellungsdatum.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "departmentId":
                employeeToEdit.setDepartmentId(departmentNameToIdCache.get(value));
                break;
            case "roleId":
                employeeToEdit.setRoleId(roleNameToIdCache.get(value));
                break;
            case "teamId":
                employeeToEdit.setTeamId(teamNameToIdCache.get(value));
                break;
        }
    }

    private String getFullName(Employee emp) {
        return emp.getFirstName() + " " + emp.getLastName();
    }

    private List<String> getVisibleFieldsForUser() {
        return new ArrayList<>(new LinkedHashSet<>(Arrays.asList(
                "firstName", "lastName", "email", "phoneNumber", "dateOfBirth", "address", "gender",
                "departmentId", "roleId", "teamId", "username", "hireDate", "employmentStatus"
        )));
    }

    private String getFieldLabel(String field) {
        Map<String, String> labels = new HashMap<>();
        labels.put("firstName", "Vorname:");
        labels.put("lastName", "Nachname:");
        labels.put("email", "E-Mail:");
        labels.put("phoneNumber", "Telefon:");
        labels.put("dateOfBirth", "Geburtsdatum:");
        labels.put("address", "Adresse:");
        labels.put("gender", "Geschlecht:");
        labels.put("departmentId", "Abteilung:");
        labels.put("roleId", "Position:");
        labels.put("teamId", "Team:");
        labels.put("username", "Benutzername:");
        labels.put("hireDate", "Einstellungsdatum:");
        labels.put("employmentStatus", "Status:");
        return labels.getOrDefault(field, field + ":");
    }

    private String getFieldValue(String field) {
        switch (field) {
            case "firstName":
                return employeeToEdit.getFirstName();
            case "lastName":
                return employeeToEdit.getLastName();
            case "email":
                return employeeToEdit.getEmail();
            case "phoneNumber":
                return employeeToEdit.getPhoneNumber();
            case "dateOfBirth":
                return employeeToEdit.getDateOfBirth() != null ? dateFormat.format(employeeToEdit.getDateOfBirth()) : "";
            case "address":
                return employeeToEdit.getAddress();
            case "gender":
                return String.valueOf(employeeToEdit.getGender());
            case "departmentId":
                return departmentIdToNameCache.getOrDefault(employeeToEdit.getDepartmentId(), employeeToEdit.getDepartmentId());
            case "roleId":
                return roleIdToNameCache.getOrDefault(employeeToEdit.getRoleId(), employeeToEdit.getRoleId());
            case "teamId":
                return teamIdToNameCache.getOrDefault(employeeToEdit.getTeamId(), "Kein Team");
            case "username":
                return employeeToEdit.getUsername();
            case "hireDate":
                return employeeToEdit.getHireDate() != null ? dateFormat.format(employeeToEdit.getHireDate()) : "";
            case "employmentStatus":
                return employeeToEdit.getEmploymentStatus();
            default:
                return "";
        }
    }

    private boolean isFieldEditable(String field) {
        Set<String> nonEditable = new HashSet<>(Arrays.asList("username"));
        return !nonEditable.contains(field);
    }

    private void onDeleteButtonClicked(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Sind Sie sicher, dass Sie den Mitarbeiter " + employeeToEdit.getFirstName() + " " + employeeToEdit.getLastName() + " löschen möchten?",
                "Mitarbeiter löschen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeManager.removeEmployee(employeeToEdit.getId());
                JOptionPane.showMessageDialog(this, "Mitarbeiter wurde gelöscht.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                eventManager.callEvent("moveBackView", null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Löschen: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}