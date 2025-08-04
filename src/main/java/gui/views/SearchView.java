package gui.views;

import model.db.Employee;
import util.EmployeeFieldAccessEvaluator;

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

public class SearchView extends View {

    private JTextField searchField;
    private JComboBox<String> departmentDropdown;
    private JCheckBox headOnlyCheckBox;
    private JPanel resultsPanel;

    private List<Employee> allEmployees;
    private Employee currentUser; // der eingeloggte Nutzer

    public SearchView() throws IOException {
        this(null, new ArrayList<>());  //leerer Konstruktor, damit SearchView korrekt initialisiert wird, ohne das Daten sofort notwendig sind
    }



    public SearchView(Employee currentUser, List<Employee> allEmployees) throws IOException {
        this.currentUser = currentUser;
        this.allEmployees = allEmployees != null ? allEmployees : new ArrayList<>();

        setLayout(new BorderLayout());

        // --- Obere Suchleiste ---
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField("Suche nach Namen, Email ...");
        searchField.setForeground(Color.GRAY);
        searchField.setMaximumSize(new Dimension(300, 30));
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
        departmentDropdown.setMaximumSize(new Dimension(180, 30));
        departmentDropdown.addItem("Alle Abteilungen");

        List<Department> departments = (List<Department>) CompanyStructureManager.getInstance().getAllDepartments();
        for (Department dep : departments) {
            departmentDropdown.addItem(dep.getName());

        };
        departmentDropdown.setMaximumSize(new Dimension(180, 30));
        searchPanel.add(departmentDropdown);

        searchPanel.add(Box.createHorizontalStrut(10));

        headOnlyCheckBox = new JCheckBox("Nur Abteilungsleiter");
        headOnlyCheckBox.setMaximumSize(new Dimension(160, 30));
        searchPanel.add(headOnlyCheckBox);

        JButton searchButton = new JButton("Suchen");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // --- Ergebnisbereich ---
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String department = (String) departmentDropdown.getSelectedItem();
        boolean headOnly = headOnlyCheckBox.isSelected();

        List<Employee> filtered = allEmployees.stream()
                .filter(emp -> EmployeeFieldAccessEvaluator.canViewBasicData(currentUser, emp))
                .filter(emp -> {
                    if (!keyword.isEmpty() && !keyword.equals("suche nach namen, email ...")) {
                        String fullName = (emp.getFirstName() + " " + emp.getLastName()).toLowerCase();
                        String email = emp.getEmail().toLowerCase();
                        if (!fullName.contains(keyword) && !email.contains(keyword)) return false;
                    }



                    if (headOnly && !emp.isManager()) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());

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
                String basic = emp.getFirstName() + " " + emp.getLastName() + " (" + emp.getEmail() + ")";
                JLabel label = new JLabel(basic);
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
            throw new RuntimeException(e);
        }
    }
}
