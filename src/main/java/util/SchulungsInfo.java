package util;

import core.CompanyStructureManager;
import model.db.Employee;
import model.json.Qualification; // Importiere die Qualification Klasse

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SchulungsInfo {
    private Employee employee;

    // Abhängigkeit zum Manager
    private CompanyStructureManager manager;

    // Listen für die verschiedenen Schulungsarten
    private List<String> completedTrainings; // Abgeschlossene Schulungen (Training-IDs)
    private List<String> potentialTrainings; // Mögliche Schulungen (Qualification-IDs)
    private List<String> openTrainings;      // Offene Schulungen (Training-IDs)

    public SchulungsInfo(Employee employee) throws IOException {
        this.employee = employee;
        this.manager = CompanyStructureManager.getInstance();

        // Initialisiere die Listen
        this.completedTrainings = employee.getCompletedTrainingIdsAsList();
        this.openTrainings = new ArrayList<>(); // Kannst du aus der DB laden

        // Ermittle die potenziellen Schulungen basierend auf den Followup-Skills
        this.potentialTrainings = findPotentialTrainings(employee.getQualificationsAsList());
    }

    private List<String> findPotentialTrainings(List<String> employeeQualificationIds) {
        List<String> potentialSkills = new ArrayList<>();

        // Durchlaufe alle Qualifikationen des Mitarbeiters
        for (String qualId : employeeQualificationIds) {
            Optional<Qualification> qualification = manager.findQualificationById(qualId);
            qualification.ifPresent(q -> {
                // Füge alle Followup-Skills als potenzielle Schulungen hinzu
                potentialSkills.addAll(q.getFollowupSkills());
            });
        }

        // Entferne Duplikate und bereits absolvierte Schulungen
        return potentialSkills.stream()
                .distinct()
                .filter(skill -> !completedTrainings.contains(skill))
                .collect(Collectors.toList());
    }

    // --- Getter-Methoden für die UI ---

    public List<String> getCompletedTrainings() {
        return completedTrainings;
    }

    public List<String> getPotentialTrainings() {
        return potentialTrainings;
    }

    public List<String> getOpenTrainings() {
        return openTrainings;
    }
}

