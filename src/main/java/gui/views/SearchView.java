package gui.views;

import model.db.Employee;
import util.EmployeeFieldAccessEvaluator;
import core.EmployeeManager;
import core.EventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private Employee currentUser;
    private EmployeeManager employeeManager;
    private EventManager eventManager;

    public SearchView() throws IOException {
        this(null, null, null);
    }

    public SearchView(Employee currentUser, EmployeeManager employeeManager, EventManager eventManager) throws IOException {
        this.currentUser = currentUser;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

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

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setOpaque(false);

        searchField = new JTextField("Suche nach Namen, Email ...");
        searchField.setForeground(Color.GRAY);
        searchField.setMaximumSize(new Dimension(450, 40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
        departmentDropdown.setMaximumSize(new Dimension(200, 40));
        departmentDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14));
        departmentDropdown.addItem("Alle Abteilungen");

        try {
            List<Department> departments = new ArrayList<>(CompanyStructureManager.getInstance().getAllDepartments());
            for (Department dep : departments) {
                departmentDropdown.addItem(dep.getName());
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Abteilungen für die Suche: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Abteilungsdaten. Suche nach Abteilung möglicherweise eingeschränkt.",
                    "Datenfehler",
                    JOptionPane.WARNING_MESSAGE);
        }

        searchPanel.add(departmentDropdown);
        searchPanel.add(Box.createHorizontalStrut(10));

        JButton searchButton = new JButton("Suchen");
        searchButton.setMaximumSize(new Dimension(100, 40));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        this.resultsPanel = new JPanel();
        this.resultsPanel.setLayout(new BoxLayout(this.resultsPanel, BoxLayout.Y_AXIS));
        this.resultsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(this.resultsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        backgroundPanel.add(searchPanel, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private void performSearch() {
        if (employeeManager == null) {
            System.err.println("Fehler: EmployeeManager ist in SearchView nicht gesetzt!");
            JOptionPane.showMessageDialog(this,
                    "Interner Fehler: EmployeeManager ist nicht verfügbar. Suche kann nicht durchgeführt werden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Employee> currentAllEmployees = employeeManager.findAll();
        System.out.println("SearchView: employeeManager.findAll() hat " + currentAllEmployees.size() + " Mitarbeiter zurückgegeben.");

        String keyword = searchField.getText().trim().toLowerCase();
        String department = (String) departmentDropdown.getSelectedItem();

        System.out.println("SearchView: Suchbegriff (Keyword): '" + keyword + "'");
        System.out.println("SearchView: Ausgewählte Abteilung: '" + department + "'");

        List<Employee> filtered = currentAllEmployees.stream()
                .filter(emp -> EmployeeFieldAccessEvaluator.canViewBasicData(currentUser, emp))
                .filter(emp -> {
                    boolean keywordMatch = true;
                    if (!keyword.isEmpty() && !keyword.equals("suche nach namen, email ...")) {
                        String fullName = (emp.getFirstName() + " " + emp.getLastName()).toLowerCase();
                        String email = emp.getEmail().toLowerCase();
                        keywordMatch = fullName.contains(keyword) || email.contains(keyword);
                    }
                    return keywordMatch;
                })
                .filter(emp -> {
                    boolean departmentMatch = true;
                    if (department != null && !department.equals("Alle Abteilungen")) {
                        String empDepartmentName = getDepartmentNameById(emp.getDepartmentId());
                        departmentMatch = empDepartmentName.equalsIgnoreCase(department);
                    }
                    return departmentMatch;
                })
                .collect(Collectors.toList());

        System.out.println("SearchView: Nach Filterung wurden " + filtered.size() + " Mitarbeiter gefunden.");

        updateResultsPanel(filtered);
    }

    private void updateResultsPanel(List<Employee> results) {
        this.resultsPanel.removeAll();

        if (results.isEmpty()) {
            JLabel noResults = new JLabel("Keine Ergebnisse gefunden.");
            noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.resultsPanel.add(noResults);
        } else {
            for (Employee emp : results) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
                card.setBackground(new Color(255, 255, 255, 180));

                // Wir legen eine feste Größe fest und verhindern, dass das Panel streckt
                Dimension cardSize = new Dimension(600, 50); // Beispielgröße
                card.setPreferredSize(cardSize);
                card.setMaximumSize(cardSize); // Das ist entscheidend für das BoxLayout!

                // Leerer Border für Innenabstand
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                String basic = emp.getFirstName() + " " + emp.getLastName() + " (" + emp.getEmail() + ")";
                JLabel label = new JLabel(basic);
                label.setForeground(Color.BLACK);
                card.add(label, BorderLayout.CENTER);

                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Mitarbeiterkarte geklickt: " + emp.getFirstName() + " " + emp.getLastName());
                        if (eventManager != null) {
                            eventManager.callEvent("changeView", new Object[]{
                                    new EmployeeDataView(currentUser, emp, employeeManager, eventManager)
                            });
                        } else {
                            System.err.println("Fehler: EventManager ist in SearchView nicht gesetzt!");
                            JOptionPane.showMessageDialog(card,
                                    "Interner Fehler: EventManager ist nicht verfügbar. Ansicht kann nicht gewechselt werden.",
                                    "Fehler",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                // Füge die Karte und einen kleinen vertikalen Abstand hinzu
                this.resultsPanel.add(card);
                this.resultsPanel.add(Box.createVerticalStrut(5));
            }
        }

        this.resultsPanel.revalidate();
        this.resultsPanel.repaint();
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
            System.err.println("Fehler beim Abrufen des Abteilungsnamens für ID " + deptId + ": " + e.getMessage());
            return "Unbekannte Abteilung (" + deptId + ")";
        }
    }
}
