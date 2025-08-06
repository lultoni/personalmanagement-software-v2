package gui.views;

import core.EmployeeManager;
import core.EventManager;
import model.db.Employee;
import model.json.Department;
import model.json.Role;
import model.json.Team; // NEU: Import fuer die Team-Klasse
import core.CompanyStructureManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays; // Fuer Arrays.sort

public class AddingEmployeesView extends View {

    private final EmployeeManager employeeManager;
    private final EventManager eventManager;

    // UI-Komponenten
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTextField dateOfBirthField; // Format YYYY-MM-DD
    private JTextField addressField;
    private JComboBox<String> genderDropdown;
    private JTextField hireDateField; // Format YYYY-MM-DD
    private JComboBox<String> employmentStatusDropdown;
    private JComboBox<String> departmentDropdown;
    private JComboBox<String> teamDropdown;
    private JComboBox<String> roleDropdown;
    private JTextField qualificationsField;
    private JTextField completedTrainingsField;
    private JTextField managerIdField;
    private JCheckBox itAdminCheckBox;
    private JCheckBox hrCheckBox;
    private JCheckBox hrHeadCheckBox;
    private JCheckBox isManagerCheckBox;

    // Caches fuer Dropdowns
    private Map<String, String> departmentNameCache = new HashMap<>();
    private Map<String, String> departmentIdCache = new HashMap<>();
    private Map<String, String> roleNameCache = new HashMap<>();
    private Map<String, String> roleIdCache = new HashMap<>();
    private Map<String, String> teamNameCache = new HashMap<>();
    private Map<String, String> teamIdCache = new HashMap<>();


