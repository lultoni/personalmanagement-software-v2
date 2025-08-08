package gui.views;

import model.db.Employee;
import model.json.Department;
import model.json.Role;
import model.json.Team;
import core.CompanyStructureManager;
import core.EmployeeManager;
import core.EventManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class EmployeeDataView extends View {

    private final Employee loggedInUser;
    private final Employee employee;
    private final EmployeeManager employeeManager;
    private final EventManager eventManager;

    private boolean editMode = false;
    private Map<String, JComponent> editFields = new HashMap<>();
    private JPanel dataPanel;

    private Map<String, String> departmentIdToNameCache = new HashMap<>();
    private Map<String, String> departmentNameToIdCache = new HashMap<>();
    private Map<String, String> roleIdToNameCache = new HashMap<>();
    private Map<String, String> roleNameToIdCache = new HashMap<>();
    private Map<String, String> teamIdToNameCache = new HashMap<>();
    private Map<String, String> teamNameToIdCache = new HashMap<>();


    private BufferedImage backgroundImage;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");

    public EmployeeDataView(Employee loggedInUser, Employee employee,
                            EmployeeManager employeeManager, EventManager eventManager) {
        this.loggedInUser = loggedInUser;
        this.employee = employee;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden für EmployeeDataView.", e);
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false);

        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        initializeCaches();
        setupUI(backgroundPanel);
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
                    Map<String, Object> dept = (Map<String, Object>) obj;
                    String id = (String) dept.get("departmentId");
                    String name = (String) dept.get("name");
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
                    Map<String, Object> role = (Map<String, Object>) obj;
                    String id = (String) role.get("roleId");
                    String name = (String) role.get("name");
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
                    Map<String, Object> team = (Map<String, Object>) obj;
                    String id = (String) team.get("teamId");
                    String name = (String) team.get("name");
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


    private void setupUI(JPanel parentContainer) {
        setView_id("view-employee");
        setView_name("Mitarbeiter: " + getFullName(employee));

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(getFullName(employee), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        mainContentPanel.add(titleLabel, BorderLayout.NORTH);

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dataPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = setupButtonPanel();
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        parentContainer.add(mainContentPanel, BorderLayout.CENTER);

        refreshDataDisplay();
    }

    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        if (canEditData()) {
            JButton editButton = new JButton("Bearbeiten");
            editButton.addActionListener(this::toggleEditMode);
            buttonPanel.add(editButton);
        }

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        return buttonPanel;
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
                genderComboBox.setSelectedItem(String.valueOf(employee.getGender()));
                editFields.put(field, genderComboBox);
                return genderComboBox;
            case "departmentId":
                JComboBox<String> departmentComboBox = new JComboBox<>(departmentNameToIdCache.keySet().toArray(new String[0]));
                departmentComboBox.setSelectedItem(departmentIdToNameCache.getOrDefault(employee.getDepartmentId(), employee.getDepartmentId()));
                editFields.put(field, departmentComboBox);
                return departmentComboBox;
            case "roleId":
                JComboBox<String> roleComboBox = new JComboBox<>(roleNameToIdCache.keySet().toArray(new String[0]));
                roleComboBox.setSelectedItem(roleIdToNameCache.getOrDefault(employee.getRoleId(), employee.getRoleId()));
                editFields.put(field, roleComboBox);
                return roleComboBox;
            case "teamId":
                String[] teamNames = teamNameToIdCache.keySet().toArray(new String[0]);
                Arrays.sort(teamNames);
                JComboBox<String> teamComboBox = new JComboBox<>(teamNames);
                teamComboBox.setSelectedItem(teamIdToNameCache.getOrDefault(employee.getTeamId(), ""));
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
            employeeManager.updateEmployee(employee);

            JOptionPane.showMessageDialog(this,
                    "Änderungen erfolgreich gespeichert",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
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
                String selectedName = (String) ((JComboBox<?>) component).getSelectedItem();
                updateEmployeeField(field, selectedName);
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
            case "dateOfBirth" -> {
                try {
                    employee.setDateOfBirth(dateFormat.parse(value));
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Ungültiges Datumsformat für Geburtsdatum. Bitte verwenden Sie YYYY-MM-DD.",
                            "Fehler beim Datum",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case "hireDate" -> {
                try {
                    employee.setHireDate(dateFormat.parse(value));
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Ungültiges Datumsformat für Einstellungsdatum. Bitte verwenden Sie YYYY-MM-DD.",
                            "Fehler beim Datum",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case "departmentId" -> employee.setDepartmentId(departmentNameToIdCache.get(value));
            case "roleId" -> employee.setRoleId(roleNameToIdCache.get(value));
            case "teamId" -> employee.setTeamId(teamNameToIdCache.get(value));

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
                    "phoneNumber", "dateOfBirth", "address", "gender",
                    "departmentId", "roleId", "teamId", "hireDate"
            ));
        }

        if (!loggedInUser.equals(employee) && (loggedInUser.isHr() || loggedInUser.isItAdmin() || loggedInUser.isHrHead() || loggedInUser.equals(loggedInUser))) {
            fields.addAll(Arrays.asList(
                    "phoneNumber", "dateOfBirth", "address", "gender",
                    "departmentId", "roleId", "teamId", "hireDate"
            ));
        }

        return new ArrayList<>(new LinkedHashSet<>(fields));
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
        labels.put("teamId", "Team:");
        labels.put("roleId", "Position:");
        labels.put("username", "Benutzername:");
        labels.put("hireDate", "Einstellungsdatum:");
        return labels.getOrDefault(field, field + ":");
    }

    private String getFieldValue(String field) {
        switch (field) {
            case "firstName": return employee.getFirstName();
            case "lastName": return employee.getLastName();
            case "email": return employee.getEmail();
            case "phoneNumber": return employee.getPhoneNumber();
            case "dateOfBirth": return employee.getDateOfBirth() != null ? dateFormat.format(employee.getDateOfBirth()) : "";
            case "address": return employee.getAddress();
            case "gender": return String.valueOf(employee.getGender());
            case "departmentId": return departmentIdToNameCache.getOrDefault(employee.getDepartmentId(), employee.getDepartmentId());
            case "roleId": return roleIdToNameCache.getOrDefault(employee.getRoleId(), employee.getRoleId());
            case "teamId": return teamIdToNameCache.getOrDefault(employee.getTeamId(), "");
            case "username": return employee.getUsername();
            case "hireDate": return employee.getHireDate() != null ? dateFormat.format(employee.getHireDate()) : "";
            default: return "";
        }
    }

    private boolean canEditData() {
        return loggedInUser.equals(employee) ||
                loggedInUser.isHr() ||
                loggedInUser.isItAdmin() ||
                loggedInUser.isHrHead();
    }

    private boolean isFieldEditable(String field) {
        Set<String> nonEditable = new HashSet<>(Arrays.asList(
                "username"
        ));
        if (field.equals("hireDate") && (loggedInUser.isHr() || loggedInUser.isItAdmin() || loggedInUser.isHrHead() || loggedInUser.equals(loggedInUser))) {
            return true;
        }
        return !nonEditable.contains(field) && (loggedInUser.isItAdmin() || loggedInUser.isHr() || loggedInUser.isHrHead() || loggedInUser.equals(loggedInUser));
    }

    @Override
    public String toString() {
        return "EmployeeDataView[" + employee.getId() + "]";
    }
}