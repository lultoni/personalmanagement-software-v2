package gui.views;

import model.db.Employee;
import core.CompanyStructureManager;
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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date; // Geändert von java.time.LocalDate
import java.text.SimpleDateFormat; // Neu hinzugefügt für Datumshandhabung
import java.text.ParseException; // Neu hinzugefügt für Datumshandhabung
import java.util.stream.Collectors;
import java.util.*;
import java.util.List;
import java.util.LinkedHashSet;


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
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Datumformat

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
            departmentCache.put("default", "Allgemein");
            roleCache.put("default", "Mitarbeiter");
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Abteilungs- oder Rollendaten: " + e.getMessage(),
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

            // Update-Logik mit remove und add
            // Dies ist ein potenzieller Engpass und könnte bei vielen Mitarbeitern langsam sein.
            // Besser wäre eine direkte Update-Methode im EmployeeManager.
            // employeeManager.removeEmployee(employee.getId()); // Entfernen nicht mehr nötig, updateEmployee macht das
            employeeManager.updateEmployee(employee); // Direkter Aufruf der updateEmployee Methode

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
            // ********************************************************************
            // KORREKTUR: Datumshandhabung für dateOfBirth (String zu Date)
            // ********************************************************************
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
            // ********************************************************************
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
                    "phoneNumber", "dateOfBirth", "address", "gender",
                    "departmentId", "roleId", "hireDate", "employmentStatus"
            ));
        }

        if (!loggedInUser.equals(employee) && (loggedInUser.isHr() || loggedInUser.isItAdmin())) {
            fields.addAll(Arrays.asList(
                    "phoneNumber", "dateOfBirth", "address", "gender",
                    "departmentId", "roleId", "hireDate", "employmentStatus"
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
            // ********************************************************************
            // KORREKTUR: Datum zu String formatieren
            // ********************************************************************
            case "dateOfBirth": return employee.getDateOfBirth() != null ? dateFormat.format(employee.getDateOfBirth()) : "";
            case "address": return employee.getAddress();
            case "gender": return String.valueOf(employee.getGender());
            case "departmentId": return departmentCache.getOrDefault(employee.getDepartmentId(), employee.getDepartmentId());
            case "roleId": return roleCache.getOrDefault(employee.getRoleId(), employee.getRoleId());
            case "username": return employee.getUsername();
            case "hireDate": return employee.getHireDate() != null ? dateFormat.format(employee.getHireDate()) : "";
            // ********************************************************************
            case "employmentStatus": return employee.getEmploymentStatus();
            default: return "";
        }
    }

    private boolean canEditData() {
        return loggedInUser.equals(employee) ||
                loggedInUser.isHr() ||
                loggedInUser.isItAdmin() ||
                loggedInUser.isHrHead(); // HR-Heads können auch bearbeiten
    }

    private boolean isFieldEditable(String field) {
        Set<String> nonEditable = new HashSet<>(Arrays.asList(
                "username", "departmentId", "roleId", "hireDate", // hireDate bleibt nicht bearbeitbar
                "employmentStatus"
        ));
        // dateOfBirth ist jetzt bearbeitbar
        // Wenn der eingeloggte Benutzer nicht HR/IT-Admin/HR-Head ist, aber sein eigenes Profil bearbeitet,
        // und das Feld nicht in der nonEditable-Liste ist, dann ist es bearbeitbar.
        // Wenn der eingeloggte Benutzer HR/IT-Admin/HR-Head ist, kann er alle nicht-nonEditable-Felder bearbeiten.
        if (field.equals("hireDate") && (loggedInUser.isHr() || loggedInUser.isItAdmin() || loggedInUser.isHrHead())) {
            return true; // HR/IT-Admins/HR-Heads können hireDate bearbeiten
        }
        return !nonEditable.contains(field) || loggedInUser.isItAdmin() || loggedInUser.isHr() || loggedInUser.isHrHead();
    }

    @Override
    public String toString() {
        return "EmployeeDataView[" + employee.getId() + "]";
    }
}
