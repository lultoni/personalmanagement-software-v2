package gui.views;

import model.db.Employee;
import core.CompanyStructureManager;
import core.EmployeeManager;
import core.EventManager;
import model.json.Qualification;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Schulungsübersicht für Mitarbeiter.
 *
 * @author Dorian Gläske
 * @version 3.5 (Anzeige von RequiredSkills in absolvierten Schulungen)
 * @since 2025-08-07
 */
public class SchulungView extends View {

    private final Employee loggedInUser;
    private final Employee employee;
    private final EmployeeManager employeeManager;
    private final EventManager eventManager;
    private final CompanyStructureManager companyStructureManager;

    private List<String> completedTrainings;
    private List<String> potentialTrainings;

    private JButton completedTrainingsButton;
    private JButton potentialTrainingsButton;
    private JButton markAsCompletedButton;
    private JList<String> trainingsList;
    private DefaultListModel<String> listModel;

    private enum TrainingView {
        COMPLETED, POTENTIAL
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

        updateTrainingData();

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
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Schulungsübersicht", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        mainPanel.setOpaque(false);

        JPanel viewButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        viewButtonPanel.setOpaque(false);

        completedTrainingsButton = new JButton("Absolvierte Schulungen");
        potentialTrainingsButton = new JButton("Mögliche Schulungen");

        for (JButton btn : new JButton[]{completedTrainingsButton, potentialTrainingsButton}) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setFocusPainted(false);
            viewButtonPanel.add(btn);
        }
        mainPanel.add(viewButtonPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        trainingsList = new JList<>(listModel);
        trainingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainingsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(trainingsList);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setOpaque(false);
        markAsCompletedButton = new JButton("Als absolviert markieren");
        markAsCompletedButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        markAsCompletedButton.setEnabled(false);
        actionPanel.add(markAsCompletedButton);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        completedTrainingsButton.addActionListener(e -> showCompletedTrainings());
        potentialTrainingsButton.addActionListener(e -> showPotentialTrainings());

        trainingsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                markAsCompletedButton.setEnabled(trainingsList.getSelectedIndex() != -1 && currentView == TrainingView.POTENTIAL);
            }
        });

        markAsCompletedButton.addActionListener(e -> {
            String selectedTrainingName = trainingsList.getSelectedValue();
            if (selectedTrainingName != null) {
                String selectedQualId = getQualIdFromTrainingName(selectedTrainingName);
                if (selectedQualId == null) {
                    selectedQualId = selectedTrainingName;
                }

                List<String> currentQualifications = new ArrayList<>(Collections.singleton(loggedInUser.getQualifications()));

                if (!currentQualifications.contains(selectedQualId)) {
                    currentQualifications.add(selectedQualId);
                    String updatedQualificationsString = String.join(",", currentQualifications);
                    loggedInUser.setQualifications(updatedQualificationsString);

                    try {
                        employeeManager.updateEmployee(loggedInUser);

                        updateTrainingData();
                        showPotentialTrainings();

                        JOptionPane.showMessageDialog(this, "Qualifikation '" + selectedTrainingName + "' erfolgreich erworben.", "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        throw new RuntimeException("Fehler beim Aktualisieren des Mitarbeiters: " + ex.getMessage(), ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Diese Qualifikation haben Sie bereits.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        showCompletedTrainings();
    }

    private void updateTrainingData() {
        // Korrigierte Zeile zur korrekten Aufteilung der Qualifikationen
        this.completedTrainings = Collections.singletonList(loggedInUser.getQualifications());
        System.out.println("Geladene Qualifikationen des Nutzers: " + completedTrainings);

        this.potentialTrainings = new ArrayList<>();
        String employeeQualId = "qual-" + loggedInUser.getRoleId();
        Optional<Qualification> optionalQual = companyStructureManager.findQualificationById(employeeQualId);

        if (optionalQual.isPresent()) {
            potentialTrainings.addAll(optionalQual.get().getFollowupSkills());
        }

        Optional<List<String>> requiredSkillsOptional = companyStructureManager.getRequiredSkillsForQualification(loggedInUser.getRoleId());
        if (requiredSkillsOptional.isPresent()) {
            potentialTrainings.addAll(requiredSkillsOptional.get());
        }

        // Korrigierter Teil: Filtere die potenziellen Schulungen, um bereits absolvierte zu entfernen
        this.potentialTrainings = potentialTrainings.stream()
                .filter(skillName -> !completedTrainings.contains(skillName))
                .distinct()
                .collect(Collectors.toList());
    }

    private void showCompletedTrainings() {
        currentView = TrainingView.COMPLETED;
        listModel.clear();
        if (completedTrainings.isEmpty() || (completedTrainings.size() == 1 && completedTrainings.get(0).isEmpty())) {
            listModel.addElement("Keine abgeschlossenen Schulungen gefunden.");
        } else {
            completedTrainings.forEach(qualIdString -> {
                String[] ids = qualIdString.split(",");
                for (String id : ids) {
                    if (!id.trim().isEmpty()) {
                        // Aufruf der neuen Methode zur Anzeige der RequiredSkills
                        listModel.addElement(getQualificationDescriptionWithRequiredSkills(id.trim()));
                    }
                }
            });
        }

        completedTrainingsButton.setEnabled(false);
        potentialTrainingsButton.setEnabled(true);
        markAsCompletedButton.setEnabled(false);
    }

    /**
     * Gibt die Beschreibung einer Qualifikation zusammen mit ihren RequiredSkills zurück.
     * @param qualId Die ID der Qualifikation.
     * @return Ein String, der die Beschreibung und die RequiredSkills enthält.
     */
    private String getQualificationDescriptionWithRequiredSkills(String qualId) {
        Optional<Qualification> optionalQual = companyStructureManager.findQualificationById(qualId);
        if (optionalQual.isPresent()) {
            Qualification qualification = optionalQual.get();
            String description = qualification.getDescription();
            List<String> requiredSkills = qualification.getRequiredSkills();

            StringBuilder sb = new StringBuilder();
            sb.append(description != null ? description : qualId);

            if (requiredSkills != null && !requiredSkills.isEmpty()) {
                sb.append(" (Erforderliche Skills: ");
                sb.append(String.join(", ", requiredSkills));
                sb.append(")");
            }
            return sb.toString();
        }
        return qualId;
    }

    private void showPotentialTrainings() {
        currentView = TrainingView.POTENTIAL;
        listModel.clear();
        if (potentialTrainings.isEmpty()) {
            listModel.addElement("Keine potenziellen Schulungen verfügbar.");
        } else {
            potentialTrainings.forEach(listModel::addElement);
        }

        completedTrainingsButton.setEnabled(true);
        potentialTrainingsButton.setEnabled(false);
        markAsCompletedButton.setEnabled(trainingsList.getSelectedIndex() != -1);
    }

    private String getQualificationDescriptionById(String qualId) {
        return Optional.ofNullable(qualId)
                .flatMap(id -> companyStructureManager.findQualificationById(id))
                .map(Qualification::getDescription)
                .orElse(qualId);
    }

    private String getQualIdFromTrainingName(String trainingName) {
        return companyStructureManager.getAllQualifications().stream()
                .filter(q -> getQualificationDescriptionById(q.getRoleId()).equals(trainingName))
                .map(Qualification::getRoleId)
                .findFirst()
                .orElse(null);
    }
}