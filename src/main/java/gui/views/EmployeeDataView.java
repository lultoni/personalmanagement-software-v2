package gui.views;

import model.db.Employee;
import core.CompanyStructureManager; // Nicht direkt verwendet, aber eventuell für andere Caches
import core.EmployeeManager;
import core.EventManager;
import util.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.BufferedReader; // Neu hinzugefügt
import java.io.InputStream; // Neu hinzugefügt
import java.io.InputStreamReader; // Neu hinzugefügt
import java.util.stream.Collectors; // Neu hinzugefügt
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

    // Hintergrundbild-Variable
    private BufferedImage backgroundImage;

    public EmployeeDataView(Employee loggedInUser, Employee employee,
                            EmployeeManager employeeManager, EventManager eventManager) {
        this.loggedInUser = loggedInUser;
        this.employee = employee;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

        // ********************************************************************
        // HINTERGRUNDDESIGN-INTEGRATION START
        // ********************************************************************
        try {
            // Lade das Hintergrundbild. Stelle sicher, dass der Pfad korrekt ist.
            // "icons/Hintergrundbild.png" ist der Pfad innerhalb deines Ressourcenordners.
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            // Fehlerbehandlung: Wenn das Bild nicht geladen werden kann, wirf eine RuntimeException
            // oder setze ein Fallback (z.B. einfarbiger Hintergrund).
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden für EmployeeDataView.", e);
        }

        // Erstelle das spezielle JPanel, das das Hintergrundbild zeichnet
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Setze die Transparenz (hier 0.2f für 20% Deckkraft, wie in LoginView)
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                // Zeichne das Bild, skaliert auf die Größe des Panels
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false); // Wichtig: Macht das Panel transparent, damit paintComponent greift

        // Setze das Layout der Haupt-View und füge das Hintergrundpanel hinzu
        // Alle anderen Komponenten werden ZUM backgroundPanel hinzugefügt, NICHT direkt zur View
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
        // ********************************************************************
        // HINTERGRUNDDESIGN-INTEGRATION ENDE
        // ********************************************************************


        initializeCaches();
        // setupUI wird jetzt das backgroundPanel als Container nutzen
        setupUI(backgroundPanel); // Übergabe des backgroundPanel an setupUI
    }

    /**
     * Hilfsmethode zum Lesen eines InputStream in einen String.
     * Dies wird benötigt, da JsonParser.parseJsonArray wahrscheinlich einen String erwartet,
     * der den JSON-Inhalt enthält, und nicht direkt einen InputStream.
     * @param is Der InputStream, der gelesen werden soll.
     * @return Der Inhalt des InputStream als String.
     * @throws IOException Wenn ein Fehler beim Lesen des Streams auftritt.
     */
    private String readInputStreamToString(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private void initializeCaches() {
        try {
            // ********************************************************************
            // KORREKTUR: Lese den InputStream in einen String, bevor er an den JsonParser übergeben wird.
            // ********************************************************************
            InputStream departmentStream = getClass().getResourceAsStream("/json/Department.json");
            if (departmentStream == null) {
                throw new IOException("Department.json konnte nicht im Ressourcenpfad gefunden werden.");
            }
            String departmentJsonString = readInputStreamToString(departmentStream);
            List<Map<String, Object>> departments = JsonParser.parseJsonArray(departmentJsonString);


            InputStream roleStream = getClass().getResourceAsStream("/json/Role.json");
            if (roleStream == null) {
                throw new IOException("Role.json konnte nicht im Ressourcenpfad gefunden werden.");
            }
            String roleJsonString = readInputStreamToString(roleStream);
            List<Map<String, Object>> roles = JsonParser.parseJsonArray(roleJsonString);

            // Füge Daten zu den Caches hinzu
            for (Map<String, Object> dept : departments) {
                String id = (String) dept.get("departmentId");
                String name = (String) dept.get("name");
                departmentCache.put(id, name);
            }

            for (Map<String, Object> role : roles) {
                String id = (String) role.get("roleId");
                String name = (String) role.get("name");
                roleCache.put(id, name);
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Initialisieren der Caches: " + e.getMessage());
            // Fallback-Werte setzen
            departmentCache.put("default", "Allgemein");
            roleCache.put("default", "Mitarbeiter");
            // Zeige eine Fehlermeldung an, falls Caches nicht geladen werden konnten
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Abteilungs- oder Rollendaten: " + e.getMessage(),
                    "Datenfehler",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Wichtig für detailliertes Debugging
        }
    }

    // setupUI Methode wurde angepasst, um das übergebene Container-Panel zu nutzen
    private void setupUI(JPanel parentContainer) {
        setView_id("view-employee");
        setView_name("Mitarbeiter: " + getFullName(employee));
        // Die Border wird jetzt auf ein inneres Panel angewendet, nicht auf die View selbst
        // setBorder(new EmptyBorder(10, 10, 10, 10)); // Diese Zeile wird entfernt oder verschoben

        // Erstelle ein Haupt-Content-Panel, das alle spezifischen UI-Elemente enthält
        // Dieses Panel ist transparent, damit der Hintergrund durchscheint
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false); // Wichtig: Macht das Panel transparent
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Hier die Border anwenden

        // Header
        JLabel titleLabel = new JLabel(getFullName(employee), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK); // Textfarbe für bessere Lesbarkeit auf transparentem Hintergrund
        mainContentPanel.add(titleLabel, BorderLayout.NORTH); // Zum mainContentPanel hinzufügen

        // Hauptinhalt
        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dataPanel.setOpaque(false); // Auch das dataPanel transparent machen

        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false); // ScrollPane transparent machen
        scrollPane.getViewport().setOpaque(false); // Viewport des ScrollPanes transparent machen

        mainContentPanel.add(scrollPane, BorderLayout.CENTER); // Zum mainContentPanel hinzufügen

        // Button Panel
        JPanel buttonPanel = setupButtonPanel(); // Methode aufrufen
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH); // Zum mainContentPanel hinzufügen

        // Füge das gesamte mainContentPanel zum übergebenen Hintergrund-Container hinzu
        parentContainer.add(mainContentPanel, BorderLayout.CENTER);

        refreshDataDisplay();
    }

    // setupButtonPanel gibt jetzt das Panel zurück, anstatt es direkt hinzuzufügen
    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false); // Auch das ButtonPanel transparent machen

        if (canEditData()) {
            JButton editButton = new JButton("Bearbeiten");
            editButton.addActionListener(this::toggleEditMode);
            buttonPanel.add(editButton);
        }

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> eventManager.callEvent("moveBackView", null));
        buttonPanel.add(backButton);

        return buttonPanel; // Panel zurückgeben
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
        gridPanel.setOpaque(false); // Wichtig: GridPanel transparent machen
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
        label.setForeground(Color.BLACK); // Optional: Textfarbe anpassen, falls Hintergrund dunkler ist
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
        label.setForeground(Color.DARK_GRAY); // Optional: Textfarbe anpassen
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

            // Update-Logik mit remove und add
            // Dies ist ein potenzieller Engpass und könnte bei vielen Mitarbeitern langsam sein.
            // Besser wäre eine direkte Update-Methode im EmployeeManager.
            employeeManager.removeEmployee(employee.getId());
            employeeManager.addEmployee(
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
            // Füge hier weitere Felder hinzu, die bearbeitet werden können
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
