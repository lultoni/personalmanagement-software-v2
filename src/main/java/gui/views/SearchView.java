package gui.views;

import model.db.Employee;
import util.EmployeeFieldAccessEvaluator;
import core.EmployeeManager;
import core.EventManager;
import model.json.Department;
import core.CompanyStructureManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.util.Map;

/**
 * Die `SearchView` ist eine grafische Benutzeroberfläche zur Suche nach Mitarbeitern.
 * Sie ermöglicht es Benutzern, Mitarbeiter nach Namen, E-Mail-Adresse oder Abteilung
 * zu filtern. Die Ergebnisse werden in einer Liste von anklickbaren Karten angezeigt.
 * Abhängig vom "Modus" (z.B. "view" oder "edit_mode") leitet die Ansicht bei einem
 * Klick auf die entsprechende Mitarbeiter-Detailansicht weiter.
 *
 * @author joshuasperber
 * @since 2025-07-28
 */

public class SearchView extends View {

    private JTextField searchField;
    private JComboBox<String> departmentDropdown;
    private JPanel resultsPanel;

    private Employee currentUser;
    private EmployeeManager employeeManager;
    private EventManager eventManager;

    private String mode; // Steuert, welche View beim Klick geöffnet wird
    private Map<String, String> departmentIdToNameCache = new HashMap<>();

    /**
     * Standard-Konstruktor. Wird in der Regel nicht direkt verwendet.
     * @throws IOException wenn das Hintergrundbild nicht geladen werden kann.
     */
    public SearchView() throws IOException {
        this(null, null, null, "view");
    }

    /**
     * Konfigurierter Konstruktor für die normale Ansicht.
     * @param currentUser Der aktuell eingeloggte Benutzer.
     * @param employeeManager Eine Instanz des EmployeeManagers.
     * @param eventManager Eine Instanz des EventManagers.
     * @throws IOException wenn das Hintergrundbild nicht geladen werden kann.
     */
    public SearchView(Employee currentUser, EmployeeManager employeeManager, EventManager eventManager) throws IOException {
        this(currentUser, employeeManager, eventManager, "view");
    }

    /**
     * Haupt-Konstruktor. Erstellt die View und initialisiert alle Komponenten.
     *
     * @param currentUser Der aktuell eingeloggte Benutzer.
     * @param employeeManager Eine Instanz des EmployeeManagers zur Datenverwaltung.
     * @param eventManager Eine Instanz des EventManagers für die Navigation.
     * @param mode Definiert den Modus der Suche ("view" oder "edit_mode").
     * @throws IOException wenn das Hintergrundbild nicht geladen werden kann.
     */
    public SearchView(Employee currentUser, EmployeeManager employeeManager, EventManager eventManager, String mode) throws IOException {
        this.currentUser = currentUser;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;
        this.mode = mode != null ? mode : "view";

        // Laden des Hintergrundbildes
        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Erstellen eines Panels, das das Bild mit Transparenz zeichnet
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Setzen der Transparenz
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false);

        // Erstellen des Suchfelds und der Dropdown-Menüs
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setOpaque(false);

        // Suchfeld für Namen und E-Mail
        searchField = new JTextField("Suche nach Namen, Email ...");
        searchField.setForeground(Color.GRAY);
        searchField.setMaximumSize(new Dimension(450, 40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Fügt Focus-Listener hinzu, um den Platzhaltertext zu verwalten
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

        // Dropdown-Menü für Abteilungen
        departmentDropdown = new JComboBox<>();
        departmentDropdown.setMaximumSize(new Dimension(200, 40));
        departmentDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14));
        departmentDropdown.addItem("Alle Abteilungen");

