package gui.views;

import model.db.Employee;
import core.CompanyStructureManager;
import core.EmployeeManager;
import core.EventManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * Ansicht für Mitarbeiterdaten mit Bearbeitungsfunktion und anzeige von Abteilungs-/Rollen-Namen
 * @version 3.0
 */
public class EmployeeDataView extends View {

    private final Employee loggedInUser;
    private final Employee employee;
    private final EmployeeManager employeeManager;
    private final EventManager eventManager;

    private boolean editMode = false;
    private Map<String, JComponent> editFields = new HashMap<>();
    private JPanel dataPanel;
    private Map<String, String> departmentCache = new HashMap<>();
    private Map<String, String> roleCache = new HashMap<>();

    public EmployeeDataView(Employee loggedInUser, Employee employee,
                            EmployeeManager employeeManager, EventManager eventManager) {
        this.loggedInUser = loggedInUser;
        this.employee = employee;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        initializeCaches();
        setupUI();
    }

    private void initializeCaches() {
        try {
            CompanyStructureManager csm = CompanyStructureManager.getInstance();
            csm.getAllDepartments().forEach(dept ->
                    departmentCache.put(dept.getId(), dept.getName()));
            csm.getAllRoles().forEach(role ->
                    roleCache.put(role.getId(), role.getName()));
        } catch (Exception e) {
            System.err.println("Fehler beim Initialisieren der Caches: " + e.getMessage());
        }
    }

    private void setupUI() {
        setView_id("view-employee");
        setView_name("Mitarbeiter: " + getFullName(employee));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel titleLabel = new JLabel(getFullName(employee), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Hauptinhalt
        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        setupButtonPanel();
        refreshDataDisplay();
    }

    private void setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (canEditData()) {
            JButton editButton = new JButton("Bearbeiten");
            editButton.addActionListener(this::toggleEditMode);
            buttonPanel.add(editButton);
        }

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
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
        gridPanel.setBackground(Color.WHITE);
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
        return label;
    }

    private JComponent createFieldComponent(String field) {
        if (editMode && isFieldEditable(field)) {
            return createEditableField(field);
        }
        return createDisplayField(field);
    }

    private JComponent createEditableField(String field) {
        if (field.equals("gender")) {
            JComboBox<String> comboBox = new JComboBox<>(new String[]{"M", "F", "D"});
            comboBox.setSelectedItem(String.valueOf(employee.getGender()));
            editFields.put(field, comboBox);
            return comboBox;
        } else {
            JTextField textField = new JTextField(getFieldValue(field), 20);
            editFields.put(field, textField);
            return textField;
        }
    }

    private JComponent createDisplayField(String field) {
        JLabel label = new JLabel(getFieldValue(field));
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private void toggleEditMode(ActionEvent e) {
        if (editMode) {
            saveChanges();
        }

        editMode = !editMode;
        JButton button = (JButton) e.getSource();
        button.setText(editMode ? "Speichern" : "Bearbeiten");
        refreshDataDisplay();
    }

    private void saveChanges() {
        try {
            updateEmployeeFromFields();
            employeeManager.updateEmployee(employee);
            JOptionPane.showMessageDialog(this, "Änderungen gespeichert",
                    "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployeeFromFields() {
        editFields.forEach((field, component) -> {
            if (component instanceof JTextField) {
                updateTextField(field, (JTextField) component);
            } else if (component instanceof JComboBox) {
                updateComboBox(field, (JComboBox<?>) component);
            }
        });
    }

    private void updateTextField(String field, JTextField textField) {
        String value = textField.getText();
        switch (field) {
            case "firstName" -> employee.setFirstName(value);
            case "lastName" -> employee.setLastName(value);
            case "email" -> employee.setEmail(value);
            case "phoneNumber" -> employee.setPhoneNumber(value);
            case "address" -> employee.setAddress(value);
            // Weitere bearbeitbare Felder...
        }
    }

    private void updateComboBox(String field, JComboBox<?> comboBox) {
        if (field.equals("gender")) {
            employee.setGender(((String) comboBox.getSelectedItem()).charAt(0));
        }
    }

    // Hilfsmethoden für Datenanzeige
    private String getFullName(Employee emp) {
        return emp.getFirstName() + " " + emp.getLastName();
    }

    private List<String> getVisibleFieldsForUser() {
        List<String> fields = new ArrayList<>(Arrays.asList(
                "firstName", "lastName", "username", "email"
        ));

        if (loggedInUser.equals(employee)) {
            fields.addAll(Arrays.asList(
                    "phoneNumber", "dateOfBirth", "address", "gender"
            ));
        }

        if (loggedInUser.isHr() || loggedInUser.isItAdmin()) {
            fields.addAll(Arrays.asList(
                    "departmentId", "roleId", "hireDate", "employmentStatus"
            ));
        }

        return fields;
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
        labels.put("username", "Benutzername:");
        labels.put("hireDate", "Einstellungsdatum:");
        labels.put("employmentStatus", "Status:");
        return labels.getOrDefault(field, field + ":");
    }

    private String getFieldValue(String field) {
        switch (field) {
            case "firstName": return employee.getFirstName();
            case "lastName": return employee.getLastName();
            case "email": return employee.getEmail();
            case "phoneNumber": return employee.getPhoneNumber();
            case "dateOfBirth": return formatDate(employee.getDateOfBirth());
            case "address": return employee.getAddress();
            case "gender": return String.valueOf(employee.getGender());
            case "departmentId": return departmentCache.getOrDefault(employee.getDepartmentId(), employee.getDepartmentId());
            case "roleId": return roleCache.getOrDefault(employee.getRoleId(), employee.getRoleId());
            case "username": return employee.getUsername();
            case "hireDate": return formatDate(employee.getHireDate());
            case "employmentStatus": return employee.getEmploymentStatus();
            default: return "";
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        // Hier könnte ein besserer Datumsformatierer eingefügt werden
        return date.toString();
    }

    private boolean canEditData() {
        return loggedInUser.equals(employee) ||
                loggedInUser.isHr() ||
                loggedInUser.isItAdmin();
    }

    private boolean isFieldEditable(String field) {
        Set<String> nonEditable = new HashSet<>(Arrays.asList(
                "username", "departmentId", "roleId", "hireDate",
                "employmentStatus", "dateOfBirth"
        ));
        return !nonEditable.contains(field);
    }

    @Override
    public String toString() {
        return "EmployeeDataView[" + employee.getId() + "]";
    }
}