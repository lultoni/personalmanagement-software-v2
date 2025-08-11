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


/**
 * Die `EditEmployeeView` bietet eine Benutzeroberfläche zum Anzeigen,
 * Bearbeiten und Löschen von Mitarbeiterinformationen. Sie zeigt
 * Details zu einem ausgewählten Mitarbeiter an und ermöglicht es
 * autorisierten Nutzern (HR, HR-Leiter, IT-Admin), die Daten zu ändern
 * oder den Mitarbeiter zu entfernen. Die Ansicht verwendet den
 * `EmployeeManager`, um Änderungen zu speichern und Löschvorgänge
 * durchzuführen.
 *
 * @author joshuasperber
 * @version 1.0
 * @since 2025-07-27
 */


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

    /**
     * Konstruktor für die EditEmployeeView.
     * Erstellt die Benutzeroberfläche zur Bearbeitung der Mitarbeiterdaten.
     *
     * @param currentUser       Der aktuell eingeloggte Benutzer, dessen Berechtigungen
     * die Sichtbarkeit von Feldern beeinflussen könnten.
     * @param employeeToEdit    Der `Employee`, dessen Daten bearbeitet werden sollen.
     * @param employeeManager   Eine Instanz des `EmployeeManager` zur Verwaltung der Mitarbeiter.
     * @param eventManager      Eine Instanz des `EventManager` zur Navigation zwischen den Views.
     * @author joshuasperber
     */
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

    /**
     * Initialisiert die internen Caches für Abteilungen, Rollen und Teams,
     * indem Daten aus dem `CompanyStructureManager` geladen werden.
     * Dies dient dazu, Dropdown-Menüs mit den korrekten Namen und IDs zu befüllen.
     * @author joshuasperber
     */
    private void initializeCaches() {
        try {
            // Abteilungen
            // Lade alle Abteilungen und speichere sie in zwei Maps (ID zu Name und Name zu ID).
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
            // Lade alle Rollen und speichere sie in zwei Maps.
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
            // Lade alle Teams und speichere sie in zwei Maps.
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

    /**
     * Erstellt das grundlegende Layout und die Komponenten der View,
     * einschließlich des Titels, der Datentabelle und der Schaltflächen.
     * @author Joshua Sperber
     */
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

        // Zeige die Daten beim Start der View an
        refreshDataDisplay();
    }

    /**
     * Aktualisiert die Anzeige der Mitarbeiterdaten. Löscht alle
     * alten Komponenten und erstellt sie neu, basierend auf dem
     * aktuellen Bearbeitungsmodus.
     * @author joshuasperber
     */
    private void refreshDataDisplay() {
        dataPanel.removeAll();
        editFields.clear();

        List<String> visibleFields = getVisibleFieldsForUser();
        JPanel gridPanel = createDataGrid(visibleFields);

        dataPanel.add(gridPanel);
        revalidate();
        repaint();
    }

    /**
     * Erstellt ein `JPanel` mit einem `GridBagLayout`, um die
     * Mitarbeiterdaten in einem übersichtlichen Raster anzuzeigen.
     *
     * @param fields Eine Liste von Feldnamen, die angezeigt werden sollen.
     * @return Ein `JPanel`, das die Daten anzeigt.
     * @author joshuasperber
     */
    private JPanel createDataGrid(List<String> fields) {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        // Erstelle für jedes Feld ein Label und eine Komponente
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

    /**
     * Erstellt und gibt ein `JLabel` für den Feldnamen zurück.
     *
     * @param field Der interne Name des Feldes.
     * @return Ein formatiertes `JLabel`.
     * @author joshuasperber
     */
    private JLabel createFieldLabel(String field) {
        JLabel label = new JLabel(getFieldLabel(field));
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.BLACK);
        return label;
    }

    /**
     * Erstellt entweder ein bearbeitbares Eingabefeld (wenn im Bearbeitungsmodus)
     * oder eine schreibgeschützte `JLabel`-Komponente für den Feldwert.
     *
     * @param field Der interne Name des Feldes.
     * @return Eine `JComponent` (entweder `JTextField`, `JComboBox` oder `JLabel`).
     * @author joshuasperber
     */
    private JComponent createFieldComponent(String field) {
        // Prüfe, ob die View im Bearbeitungsmodus ist und das Feld editierbar ist.
        if (editMode && isFieldEditable(field)) {
            return createEditableField(field);
        }
        // Ansonsten erstelle eine schreibgeschützte Anzeige.
        return createDisplayField(field);
    }

    /**
     * Erstellt und gibt ein bearbeitbares Eingabefeld (z.B. `JTextField` oder `JComboBox`)
     * für das angegebene Feld zurück.
     *
     * @param field Der interne Name des Feldes.
     * @return Eine bearbeitbare `JComponent`.
     * @author joshuasperber
     */
    private JComponent createEditableField(String field) {
        switch (field) {
            case "gender":
                // Dropdown für das Geschlecht
                JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"M", "F", "D"});
                genderComboBox.setSelectedItem(String.valueOf(employeeToEdit.getGender()));
                editFields.put(field, genderComboBox);
                return genderComboBox;
            case "departmentId":
                // Dropdown für die Abteilung, befüllt aus dem Cache
                JComboBox<String> departmentComboBox = new JComboBox<>(departmentNameToIdCache.keySet().toArray(new String[0]));
                departmentComboBox.setSelectedItem(departmentIdToNameCache.getOrDefault(employeeToEdit.getDepartmentId(), employeeToEdit.getDepartmentId()));
                editFields.put(field, departmentComboBox);
                return departmentComboBox;
            case "roleId":
                // Dropdown für die Rolle, befüllt aus dem Cache
                JComboBox<String> roleComboBox = new JComboBox<>(roleNameToIdCache.keySet().toArray(new String[0]));
                roleComboBox.setSelectedItem(roleIdToNameCache.getOrDefault(employeeToEdit.getRoleId(), employeeToEdit.getRoleId()));
                editFields.put(field, roleComboBox);
                return roleComboBox;
            case "teamId":
                // Dropdown für das Team, befüllt aus dem Cache und sortiert
                String[] teamNames = teamNameToIdCache.keySet().toArray(new String[0]);
                Arrays.sort(teamNames);
                JComboBox<String> teamComboBox = new JComboBox<>(teamNames);
                teamComboBox.setSelectedItem(teamIdToNameCache.getOrDefault(employeeToEdit.getTeamId(), ""));
                editFields.put(field, teamComboBox);
                return teamComboBox;
            default:
                // Standard-Textfeld für alle anderen editierbaren Felder
                JTextField textField = new JTextField(getFieldValue(field), 20);
                editFields.put(field, textField);
                return textField;
        }
    }

    /**
     * Erstellt und gibt eine nicht bearbeitbare `JLabel`-Komponente
     * zurück, die den Wert eines Feldes anzeigt.
     *
     * @param field Der interne Name des Feldes.
     * @return Eine schreibgeschützte `JLabel` für den Feldwert.
     * @author joshuasperber
     */
    private JComponent createDisplayField(String field) {
        JLabel label = new JLabel(getFieldValue(field));
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    /**
     * Erstellt das Panel, das die Schaltflächen "Bearbeiten", "Löschen" und "Zurück" enthält.
     * @return Das Panel mit den Buttons.
     * @author joshuasperber
     */
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

    /**
     * Schaltet zwischen dem Anzeige- und dem Bearbeitungsmodus um.
     * Beim Umschalten in den Anzeigemodus werden die Änderungen gespeichert.
     * @param e Das `ActionEvent`, ausgelöst durch den Klick auf den Button.
     * @author joshuasperber
     */
    private void toggleEditMode(ActionEvent e) {
        // Wenn bereits im Bearbeitungsmodus, versuche die Änderungen zu speichern
        if (editMode) {
            if (!saveChanges()) {
                return;
            }
        }
        // Umschalten des Modus und Aktualisieren des Button-Textes
        editMode = !editMode;
        JButton button = (JButton) e.getSource();
        button.setText(editMode ? "Speichern" : "Bearbeiten");
        // Aktualisiere die Anzeige der Felder
        refreshDataDisplay();
    }

    /**
     * Speichert die Änderungen, die im Bearbeitungsmodus vorgenommen wurden,
     * indem die `updateEmployee`-Methode des `EmployeeManager` aufgerufen wird.
     *
     * @return `true`, wenn die Änderungen erfolgreich gespeichert wurden, sonst `false`.
     * @author joshuasperber
     */
    private boolean saveChanges() {
        try {
            // Übertrage die Werte aus den Eingabefeldern in das Mitarbeiterobjekt
            updateEmployeeFromFields();
            // Rufe den Manager auf, um die Daten zu aktualisieren
            employeeManager.updateEmployee(employeeToEdit);
            JOptionPane.showMessageDialog(this, "Änderungen erfolgreich gespeichert", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Aktualisiert die Mitarbeiterdaten des `employeeToEdit`-Objekts
     * mit den Werten aus den Eingabefeldern.
     * @author joshuasperber
     */
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

    /**
     * Hilfsmethode zum Aktualisieren eines bestimmten Feldes des `Employee`-Objekts.
     *
     * @param field Der Name des zu aktualisierenden Feldes.
     * @param value Der neue Wert für das Feld.
     * @author joshuasperber
     */
    private void updateEmployeeField(String field, String value) {
        switch (field) {
            // Persönliche Daten
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
            // Datumsfelder, die eine Parsen-Logik benötigen
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
            // Unternehmensstrukturfelder
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

    /**
     * Gibt den vollständigen Namen eines Mitarbeiters zurück.
     *
     * @param emp Das `Employee`-Objekt.
     * @return Der vollständige Name als `String`.
     * @author joshuasperber
     */
    private String getFullName(Employee emp) {
        return emp.getFirstName() + " " + emp.getLastName();
    }

    /**
     * Gibt eine Liste von Feldern zurück, die für den eingeloggten Benutzer
     * in dieser View sichtbar sein sollen.
     *
     * @return Eine `List` von `String`-Feldnamen.
     * @author joshuasperber
     */
    private List<String> getVisibleFieldsForUser() {
        return new ArrayList<>(new LinkedHashSet<>(Arrays.asList(
                "firstName", "lastName", "email", "phoneNumber", "dateOfBirth", "address", "gender",
                "departmentId", "roleId", "teamId", "username", "hireDate"
        )));
    }

    /**
     * Gibt die lokalisierte Beschriftung (Label) für ein bestimmtes Feld zurück.
     *
     * @param field Der interne Name des Feldes.
     * @return Die zugehörige Beschriftung als `String`.
     * @author joshuasperber
     */
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
        return labels.getOrDefault(field, field + ":");
    }

    /**
     * Gibt den Wert eines Feldes aus dem `employeeToEdit`-Objekt als `String` zurück.
     *
     * @param field Der interne Name des Feldes.
     * @return Der Wert des Feldes als `String`.
     * @author joshuasperber
     */
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
            default:
                return "";
        }
    }

    /**
     * Überprüft, ob ein bestimmtes Feld bearbeitet werden kann.
     * @param field Der Name des Feldes.
     * @return `true`, wenn das Feld bearbeitbar ist, sonst `false`.
     * @author joshuasperber
     */
    private boolean isFieldEditable(String field) {
        Set<String> nonEditable = new HashSet<>(Arrays.asList("username"));
        return !nonEditable.contains(field);
    }

    /**
     * Behandelt den Klick auf den "Mitarbeiter löschen"-Button.
     * Zeigt eine Bestätigungsnachricht an und führt bei Zustimmung
     * die Löschung des Mitarbeiters über den `EmployeeManager` durch.
     *
     * @param e Das `ActionEvent`, ausgelöst durch den Klick auf den Button.
     * @author joshuasperber
     */
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
                // Lösche den Mitarbeiter über den EmployeeManager
                employeeManager.removeEmployee(employeeToEdit.getId());
                JOptionPane.showMessageDialog(this, "Mitarbeiter wurde gelöscht.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                // Navigiere zur vorherigen Ansicht zurück
                eventManager.callEvent("moveBackView", null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Löschen: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}