package gui.views;

import model.db.Employee;
import model.db.Department;
import model.db.Role;
import core.CompanyStructureManager;
import core.EmployeeManager;
import core.EventManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

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

            // Abteilungscache
            for (Department dept : csm.getAllDepartments()) {
                try {
                    departmentCache.put(dept.getId(), dept.getName());
                } catch (Exception e) {
                    try {
                        departmentCache.put(dept.getDepartmentId(), dept.getName());
                    } catch (Exception e2) {
                        System.err.println("Could not extract department ID");
                    }
                }
            }

            // Rollencache
            for (Role role : csm.getAllRoles()) {
                try {
                    roleCache.put(role.getId(), role.getName());
                } catch (Exception e) {
                    try {
                        roleCache.put(role.getRoleId(), role.getName());
                    } catch (Exception e2) {
                        System.err.println("Could not extract role ID");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Cache Initialization Error: " + e.getMessage());
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

            // Update-Logik ohne employeeDao.updateEmployee()
            if (employeeManager != null) {
                // Erstelle aktualisierten Employee
                Employee updatedEmployee = new Employee(
                        employee.getId(),
                        employee.getUsername(),
                        employee.getPassword(),
                        employee.getPermissionString(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getDateOfBirth(),
                        employee.getAddress(),
                        employee.getGender(),
                        employee.getHireDate(),
                        employee.getEmploymentStatus(),
                        employee.getDepartmentId(),
                        employee.getTeamId(),
                        employee.getRoleId(),
                        employee.getQualifications(),
                        employee.getCompletedTrainings(),
                        employee.getManagerId(),
                        employee.isItAdmin(),
                        employee.isHr(),
                        employee.isHrHead(),
                        employee.isManager()
                );

                // Entferne alten und füge neuen Employee hinzu
                employeeManager.removeEmployee(employee.getId());
                employeeManager.addEmployee(
                        updatedEmployee,
                        updatedEmployee.getPassword(),
                        updatedEmployee.getPermissionString(),
                        updatedEmployee.getFirstName(),
                        updatedEmployee.getLastName(),
                        updatedEmployee.getEmail(),
                        updatedEmployee.getPhoneNumber(),
                        updatedEmployee.getDateOfBirth(),
                        updatedEmployee.getAddress(),
                        updatedEmployee.getGender(),
                        updatedEmployee.getHireDate(),
                        updatedEmployee.getEmploymentStatus(),
                        updatedEmployee.getDepartmentId(),
                        updatedEmployee.getTeamId(),
                        updatedEmployee.getRoleId(),
                        updatedEmployee.getQualifications(),
                        updatedEmployee.getCompletedTrainings(),
                        updatedEmployee.getManagerId(),
                        updatedEmployee.isItAdmin(),
                        updatedEmployee.isHr(),
                        updatedEmployee.isHrHead(),
                        updatedEmployee.isManager()
                );

                JOptionPane.showMessageDialog(this,
                        "Änderungen erfolgreich gespeichert",
                        "Erfolg",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            throw new IllegalStateException("EmployeeManager nicht verfügbar");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Speichern: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
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
                String value = (String) ((JComboBox<?>) component).getSelectedItem();
                updateEmployeeField(field, value);
            }
        });
    }

    private void updateEmployeeField(String field, String value) {
        switch (field) {
            case "firstName" -> employee.setFirstName(value);
            case "lastName" -> employee.setLastName(value);
            case "email" -> employee.setEmail(value);
            case "phoneNumber" -> employee.setPhoneNumber(value);
            case "address" -> employee.setAddress(value);
            case "gender" -> employee.setGender(value.charAt(0));
        }
    }

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
            case "dateOfBirth": return employee.getDateOfBirth() != null ? employee.getDateOfBirth().toString() : "";
            case "address": return employee.getAddress();
            case "gender": return String.valueOf(employee.getGender());
            case "departmentId": return departmentCache.getOrDefault(employee.getDepartmentId(), employee.getDepartmentId());
            case "roleId": return roleCache.getOrDefault(employee.getRoleId(), employee.getRoleId());
            case "username": return employee.getUsername();
            case "hireDate": return employee.getHireDate() != null ? employee.getHireDate().toString() : "";
            case "employmentStatus": return employee.getEmploymentStatus();
            default: return "";
        }
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