        // Laden der Abteilungen aus dem CompanyStructureManager
        try {
            for (Object obj : CompanyStructureManager.getInstance().getAllDepartments()) {
                if (obj instanceof Department) {
                    Department dep = (Department) obj;
                    departmentDropdown.addItem(dep.getName());
                    departmentIdToNameCache.put(dep.getDepartmentId(), dep.getName());
                } else if (obj instanceof Map) {
                    Map<String, Object> depMap = (Map<String, Object>) obj;
                    String id = (String) depMap.get("departmentId");
                    String name = (String) depMap.get("name");
                    if (id != null && name != null) {
                        departmentDropdown.addItem(name);
                        departmentIdToNameCache.put(id, name);
                    }
                }
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

        // Suchen-Button
        JButton searchButton = new JButton("Suchen");
        searchButton.setMaximumSize(new Dimension(100, 40));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        // Fügt einen ActionListener hinzu, der die Suche ausführt
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        // Panel für die Suchergebnisse
        this.resultsPanel = new JPanel();
        this.resultsPanel.setLayout(new BoxLayout(this.resultsPanel, BoxLayout.Y_AXIS));
        this.resultsPanel.setOpaque(false);

        // ScrollPane, falls die Ergebnisse nicht auf eine Seite passen
        JScrollPane scrollPane = new JScrollPane(this.resultsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Hinzufügen der Komponenten zum Hintergrund-Panel
        backgroundPanel.add(searchPanel, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Finales Layout-Setup
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Setzt den EmployeeManager.
     * @param employeeManager Der zu setzende EmployeeManager.
     */
    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    /**
     * Setzt den EventManager.
     * @param eventManager Der zu setzende EventManager.
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Führt die Suche basierend auf den Eingabefeldern und der Dropdown-Auswahl durch.
     * Die Methode filtert die Mitarbeiterliste und aktualisiert das Ergebnispanel.
     */
    private void performSearch() {
        if (employeeManager == null) {
            System.err.println("Fehler: EmployeeManager ist in SearchView nicht gesetzt!");
            JOptionPane.showMessageDialog(this,
                    "Interner Fehler: EmployeeManager ist nicht verfügbar. Suche kann nicht durchgeführt werden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ruft alle Mitarbeiter ab
        List<Employee> currentAllEmployees = employeeManager.findAll();
        System.out.println("SearchView: employeeManager.findAll() hat " + currentAllEmployees.size() + " Mitarbeiter zurückgegeben.");

        String keyword = searchField.getText().trim().toLowerCase();
        String department = (String) departmentDropdown.getSelectedItem();

        System.out.println("SearchView: Suchbegriff (Keyword): '" + keyword + "'");
        System.out.println("SearchView: Ausgewählte Abteilung: '" + department + "'");

        // Stream-basierte Filterung der Mitarbeiterliste
        List<Employee> filtered = currentAllEmployees.stream()
                // Filtert Mitarbeiter, auf die der aktuelle Benutzer zugreifen darf
                .filter(emp -> EmployeeFieldAccessEvaluator.canViewBasicData(currentUser, emp))
                // Filtert nach dem Suchbegriff (Name oder E-Mail)
                .filter(emp -> {
                    boolean keywordMatch = true;
                    if (!keyword.isEmpty() && !keyword.equals("suche nach namen, email ...")) {
                        String fullName = (emp.getFirstName() + " " + emp.getLastName()).toLowerCase();
                        String email = emp.getEmail().toLowerCase();
                        keywordMatch = fullName.contains(keyword) || email.contains(keyword);
                    }
                    return keywordMatch;
                })
                // Filtert nach der ausgewählten Abteilung
                .filter(emp -> {
                    boolean departmentMatch = true;
                    if (department != null && !department.equals("Alle Abteilungen")) {
                        String empDepartmentName = departmentIdToNameCache.getOrDefault(emp.getDepartmentId(), "");
                        departmentMatch = empDepartmentName.equalsIgnoreCase(department);
                    }
                    return departmentMatch;
                })
                .collect(Collectors.toList());

        System.out.println("SearchView: Nach Filterung wurden " + filtered.size() + " Mitarbeiter gefunden.");

        // Aktualisiert das UI-Panel mit den gefilterten Ergebnissen
        updateResultsPanel(filtered);
    }

    /**
     * Aktualisiert das Ergebnispanel mit den gefundenen Mitarbeitern.
     * Erstellt für jeden Mitarbeiter eine anklickbare Karte.
     *
     * @param results Die Liste der gefilterten Mitarbeiter.
     */
    private void updateResultsPanel(List<Employee> results) {
        // Entfernt alle alten Ergebnisse
        this.resultsPanel.removeAll();

        if (results.isEmpty()) {
            JLabel noResults = new JLabel("Keine Ergebnisse gefunden.");
            noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.resultsPanel.add(noResults);
        } else {
            // Erstellt für jeden Mitarbeiter eine "Karte"
            for (Employee emp : results) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
                card.setBackground(new Color(255, 255, 255, 180));

                Dimension cardSize = new Dimension(600, 50);
                card.setPreferredSize(cardSize);
                card.setMaximumSize(cardSize);

                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                String basic = emp.getFirstName() + " " + emp.getLastName() + " (" + emp.getEmail() + ")";
                JLabel label = new JLabel(basic);
                label.setForeground(Color.BLACK);
                card.add(label, BorderLayout.CENTER);

                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Fügt einen MouseListener hinzu, um auf Klicks zu reagieren
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Mitarbeiterkarte geklickt im Modus: " + mode);
                        if (eventManager != null) {
                            // Je nach Modus wird eine andere View aufgerufen
                            if ("edit_mode".equals(mode)) {
                                eventManager.callEvent("changeView", new Object[]{
                                        new EditEmployeeView(currentUser, emp, employeeManager, eventManager)
                                });
                            } else {
                                eventManager.callEvent("changeView", new Object[]{
                                        new EmployeeDataView(currentUser, emp, employeeManager, eventManager)
                                });
                            }
                        } else {
                            System.err.println("Fehler: EventManager ist in SearchView nicht gesetzt!");
                            JOptionPane.showMessageDialog(card,
                                    "Interner Fehler: EventManager ist nicht verfügbar. Ansicht kann nicht gewechselt werden.",
                                    "Fehler",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                this.resultsPanel.add(card);
                this.resultsPanel.add(Box.createVerticalStrut(5));
            }
        }

        // Aktualisiert das Layout der Ergebnisliste
        this.resultsPanel.revalidate();
        this.resultsPanel.repaint();
    }
}