package gui.views;

import model.db.Employee;
import core.CompanyStructureManager;
import core.EmployeeManager;
import core.EventManager;
import model.json.Qualification;
import util.JsonParser;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Schulungsübersicht für Mitarbeiter.
 * @author Dorian Gläske
 * @version 2.0 (Überarbeitete GUI und Logik)
 * @since 2025-07-31
 */
public class SchulungView extends View {

    private final Employee loggedInUser;
    private final Employee employee;
    private final EmployeeManager employeeManager;
    private final EventManager eventManager;
    private final CompanyStructureManager companyStructureManager;

    private List<String> completedTrainings;
    private List<String> potentialTrainings;
    private List<String> openTrainings;

    private JButton completedTrainingsButton;
    private JButton openTrainingsButton;
    private JButton potentialTrainingsButton;
    private JButton markAsCompletedButton;
    private JList<String> trainingsList;
    private DefaultListModel<String> listModel;

    private enum TrainingView {
        COMPLETED, OPEN, POTENTIAL
    }

    private TrainingView currentView = TrainingView.COMPLETED;

    public SchulungView(Employee loggedInUser, Employee employee, EmployeeManager employeeManager, EventManager eventManager) throws IOException {
        this.loggedInUser = loggedInUser;
        this.employee = employee;
        this.employeeManager = employeeManager;
        this.eventManager = eventManager;
        this.companyStructureManager = CompanyStructureManager.getInstance();
        setView_id("view-training");
        setView_name("Schulungsübersicht");

        // Daten initial laden
        updateTrainingData();

        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Hintergrundpanel mit halbtransparentem Bild
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
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setOpaque(false);

        // Titel
        JLabel titleLabel = new JLabel("Schulungsübersicht", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Hauptpanel für Buttons, Liste und Action-Buttons
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        mainPanel.setOpaque(false);

        // Button Panel für die Ansichten
        JPanel viewButtonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        viewButtonPanel.setOpaque(false);

        completedTrainingsButton = new JButton("Absolvierte Schulungen");
        openTrainingsButton = new JButton("Offene Schulungen");
        potentialTrainingsButton = new JButton("Mögliche Schulungen");

        for (JButton btn : new JButton[]{completedTrainingsButton, openTrainingsButton, potentialTrainingsButton}) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setFocusPainted(false);
            viewButtonPanel.add(btn);
        }
        mainPanel.add(viewButtonPanel, BorderLayout.NORTH);

        // Liste zur Anzeige der Schulungen
        listModel = new DefaultListModel<>();
        trainingsList = new JList<>(listModel);
        trainingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainingsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(trainingsList);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Action Panel mit dem "Als absolviert markieren" Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setOpaque(false);
        markAsCompletedButton = new JButton("Als absolviert markieren");
        markAsCompletedButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        markAsCompletedButton.setEnabled(false); // Anfangs deaktiviert
        actionPanel.add(markAsCompletedButton);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // --- Event Listener ---
        completedTrainingsButton.addActionListener(e -> showCompletedTrainings());
        openTrainingsButton.addActionListener(e -> showOpenTrainings());
        potentialTrainingsButton.addActionListener(e -> showPotentialTrainings());

        trainingsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                markAsCompletedButton.setEnabled(trainingsList.getSelectedIndex() != -1 && currentView == TrainingView.POTENTIAL);
            }
        });

        markAsCompletedButton.addActionListener(e -> {
            String selectedTrainingName = trainingsList.getSelectedValue();
            if (selectedTrainingName != null) {
                // Finde die Qualifikation-ID aus dem Namen
                String selectedQualId = getQualIdFromTrainingName(selectedTrainingName);
                if (selectedQualId != null) {
                    // TODO: Implementiere die Logik zum Zuweisen der Qualifikation
                    // Angenommen, du hast eine Methode, die die Qualifikation dem Employee hinzufügt
                    System.out.println("Qualifikation '" + selectedQualId + "' als abgeschlossen markiert.");
                    // Beispiel-Aufruf (diese Methode muss noch existieren):
                    // employeeManager.addQualificationToEmployee(loggedInUser, selectedQualId);

                    // UI aktualisieren
                    updateTrainingData();
                    showPotentialTrainings();
                } else {
                    JOptionPane.showMessageDialog(this, "Fehler: Konnte Qualifikation-ID nicht finden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Anfangsansicht festlegen
        showCompletedTrainings();
    }

    private void updateTrainingData() {
        this.completedTrainings = Collections.singletonList(loggedInUser.getQualifications());
        // Hier sollte die Logik zum Laden der offenen Schulungen stehen,
        // z.B. aus der Datenbank, falls vorhanden.
        // Für dieses Beispiel verwenden wir einfach eine leere Liste
        this.openTrainings = new ArrayList<>();
        this.potentialTrainings = findPotentialTrainings(Collections.singletonList(loggedInUser.getQualifications()));
    }

    private void showCompletedTrainings() {
        currentView = TrainingView.COMPLETED;
        listModel.clear();
        completedTrainings.forEach(id -> listModel.addElement(getQualificationNameById(id)));

        completedTrainingsButton.setEnabled(false);
        openTrainingsButton.setEnabled(true);
        potentialTrainingsButton.setEnabled(true);
        markAsCompletedButton.setEnabled(false);
    }

    private void showOpenTrainings() {
        currentView = TrainingView.OPEN;
        listModel.clear();
        openTrainings.forEach(id -> listModel.addElement(getQualificationNameById(id)));

        completedTrainingsButton.setEnabled(true);
        openTrainingsButton.setEnabled(false);
        potentialTrainingsButton.setEnabled(true);
        markAsCompletedButton.setEnabled(false);
    }

    private void showPotentialTrainings() {
        currentView = TrainingView.POTENTIAL;
        listModel.clear();
        potentialTrainings.forEach(id -> listModel.addElement(getQualificationNameById(id)));

        completedTrainingsButton.setEnabled(true);
        openTrainingsButton.setEnabled(true);
        potentialTrainingsButton.setEnabled(false);
        markAsCompletedButton.setEnabled(trainingsList.getSelectedIndex() != -1);
    }

    private List<String> findPotentialTrainings(List<String> employeeQualificationIds) {
        return employeeQualificationIds.stream()
                .flatMap(qualId -> companyStructureManager.findQualificationById(qualId)
                        .map(Qualification::getFollowupSkills)
                        .orElse(new ArrayList<>()).stream())
                .filter(skillId -> !completedTrainings.contains(skillId))
                .distinct()
                .collect(Collectors.toList());
    }

    private String getQualificationNameById(String qualId) {
        return companyStructureManager.findQualificationById(qualId)
                .map(Qualification::getDescription)
                .orElse("Unbekannte Qualifikation: " + qualId);
    }

    private String getQualIdFromTrainingName(String trainingName) {
        return companyStructureManager.getAllQualifications().stream()
                .filter(q -> getQualificationNameById(q.getRoleId()).equals(trainingName))
                .map(Qualification::getRoleId)
                .findFirst()
                .orElse(null);
    }
}