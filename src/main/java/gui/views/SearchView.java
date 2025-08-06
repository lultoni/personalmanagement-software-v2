package gui.views;

import model.db.Employee;
import util.EmployeeFieldAccessEvaluator;
import core.EmployeeManager; // NEU: Importiere den EmployeeManager

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import core.CompanyStructureManager;
import model.json.Department;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;

public class SearchView extends View {

    private JTextField searchField;
    private JComboBox<String> departmentDropdown;
    private JPanel resultsPanel;

    // private List<Employee> allEmployees; // Diese Liste wird nicht mehr als Instanzvariable gehalten
    private Employee currentUser;
    private EmployeeManager employeeManager; // NEU: Referenz zum EmployeeManager

    public SearchView() throws IOException {
        // Dieser Konstruktor sollte idealerweise den EmployeeManager erhalten,
        // aber für die Kompatibilität lassen wir ihn und rufen den Hauptkonstruktor auf.
        // Die allEmployees-Liste muss dann über den EmployeeManager geholt werden.
        this(null, null); // employeeManager wird dann später gesetzt oder übergeben
    }

    public SearchView(Employee currentUser, List<Employee> allEmployees) throws IOException {
        // ********************************************************************
        // KORREKTUR: EmployeeManager wird jetzt benötigt, um die Daten abzurufen.
        // Die allEmployees-Liste wird nicht mehr direkt übergeben, sondern bei Bedarf vom Manager geholt.
        // ********************************************************************
        this.currentUser = currentUser;
        // this.allEmployees = allEmployees != null ? new ArrayList<>(allEmployees) : new ArrayList<>(); // Entfernt
        // Der EmployeeManager muss nun übergeben werden, damit die SearchView ihn nutzen kann.
        // Annahme: Der EmployeeManager wird vom EventManager oder der Haupt-GUI-Klasse übergeben.
        // Für den Moment setzen wir ihn auf null und nehmen an, dass er über einen Setter gesetzt wird
        // oder der Konstruktor in FeatureBar angepasst wird.
        // Wenn dieser Konstruktor von FeatureBar aufgerufen wird, muss er den EmployeeManager übergeben.
        // Beispiel: new SearchView(currentUser, employeeManager)
        this.employeeManager = null; // Muss später gesetzt werden!
        // ********************************************************************

        // Hintergrundbild laden
        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Hintergrundpanel
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

        // --- Suchleiste ---
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setOpaque(false);

        searchField = new JTextField("Suche nach Namen, Email ...");
        searchField.setForeground(Color.GRAY);
        // ********************************************************************
        // KORREKTUR: Größe der Suchleiste anpassen
        // ********************************************************************
        searchField.setMaximumSize(new Dimension(450, 40)); // Breiter und höher
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Schriftgröße anpassen
        // ********************************************************************
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Suche nach Namen, Email ...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Suche nach Namen, Email ...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));

        departmentDropdown = new JComboBox<>();
        // ********************************************************************
        // KORREKTUR: Größe des Dropdowns anpassen
        // ********************************************************************
        departmentDropdown.setMaximumSize(new Dimension(200, 40)); // Breiter und höher
        departmentDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Schriftgröße anpassen
        // ********************************************************************
        departmentDropdown.addItem("Alle Abteilungen");

        List<Department> departments = new ArrayList<>(CompanyStructureManager.getInstance().getAllDepartments());
        for (Department dep : departments) {
            departmentDropdown.addItem(dep.getName());
        }
        searchPanel.add(departmentDropdown);
        searchPanel.add(Box.createHorizontalStrut(10));

        JButton searchButton = new JButton("Suchen");
        // ********************************************************************
        // KORREKTUR: Größe des Buttons anpassen
        // ********************************************************************
        searchButton.setMaximumSize(new Dimension(100, 40)); // Höhe anpassen
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14)); // Schriftgröße anpassen
        // ********************************************************************
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        // --- Ergebnisbereich ---
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // In Hintergrundpanel einfügen
        backgroundPanel.add(searchPanel, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // In Hauptansicht einfügen
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    // ********************************************************************
    // NEU: Setter für EmployeeManager
    // ********************************************************************
    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }
    // ********************************************************************

    private void performSearch() {
        // ********************************************************************
        // KORREKTUR: allEmployees bei jeder Suche vom EmployeeManager abrufen
        // ********************************************************************
        if (employeeManager == null) {
            System.err.println("Fehler: EmployeeManager ist in SearchView nicht gesetzt!");
            JOptionPane.showMessageDialog(this,
                    "Interner Fehler: EmployeeManager ist nicht verfügbar. Suche kann nicht durchgeführt werden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // allEmployees wird jetzt immer aktuell vom Manager geholt
        List<Employee> currentAllEmployees = employeeManager.findAll();
        // ********************************************************************

        String keyword = searchField.getText().trim().toLowerCase();
        String department = (String) departmentDropdown.getSelectedItem();

        List<Employee> filtered = currentAllEmployees.stream() // Hier currentAllEmployees verwenden
                .filter(emp -> EmployeeFieldAccessEvaluator.canViewBasicData(currentUser, emp))
                .filter(emp -> {
                    // Filter nach Schlüsselwort (Name oder E-Mail)
                    if (!keyword.isEmpty() && !keyword.equals("suche nach namen, email ...")) {
                        String fullName = (emp.getFirstName() + " " + emp.getLastName()).toLowerCase();
                        String email = emp.getEmail().toLowerCase();
                        if (!fullName.contains(keyword) && !email.contains(keyword)) return false;
                    }

                    // Filter nach Abteilung
                    if (department != null && !department.equals("Alle Abteilungen")) {
                        String empDepartmentName = getDepartmentNameById(emp.getDepartmentId());
                        if (!empDepartmentName.equalsIgnoreCase(department)) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        updateResultsPanel(filtered);
    }

    private void updateResultsPanel(List<Employee> results) {
        resultsPanel.removeAll();

        if (results.isEmpty()) {
            JLabel noResults = new JLabel("Keine Ergebnisse gefunden.");
            noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.add(noResults);
        } else {
            for (Employee emp : results) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                card.setBackground(new Color(255, 255, 255, 180));
                String basic = emp.getFirstName() + " " + emp.getLastName() + " (" + emp.getEmail() + ")";
                JLabel label = new JLabel(basic);
                label.setForeground(Color.BLACK);
                card.add(label, BorderLayout.CENTER);
                resultsPanel.add(card);
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private String getDepartmentNameById(String deptId) {
        try {
            return CompanyStructureManager.getInstance()
                    .getAllDepartments()
                    .stream()
                    .filter(d -> d.getDepartmentId().equals(deptId))
                    .map(d -> d.getName())
                    .findFirst()
                    .orElse(deptId);
        } catch (IOException e) {
            System.err.println("Fehler beim Abrufen des Abteilungsnamens: " + e.getMessage());
            throw new RuntimeException("Konnte Abteilungsnamen nicht abrufen: " + e.getMessage(), e);
        }
    }
}
