package gui.views;

import core.EmployeeManager;
import core.EventManager;
import model.json.Department;
import model.json.Role;
import model.json.Team;
import core.CompanyStructureManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat; // Neu hinzugefügt für Datumshandhabung
import java.text.ParseException; // Neu hinzugefügt für Datumshandhabung
import java.util.List;

/**
 * Die `AddEmployeeView` ist eine grafische Benutzeroberfläche zur Erfassung und
 * Speicherung neuer Mitarbeiterdatensätze. Sie bietet ein Formular mit
 * verschiedenen Eingabefeldern wie persönlichen Daten, Kontaktdaten,
 * Unternehmensstruktur-Informationen (Abteilung, Rolle, Team) und
 * Berechtigungen. Die View interagiert mit dem `EmployeeManager`, um
 * die Daten zu verarbeiten und an die Datenbank zu senden.
 *
 * @author joshuasperber
 * @version 1.0
 * @since 2025-07-28
 */

public class AddEmployeeView extends View {

    private final EmployeeManager employeeManager;
    private final EventManager eventManager;

    // UI-Komponenten
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField firstNameField;
    private final  JTextField lastNameField;
    private final  JTextField emailField;
    private final  JTextField phoneNumberField;
    private final  JTextField dateOfBirthField; // Format YYYY-MM-DD
    private final  JTextField addressField;
    private final  JComboBox<String> genderDropdown;
    private final  JTextField hireDateField; // Format YYYY-MM-DD
    private final  JComboBox<String> employmentStatusDropdown;
    private final  JComboBox<String> departmentDropdown;
    private final  JComboBox<String> teamDropdown;
    private final  JComboBox<String> roleDropdown;
    private final  JTextField qualificationsField;
    private final  JTextField completedTrainingsField;
    private final  JTextField managerIdField;
    private final  JCheckBox itAdminCheckBox;
    private final  JCheckBox hrCheckBox;
    private final  JCheckBox hrHeadCheckBox;
    private final  JCheckBox isManagerCheckBox;