    public AddingEmployeesView(EmployeeManager employeeManager, EventManager eventManager) {
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        setView_id("view-add-employee");
        setView_name("Mitarbeiter hinzufuegen");

        // Hintergrundbild laden
        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
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

        // Titel
        JLabel titleLabel = new JLabel("Neuen Mitarbeiter hinzufuegen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Formular-Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ********************************************************************
        // KORREKTUR: Caches initialisieren VOR der Initialisierung der Dropdowns
        // ********************************************************************
        initializeCaches();
        // ********************************************************************

        // Zeilen fuer Eingabefelder
        int row = 0;

        // Benutzername
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Benutzername:"), gbc);
        gbc.gridx = 1; usernameField = new JTextField(20); formPanel.add(usernameField, gbc);
        row++;

        // Passwort
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Passwort:"), gbc);
        gbc.gridx = 1; passwordField = new JPasswordField(20); formPanel.add(passwordField, gbc);
        row++;

        // Vorname
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Vorname:"), gbc);
        gbc.gridx = 1; firstNameField = new JTextField(20); formPanel.add(firstNameField, gbc);
        row++;

        // Nachname
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Nachname:"), gbc);
        gbc.gridx = 1; lastNameField = new JTextField(20); formPanel.add(lastNameField, gbc);
        row++;

        // E-Mail
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("E-Mail:"), gbc);
        gbc.gridx = 1; emailField = new JTextField(20); formPanel.add(emailField, gbc);
        row++;

        // Telefonnummer
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Telefonnummer:"), gbc);
        gbc.gridx = 1; phoneNumberField = new JTextField(20); formPanel.add(phoneNumberField, gbc);
        row++;

        // Geburtsdatum (YYYY-MM-DD)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Geburtsdatum (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; dateOfBirthField = new JTextField(20); formPanel.add(dateOfBirthField, gbc);
        row++;

        // Adresse
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1; addressField = new JTextField(20); formPanel.add(addressField, gbc);
        row++;

        // Geschlecht
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Geschlecht:"), gbc);
        gbc.gridx = 1; genderDropdown = new JComboBox<>(new String[]{"M", "F", "D"}); formPanel.add(genderDropdown, gbc);
        row++;

        // Einstellungsdatum (YYYY-MM-DD)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Einstellungsdatum (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; hireDateField = new JTextField(20); formPanel.add(hireDateField, gbc);
        row++;

        // Beschäftigungsstatus
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Beschaeftigungsstatus:"), gbc);
        gbc.gridx = 1; employmentStatusDropdown = new JComboBox<>(new String[]{"Active", "On Leave", "Terminated", "Probation", "Retired"}); formPanel.add(employmentStatusDropdown, gbc);
        row++;

        // Abteilung
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Abteilung:"), gbc);
        // ********************************************************************
        // KORREKTUR: Dropdowns NACH initializeCaches() initialisieren
        // ********************************************************************
        departmentDropdown = new JComboBox<>(departmentNameCache.keySet().toArray(new String[0]));
        formPanel.add(departmentDropdown, gbc);
        row++;

        // Rolle
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Rolle:"), gbc);
        roleDropdown = new JComboBox<>(roleNameCache.keySet().toArray(new String[0]));
        gbc.gridx = 1; formPanel.add(roleDropdown, gbc);
        row++;

        // Team (Optional)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Team (Optional):"), gbc);
        String[] teamNames = teamNameCache.keySet().toArray(new String[0]);
        Arrays.sort(teamNames, (s1, s2) -> {
            if (s1.equals("Kein Team")) return -1;
            if (s2.equals("Kein Team")) return 1;
            return s1.compareTo(s2);
        });
        teamDropdown = new JComboBox<>(teamNames);
        teamDropdown.setSelectedItem("Kein Team"); // Standardauswahl
        gbc.gridx = 1; formPanel.add(teamDropdown, gbc);
        row++;
        // ********************************************************************

        // Qualifikationen (JSON-String)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Qualifikationen (JSON):"), gbc);
        gbc.gridx = 1; qualificationsField = new JTextField(20); formPanel.add(qualificationsField, gbc);
        row++;

        // Abgeschlossene Trainings (JSON-String)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Abgeschlossene Trainings (JSON):"), gbc);
        gbc.gridx = 1; completedTrainingsField = new JTextField(20); formPanel.add(completedTrainingsField, gbc);
        row++;

        // Manager ID (Optional)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Manager ID (Optional):"), gbc);
        gbc.gridx = 1; managerIdField = new JTextField(20); formPanel.add(managerIdField, gbc);
        row++;

        // Checkboxen
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkBoxPanel.setOpaque(false);
        itAdminCheckBox = new JCheckBox("IT-Admin");
        hrCheckBox = new JCheckBox("HR");
        hrHeadCheckBox = new JCheckBox("HR-Leiter");
        isManagerCheckBox = new JCheckBox("Manager");
        checkBoxPanel.add(itAdminCheckBox);
        checkBoxPanel.add(hrCheckBox);
        checkBoxPanel.add(hrHeadCheckBox);
        checkBoxPanel.add(isManagerCheckBox);
        formPanel.add(checkBoxPanel, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(this::saveEmployee);
        buttonPanel.add(saveButton);

        JButton backButton = new JButton("Zurueck");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void initializeCaches() {
        try {
            // Abteilungen
            List<Department> departments = (List<Department>) CompanyStructureManager.getInstance().getAllDepartments();
            for (Department dept : departments) {
                departmentNameCache.put(dept.getName(), dept.getDepartmentId());
                departmentIdCache.put(dept.getDepartmentId(), dept.getName());
            }

            // Rollen
           /**List<Role> roles = (List<Role>) CompanyStructureManager.getInstance().getAllRoles();
            for (Role role : roles) {
                roleNameCache.put(role.getName(), role.getRoleId());
                roleIdCache.put(role.getRoleId(), role.getName());
            }*/

            // Teams aus CompanyStructureManager laden
            try {
                // Annahme: CompanyStructureManager.getAllTeams() gibt List<Team> zurueck
                List<Team> teams = (List<Team>) CompanyStructureManager.getInstance().getAllTeams();
                if (teams != null && !teams.isEmpty()) { // Auch auf leere Liste pruefen
                    for (Team team : teams) {
                        teamNameCache.put(team.getName(), team.getTeamId());
                        teamIdCache.put(team.getTeamId(), team.getName());
                    }
                } else {
                    System.err.println("Warnung: CompanyStructureManager.getAllTeams() gab eine leere Liste zurueck. Verwende Fallback-Teams.");
                    addFallbackTeams();
                }
            } catch (NoSuchMethodError | NullPointerException e) {
                System.err.println("Warnung: CompanyStructureManager.getAllTeams() nicht gefunden oder fehlerhaft. Verwende Fallback-Teams.");
                addFallbackTeams();
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Initialisieren der Caches fuer Abteilungen/Rollen: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Strukturdaten: " + e.getMessage(),
                    "Datenfehler",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addFallbackTeams() {
        teamNameCache.put("Kein Team", null); // Explizit "Kein Team" mit null ID
        teamNameCache.put("Development Team", "DEV_TEAM");
        teamNameCache.put("Marketing Team", "MKT_TEAM");
        teamNameCache.put("Sales Team", "SAL_TEAM");
        teamIdCache.put("DEV_TEAM", "Development Team");
        teamIdCache.put("MKT_TEAM", "Marketing Team");
        teamIdCache.put("SAL_TEAM", "Sales Team");
    }


    private void saveEmployee(ActionEvent e) {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phoneNumber = phoneNumberField.getText().trim();
            String address = addressField.getText().trim();
            char gender = ((String) genderDropdown.getSelectedItem()).charAt(0);
            String employmentStatus = (String) employmentStatusDropdown.getSelectedItem();

            String departmentId = departmentNameCache.get(departmentDropdown.getSelectedItem());
            String roleId = roleNameCache.get(roleDropdown.getSelectedItem());
            String teamId = null;
            if (teamDropdown.getSelectedItem() != null && !"Kein Team".equals(teamDropdown.getSelectedItem())) {
                teamId = teamNameCache.get(teamDropdown.getSelectedItem());
            } else if (teamDropdown.getSelectedItem() != null && "Kein Team".equals(teamDropdown.getSelectedItem())) {
                teamId = null; // Setze teamId explizit auf null, wenn "Kein Team" ausgewählt ist
            }


            LocalDate dateOfBirth = null;
            if (!dateOfBirthField.getText().trim().isEmpty()) {
                try {
                    dateOfBirth = LocalDate.parse(dateOfBirthField.getText().trim());
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ungueltiges Geburtsdatum-Format. Bitte YYYY-MM-DD verwenden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            LocalDate hireDate = null;
            if (!hireDateField.getText().trim().isEmpty()) {
                try {
                    hireDate = LocalDate.parse(hireDateField.getText().trim());
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ungueltiges Einstellungsdatum-Format. Bitte YYYY-MM-DD verwenden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String qualifications = qualificationsField.getText().trim();
            String completedTrainings = completedTrainingsField.getText().trim();

            Integer managerId = null;
            if (!managerIdField.getText().trim().isEmpty()) {
                try {
                    managerId = Integer.parseInt(managerIdField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ungueltiges Manager ID-Format. Bitte eine Zahl eingeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            boolean itAdmin = itAdminCheckBox.isSelected();
            boolean hr = hrCheckBox.isSelected();
            boolean hrHead = hrHeadCheckBox.isSelected();
            boolean isManager = isManagerCheckBox.isSelected();

            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || departmentId == null || roleId == null) {
                JOptionPane.showMessageDialog(this, "Bitte alle Pflichtfelder ausfuellen (Benutzername, Passwort, Vorname, Nachname, E-Mail, Abteilung, Rolle).", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            employeeManager.addEmployee(
                    username, password, "default", firstName, lastName, email, phoneNumber,
                    dateOfBirth, address, gender, hireDate, employmentStatus, departmentId,
                    teamId, roleId, qualifications, completedTrainings, managerId,
                    itAdmin, hr, hrHead, isManager
            );

            JOptionPane.showMessageDialog(this, "Mitarbeiter erfolgreich hinzugefuegt!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            eventManager.callEvent("moveBackView", null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Hinzufuegen des Mitarbeiters: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneNumberField.setText("");
        dateOfBirthField.setText("");
        addressField.setText("");
        genderDropdown.setSelectedIndex(0);
        hireDateField.setText("");
        employmentStatusDropdown.setSelectedIndex(0);
        departmentDropdown.setSelectedIndex(0);
        roleDropdown.setSelectedIndex(0);
        teamDropdown.setSelectedItem("Kein Team");
        qualificationsField.setText("");
        completedTrainingsField.setText("");
        managerIdField.setText("");
        itAdminCheckBox.setSelected(false);
        hrCheckBox.setSelected(false);
        hrHeadCheckBox.setSelected(false);
        isManagerCheckBox.setSelected(false);
    }
}
