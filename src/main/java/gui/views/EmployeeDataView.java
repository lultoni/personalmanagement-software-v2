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


/**
 * Die `EmployeeDataView` zeigt die detaillierten Informationen eines Mitarbeiters an.
 * Je nach Berechtigungen des eingeloggten Benutzers können die Daten eingesehen,
 * bearbeitet oder als schreibgeschützte Felder angezeigt werden. Sie interagiert
 * mit dem `EmployeeManager`, um Änderungen zu speichern und die Datenstruktur
 * über den `CompanyStructureManager` zu laden.
 *
 * @author joshuasperber
 * @version 1.3
 * @since 2025-07-28
 */


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

    /**
     * Konstruktor für die EmployeeDataView.
     * Initialisiert die View mit den notwendigen Managern und Mitarbeiterobjekten.
     *
     * @param loggedInUser Der aktuell eingeloggte Benutzer.
     * @param employee Der Mitarbeiter, dessen Daten angezeigt werden sollen.
     * @param employeeManager Der Manager für die Mitarbeiterdaten.
     * @param eventManager Der Manager für die View-Events.
     * @author joshuasperber
     */
    public EmployeeDataView(Employee loggedInUser, Employee employee,
                            EmployeeManager employeeManager, EventManager eventManager) {
        this.loggedInUser = loggedInUser;
        this.employee = employee;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        // Lade das Hintergrundbild für die View
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden für EmployeeDataView.", e);
        }

        // Erstelle ein Panel, das das Hintergrundbild zeichnet
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Setze die Transparenz des Bildes
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false);

        // Füge das Hintergrundpanel zur Haupt-View hinzu
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Initialisiere die Caches für die Dropdowns
        initializeCaches();
        // Baue die Benutzeroberfläche auf
        setupUI(backgroundPanel);
    }

    /**
     * Initialisiert die internen Caches für Abteilungen, Rollen und Teams
     * aus dem `CompanyStructureManager`. Dies sorgt für eine schnelle
     * Zuordnung von IDs zu Namen und umgekehrt.
     * @author joshuasperber
     */
    private void initializeCaches() {
        try {
            // Lade Abteilungen und fülle die Caches
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

            // Lade Rollen und fülle die Caches
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

            // Lade Teams und fülle die Caches
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


    /**
     * Baut die Hauptkomponenten der Benutzeroberfläche zusammen und fügt sie
     * dem übergeordneten Container hinzu. Dazu gehören Titel, Daten-Panel und Buttons.
     * @param parentContainer Der Container, in dem die UI-Komponenten platziert werden.
     * @author joshuasperber
     */
    private void setupUI(JPanel parentContainer) {
        setView_id("view-employee");
        setView_name("Mitarbeiter: " + getFullName(employee));

        // Hauptpanel für den Inhalt
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titel-Label mit dem vollständigen Namen des Mitarbeiters
        JLabel titleLabel = new JLabel(getFullName(employee), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        mainContentPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel für die Mitarbeiterdaten-Felder
        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dataPanel.setOpaque(false);

        // ScrollPane, falls die Datenliste zu lang wird
        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // Füge das Button-Panel hinzu
        JPanel buttonPanel = setupButtonPanel();
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        parentContainer.add(mainContentPanel, BorderLayout.CENTER);

        // Erstelle die anfängliche Anzeige der Daten
        refreshDataDisplay();
    }

    /**
     * Erstellt das Panel, das die "Bearbeiten" und "Zurück"-Buttons enthält.
     * Der "Bearbeiten"-Button wird nur angezeigt, wenn der eingeloggte Nutzer
     * die entsprechenden Berechtigungen hat.
     * @return Das Panel mit den Buttons.
     * @author joshuasperber
     */
    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        // Zeige den Bearbeiten-Button nur, wenn der Benutzer berechtigt ist
        if (canEditData()) {
            JButton editButton = new JButton("Bearbeiten");
            editButton.addActionListener(this::toggleEditMode);
            buttonPanel.add(editButton);
        }

        // Der Zurück-Button ist immer vorhanden
        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    /**
     * Aktualisiert die Anzeige der Mitarbeiterdaten. Entfernt alte Komponenten,
     * leert den Bearbeitungs-Cache und baut die Felder neu auf, je nach
     * aktuellem Bearbeitungsmodus.
     * @author joshuasperber
     */
    private void refreshDataDisplay() {
        dataPanel.removeAll();
        editFields.clear();

        // Bestimme, welche Felder sichtbar sein sollen
        List<String> visibleFields = getVisibleFieldsForUser();
        // Erstelle das Datenraster mit den Feldern
        JPanel gridPanel = createDataGrid(visibleFields);

        dataPanel.add(gridPanel);
        // Aktualisiere die Anzeige
        revalidate();
        repaint();
    }

    /**
     * Erstellt ein `JPanel` im `GridBagLayout`, um eine übersichtliche
     * zweispaltige Darstellung von Feld-Labels und Feld-Komponenten zu erreichen.
     * @param fields Eine Liste der anzuzeigenden Feldnamen.
     * @return Das fertiggestellte `JPanel`.
     * @author joshuasperber
     */
    private JPanel createDataGrid(List<String> fields) {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        // Iteriere über die Felder und erstelle für jedes ein Label und eine Komponente
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
     * Erstellt ein beschriftendes `JLabel` für ein Datenfeld.
     * @param field Der interne Name des Feldes.
     * @return Das Label-Objekt.
     * @author joshuasperber
     */
    private JLabel createFieldLabel(String field) {
        JLabel label = new JLabel(getFieldLabel(field));
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.BLACK);
        return label;
    }

    /**
     * Erstellt entweder ein bearbeitbares Eingabefeld oder ein schreibgeschütztes
     * `JLabel` für ein Datenfeld, abhängig vom aktuellen Modus und den Berechtigungen.
     * @param field Der interne Name des Feldes.
     * @return Die JComponent, die das Feld darstellt.
     * @author joshuasperber
     */
    private JComponent createFieldComponent(String field) {
        // Wenn im Bearbeitungsmodus und das Feld editierbar ist, erstelle ein editierbares Feld.
        if (editMode && isFieldEditable(field)) {
            return createEditableField(field);
        }
        // Ansonsten erstelle ein schreibgeschütztes Anzeigefeld.
        return createDisplayField(field);
    }

    /**
     * Erstellt und gibt eine bearbeitbare Komponente (z. B. `JTextField`, `JComboBox`)
     * für das angegebene Feld zurück.
     * @param field Der interne Name des Feldes.
     * @return Die bearbeitbare JComponent.
     * @author joshuasperber
     */
    private JComponent createEditableField(String field) {
        switch (field) {
            case "gender":
                // Dropdown-Menü für Geschlecht
                JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"M", "F", "D"});
                genderComboBox.setSelectedItem(String.valueOf(employee.getGender()));
                editFields.put(field, genderComboBox);
                return genderComboBox;
            case "departmentId":
                // Dropdown für Abteilung, befüllt aus dem Cache
                JComboBox<String> departmentComboBox = new JComboBox<>(departmentNameToIdCache.keySet().toArray(new String[0]));
                departmentComboBox.setSelectedItem(departmentIdToNameCache.getOrDefault(employee.getDepartmentId(), employee.getDepartmentId()));
                editFields.put(field, departmentComboBox);
                return departmentComboBox;
            case "roleId":
                // Dropdown für Rolle, befüllt aus dem Cache
                JComboBox<String> roleComboBox = new JComboBox<>(roleNameToIdCache.keySet().toArray(new String[0]));
                roleComboBox.setSelectedItem(roleIdToNameCache.getOrDefault(employee.getRoleId(), employee.getRoleId()));
                editFields.put(field, roleComboBox);
                return roleComboBox;
            case "teamId":
                // Dropdown für Team, befüllt aus dem Cache
                String[] teamNames = teamNameToIdCache.keySet().toArray(new String[0]);
                Arrays.sort(teamNames);
                JComboBox<String> teamComboBox = new JComboBox<>(teamNames);
                teamComboBox.setSelectedItem(teamIdToNameCache.getOrDefault(employee.getTeamId(), ""));
                editFields.put(field, teamComboBox);
                return teamComboBox;
            default:
                // Standard-Textfeld für alle anderen Felder
                JTextField textField = new JTextField(getFieldValue(field), 20);
                editFields.put(field, textField);
                return textField;
        }
    }

    /**
     * Erstellt ein schreibgeschütztes `JLabel`, das den aktuellen Wert eines Feldes anzeigt.
     * @param field Der interne Name des Feldes.
     * @return Das JLabel-Objekt.
     * @author joshuasperber
     */
    private JComponent createDisplayField(String field) {
        JLabel label = new JLabel(getFieldValue(field));
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    /**
     * Schaltet den Bearbeitungsmodus der View um. Beim Wechsel von Bearbeiten zu
     * Anzeigen werden die Änderungen gespeichert.
     * @param e Das ActionEvent, das den Button-Klick repräsentiert.
     * @author joshuasperber
     */
    private void toggleEditMode(ActionEvent e) {
        // Speichere Änderungen, wenn der Bearbeitungsmodus beendet wird
        if (editMode) {
            if (!saveChanges()) {
                return;
            }
        }
        // Wechsle den Modus und aktualisiere den Button-Text
        editMode = !editMode;
        JButton button = (JButton) e.getSource();
        button.setText(editMode ? "Speichern" : "Bearbeiten");
        // Lade die Anzeige neu, um die Felder zu aktualisieren
        refreshDataDisplay();
    }

    /**
     * Speichert die vorgenommenen Änderungen an den Mitarbeiterdaten.
     * @return true, wenn das Speichern erfolgreich war, andernfalls false.
     * @author joshuasperber
     */
    private boolean saveChanges() {
        try {
            // Aktualisiere das Employee-Objekt mit den Werten aus den Feldern
            updateEmployeeFromFields();
            // Rufe den EmployeeManager auf, um die Daten in der Datenbank zu aktualisieren
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

    /**
     * Iteriert durch die bearbeitbaren Felder und überträgt die Werte
     * der Komponenten in das `employee`-Objekt.
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
     * Aktualisiert ein spezifisches Feld des `employee`-Objekts basierend
     * auf dem Feldnamen und dem neuen Wert.
     * @param field Der Name des zu aktualisierenden Feldes.
     * @param value Der neue Wert.
     * @author joshuasperber
     */
    private void updateEmployeeField(String field, String value) {
        switch (field) {
            case "firstName" -> employee.setFirstName(value);
            case "lastName" -> employee.setLastName(value);
            case "email" -> employee.setEmail(value);
            case "phoneNumber" -> employee.setPhoneNumber(value);
            case "address" -> employee.setAddress(value);
            case "gender" -> employee.setGender(value.charAt(0));
            case "dateOfBirth" -> {
                // Konvertiere den String-Wert in ein Datum
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
                // Konvertiere den String-Wert in ein Datum
                try {
                    employee.setHireDate(dateFormat.parse(value));
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Ungültiges Datumsformat für Einstellungsdatum. Bitte verwenden Sie YYYY-MM-DD.",
                            "Fehler beim Datum",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            // Aktualisiere die ID basierend auf dem ausgewählten Namen aus dem Cache
            case "departmentId" -> employee.setDepartmentId(departmentNameToIdCache.get(value));
            case "roleId" -> employee.setRoleId(roleNameToIdCache.get(value));
            case "teamId" -> employee.setTeamId(teamNameToIdCache.get(value));
        }
    }

    /**
     * Hilfsmethode, die den vollständigen Namen eines Mitarbeiters zurückgibt.
     * @param emp Das Employee-Objekt.
     * @return Der Vorname und Nachname des Mitarbeiters.
     * @author joshuasperber
     */
    private String getFullName(Employee emp) {
        return emp.getFirstName() + " " + emp.getLastName();
    }

    /**
     * Bestimmt basierend auf den Berechtigungen des eingeloggten Benutzers,
     * welche Felder in der View angezeigt werden sollen.
     * @return Eine sortierte Liste der sichtbaren Feldnamen.
     * @author joshuasperber
     */
    private List<String> getVisibleFieldsForUser() {
        List<String> fields = new ArrayList<>(Arrays.asList(
                "firstName", "lastName", "username", "email"
        ));

        // Zeige zusätzliche Felder an, wenn der Benutzer seine eigenen Daten ansieht
        if (loggedInUser.equals(employee)) {
            fields.addAll(Arrays.asList(
                    "phoneNumber", "dateOfBirth", "address", "gender",
                    "departmentId", "roleId", "teamId", "hireDate"
            ));
        }

        // Zeige zusätzliche Felder an, wenn der Benutzer HR-Mitarbeiter, IT-Admin oder HR-Leiter ist
        if (!loggedInUser.equals(employee) && (loggedInUser.isHr() || loggedInUser.isItAdmin() || loggedInUser.isHrHead())) {
            fields.addAll(Arrays.asList(
                    "phoneNumber", "dateOfBirth", "address", "gender",
                    "departmentId", "roleId", "teamId", "hireDate"
            ));
        }

        // Verwende LinkedHashSet, um Duplikate zu entfernen und die Reihenfolge zu erhalten
        return new ArrayList<>(new LinkedHashSet<>(fields));
    }

    /**
     * Gibt die lokalisierte Beschriftung für einen Feldnamen zurück.
     * @param field Der interne Feldname.
     * @return Die zugehörige, menschenlesbare Beschriftung.
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
        labels.put("teamId", "Team:");
        labels.put("roleId", "Position:");
        labels.put("username", "Benutzername:");
        labels.put("hireDate", "Einstellungsdatum:");
        return labels.getOrDefault(field, field + ":");
    }

    /**
     * Gibt den Wert eines spezifischen Feldes aus dem `employee`-Objekt zurück.
     * @param field Der interne Feldname.
     * @return Der Wert des Feldes als String.
     * @author joshuasperber
     */
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

    /**
     * Überprüft, ob der eingeloggte Benutzer die Berechtigung hat, die
     * Mitarbeiterdaten zu bearbeiten.
     * @return true, wenn der Benutzer die Daten bearbeiten darf, sonst false.
     * @author joshuasperber
     */
    private boolean canEditData() {
        return loggedInUser.equals(employee) ||
                loggedInUser.isHr() ||
                loggedInUser.isItAdmin() ||
                loggedInUser.isHrHead();
    }

    /**
     * Bestimmt, ob ein spezifisches Feld im Bearbeitungsmodus editierbar sein soll.
     * Bestimmte Felder wie der Benutzername sind nicht editierbar.
     * @param field Der interne Feldname.
     * @return true, wenn das Feld bearbeitet werden kann, sonst false.
     * @author joshuasperber
     */
    private boolean isFieldEditable(String field) {
        Set<String> nonEditable = new HashSet<>(Arrays.asList(
                "username"
        ));
        // Der Benutzername ist nicht editierbar
        if (nonEditable.contains(field)) {
            return false;
        }

        // Spezielle Regel für das Einstellungsdatum
        if (field.equals("hireDate")) {
            return loggedInUser.isHr() || loggedInUser.isItAdmin() || loggedInUser.isHrHead();
        }

        // Generelle Regel für alle anderen Felder, die für den eingeloggten Nutzer editierbar sind
        return loggedInUser.isItAdmin() || loggedInUser.isHr() || loggedInUser.isHrHead() || loggedInUser.equals(employee);
    }

    /**
     * Gibt eine String-Repräsentation des Objekts zurück.
     * @return Eine beschreibende Zeichenkette.
     * @author joshuasperber
     */
    @Override
    public String toString() {
        return "EmployeeDataView[" + employee.getId() + "]";
    }
}