    // Caches fuer Dropdowns
    private final Map<String, String> departmentNameCache = new HashMap<>();
    private final  Map<String, String> departmentIdCache = new HashMap<>();
    private final  Map<String, String> roleNameCache = new HashMap<>();
    private final  Map<String, String> roleIdCache = new HashMap<>();
    private final  Map<String, String> teamNameCache = new HashMap<>();
    private final  Map<String, String> teamIdCache = new HashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Konstruktor für die AddEmployeeView.
     * Erstellt die grafische Oberfläche und initialisiert alle Komponenten.
     *
     * @param employeeManager Eine Instanz des EmployeeManager zur Verwaltung der Mitarbeiterdaten.
     * @param eventManager    Eine Instanz des EventManager zur Kommunikation mit anderen Views.
     * @author Joshua Sperber
     */
    public AddEmployeeView(EmployeeManager employeeManager, EventManager eventManager) {
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        setView_id("view-add-employee");
        setView_name("Mitarbeiter hinzufuegen");

        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("icons/Hintergrundbild.png")));
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

        JLabel titleLabel = new JLabel("Neuen Mitarbeiter hinzufügen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

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

        // Caches zuerst initialisieren, damit die Dropdowns danach gefüllt werden können
        initializeCaches();

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Benutzername:"), gbc);
        gbc.gridx = 1; usernameField = new JTextField(20); formPanel.add(usernameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Passwort:"), gbc);
        gbc.gridx = 1; passwordField = new JPasswordField(20); formPanel.add(passwordField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Vorname:"), gbc);
        gbc.gridx = 1; firstNameField = new JTextField(20); formPanel.add(firstNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Nachname:"), gbc);
        gbc.gridx = 1; lastNameField = new JTextField(20); formPanel.add(lastNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("E-Mail:"), gbc);
        gbc.gridx = 1; emailField = new JTextField(20); formPanel.add(emailField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Telefonnummer:"), gbc);
        gbc.gridx = 1; phoneNumberField = new JTextField(20); formPanel.add(phoneNumberField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Geburtsdatum (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; dateOfBirthField = new JTextField(20); formPanel.add(dateOfBirthField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1; addressField = new JTextField(20); formPanel.add(addressField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Geschlecht:"), gbc);
        gbc.gridx = 1; genderDropdown = new JComboBox<>(new String[]{"M", "F", "D"}); formPanel.add(genderDropdown, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Einstellungsdatum (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; hireDateField = new JTextField(20); formPanel.add(hireDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Beschaeftigungsstatus:"), gbc);
        gbc.gridx = 1; employmentStatusDropdown = new JComboBox<>(new String[]{"Active", "On Leave", "Terminated", "Probation", "Retired"}); formPanel.add(employmentStatusDropdown, gbc);
        row++;

        // Drop-down Menüs nach dem Laden der Daten initialisieren
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Abteilung:"), gbc);
        departmentDropdown = new JComboBox<>(departmentNameCache.keySet().toArray(new String[0]));
        gbc.gridx = 1; formPanel.add(departmentDropdown, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Rolle:"), gbc);
        roleDropdown = new JComboBox<>(roleNameCache.keySet().toArray(new String[0]));
        gbc.gridx = 1; formPanel.add(roleDropdown, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Team (Optional):"), gbc);
        String[] teamNames = teamNameCache.keySet().toArray(new String[0]);
        Arrays.sort(teamNames, (s1, s2) -> {
            if (s1.equals("Kein Team")) return -1;
            if (s2.equals("Kein Team")) return 1;
            return s1.compareTo(s2);
        });
        teamDropdown = new JComboBox<>(teamNames);
        teamDropdown.setSelectedItem("Kein Team");
        gbc.gridx = 1; formPanel.add(teamDropdown, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Qualifikationen (JSON):"), gbc);
        gbc.gridx = 1; qualificationsField = new JTextField(20); formPanel.add(qualificationsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Abgeschlossene Trainings (JSON):"), gbc);
        gbc.gridx = 1; completedTrainingsField = new JTextField(20); formPanel.add(completedTrainingsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Manager ID (Optional):"), gbc);
        gbc.gridx = 1; managerIdField = new JTextField(20); formPanel.add(managerIdField, gbc);
        row++;

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(this::saveEmployee);
        buttonPanel.add(saveButton);

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Initialisiert die internen Caches für Abteilungen, Rollen und Teams, indem die
     * Daten aus dem `CompanyStructureManager` geladen werden.
     * Bei Fehlern werden Fallback-Daten verwendet, um die Dropdown-Menüs zu befüllen.
     * @author Joshua Sperber
     */
    private void initializeCaches() {
        // Abteilungen
        try {
            // Nicht direkt zu List casten, sondern über das Iterable-Interface iterieren
            for (Object obj : CompanyStructureManager.getInstance().getAllDepartments()) {
                if (obj instanceof Department) {
                    Department dept = (Department) obj;
                    departmentNameCache.put(dept.getName(), dept.getDepartmentId());
                    departmentIdCache.put(dept.getDepartmentId(), dept.getName());
                } else if (obj instanceof Map) {
                    Map<String, Object> dept = (Map<String, Object>) obj;
                    String id = (String) dept.get("departmentId");
                    String name = (String) dept.get("name");
                    if (id != null && name != null) {
                        departmentNameCache.put(name, id);
                        departmentIdCache.put(id, name);
                    }
                }
            }
            if (departmentNameCache.isEmpty()) {
                System.err.println("Warnung: CompanyStructureManager.getAllDepartments() gab eine leere Liste zurueck. Verwende Fallback-Abteilungen.");
                addFallbackDepartments();
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Abteilungen: " + e.getMessage());
            addFallbackDepartments();
        }


        // Rollen
        try {
            // Nicht direkt zu List casten, sondern über das Iterable-Interface iterieren
            for (Object obj : CompanyStructureManager.getInstance().getAllRoles()) {
                if (obj instanceof Role) {
                    Role role = (Role) obj;
                    roleNameCache.put(role.getName(), role.getroleId());
                    roleIdCache.put(role.getroleId(), role.getName());
                } else if (obj instanceof Map) {
                    Map<String, Object> role = (Map<String, Object>) obj;
                    String id = (String) role.get("roleId");
                    String name = (String) role.get("name");
                    if (id != null && name != null) {
                        roleNameCache.put(name, id);
                        roleIdCache.put(id, name);
                    }
                }
            }
            if (roleNameCache.isEmpty()) {
                System.err.println("Warnung: CompanyStructureManager.getAllRoles() gab eine leere Liste zurueck. Verwende Fallback-Rollen.");
                addFallbackRoles();
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Rollen: " + e.getMessage());
            addFallbackRoles();
        }

        // Teams
        try {
            // Nicht direkt zu List casten, sondern über das Iterable-Interface iterieren
            for (Object obj : CompanyStructureManager.getInstance().getAllTeams()) {
                if (obj instanceof Team) {
                    Team team = (Team) obj;
                    teamNameCache.put(team.getName(), team.getTeamId());
                    teamIdCache.put(team.getTeamId(), team.getName());
                } else if (obj instanceof Map) {
                    Map<String, Object> team = (Map<String, Object>) obj;
                    String id = (String) team.get("teamId");
                    String name = (String) team.get("name");
                    if (id != null && name != null) {
                        teamNameCache.put(name, id);
                        teamIdCache.put(id, name);
                    }
                }
            }
            if (teamNameCache.isEmpty()) {
                System.err.println("Warnung: CompanyStructureManager.getAllTeams() gab eine leere Liste zurueck. Verwende Fallback-Teams.");
                addFallbackTeams();
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Teams: " + e.getMessage());
            addFallbackTeams();
        }

    }
    // ... (restliche Methoden bleiben gleich)
    /**
     * Fügt Fallback-Abteilungsdaten in den Cache ein, falls die Daten
     * aus dem `CompanyStructureManager` nicht geladen werden können.
     * @author Joshua Sperber
     */
    private void addFallbackDepartments() {
        departmentNameCache.put("Unbekannt", "UNKNOWN_DEP");
        departmentIdCache.put("UNKNOWN_DEP", "Unbekannt");
    }

    /**
     * Fügt Fallback-Rollendaten in den Cache ein, falls die Daten
     * aus dem `CompanyStructureManager` nicht geladen werden können.
     * @author Joshua Sperber
     */
    private void addFallbackRoles() {
        roleNameCache.put("Mitarbeiter", "EMPLOYEE_ROLE");
        roleIdCache.put("EMPLOYEE_ROLE", "Mitarbeiter");
    }

    /**
     * Fügt Fallback-Teamdaten in den Cache ein, falls die Daten
     * aus dem `CompanyStructureManager` nicht geladen werden können.
     * @author Joshua Sperber
     */
    private void addFallbackTeams() {
        teamNameCache.put("Kein Team", null);
        teamNameCache.put("Development Team", "DEV_TEAM");
        teamNameCache.put("Marketing Team", "MKT_TEAM");
        teamNameCache.put("Sales Team", "SAL_TEAM");
        teamIdCache.put("DEV_TEAM", "Development Team");
        teamIdCache.put("MKT_TEAM", "Marketing Team");
        teamIdCache.put("SAL_TEAM", "Sales Team");
    }


    /**
     * Behandelt den Klick auf den Speichern-Button.
     * Sammelt die eingegebenen Daten, validiert sie und ruft die
     * `addEmployee`-Methode des `EmployeeManager` auf, um den neuen
     * Mitarbeiter in der Datenbank zu speichern.
     * Bei Erfolg wird eine Bestätigungsmeldung angezeigt und das Formular zurückgesetzt.
     * Bei Fehlern werden entsprechende Fehlermeldungen angezeigt.
     *
     * @param e Das `ActionEvent`, ausgelöst durch den Klick auf den Button.
     * @author Joshua Sperber
     */
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
                teamId = null;
            }


            Date dateOfBirth = null;
            if (!dateOfBirthField.getText().trim().isEmpty()) {
                try {
                    dateOfBirth = dateFormat.parse(dateOfBirthField.getText().trim());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ungueltiges Geburtsdatum-Format. Bitte YYYY-MM-DD verwenden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Date hireDate = null;
            if (!hireDateField.getText().trim().isEmpty()) {
                try {
                    hireDate = dateFormat.parse(hireDateField.getText().trim());
                } catch (ParseException ex) {
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

    /**
     * Setzt alle Eingabefelder und Dropdowns des Formulars auf ihre
     * Standardwerte zurück.
     * @author Joshua Sperber
     */
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