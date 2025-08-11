package core;

import model.json.Company;
import model.json.Department;
import model.json.Qualification;
import model.json.Role;
import model.json.Team;
import util.JsonParser; // Dein JsonParser
import java.io.IOException;
import java.util.Collection; // Für das Abrufen aller Objekte
import java.util.Collections; // Für Collections.unmodifiableCollection
import java.util.List;
import java.util.Map;
import java.util.Optional; // Für sicherere Rückgaben bei find-Methoden
import java.util.stream.Collectors;

/**
 * Verwaltet die eingelesenen Unternehmensstrukturdaten (Company, Departments, Roles, Teams, Qualifications).
 * Diese Klasse lädt die Daten einmalig über den JsonParser und bietet Methoden zum Zugriff und zur einfachen Verwaltung.
 *
 * @author Dorian Gläske, joshuasperber
 * @version 1.1
 * @since 2025-08-04
 */
public class CompanyStructureManager {

    private Company company;
    private Map<String, Department> departmentMap;
    private Map<String, Qualification> qualificationMap;
    private Map<String, Role> roleMap;
    private Map<String, Team> teamMap;

    private static CompanyStructureManager instance;

    private CompanyStructureManager() throws IOException {
        loadAllData();
    }

    public static CompanyStructureManager getInstance() throws IOException {
        if (instance == null) {
            instance = new CompanyStructureManager();
        }
        return instance;
    }

    private void loadAllData() throws IOException {
        System.out.println("Lade Unternehmensstrukturdaten...");
        this.company = JsonParser.getCompany();
        this.departmentMap = JsonParser.getDepartmentMap();
        this.qualificationMap = JsonParser.getQualificationMap();
        this.roleMap = JsonParser.getRoleMap();
        this.teamMap = JsonParser.getTeamMap();
        System.out.println("Unternehmensstrukturdaten erfolgreich geladen.");
    }

    // --- Getter-Methoden für die geladenen Daten ---

    public Company getCompany() {
        return company;
    }

    public Optional<Department> findDepartmentById(String departmentId) {
        return Optional.ofNullable(departmentMap.get(departmentId));
    }

    public Collection<Department> getAllDepartments() {
        return Collections.unmodifiableCollection(departmentMap.values());
    }

    /**
     * NEUE METHODE: Findet eine Qualifikation anhand ihrer ID.
     * @param qualificationId Die ID der Qualifikation.
     * @return Ein Optional, das die Qualifikation enthält, falls gefunden, ansonsten leer.
     */
    public Optional<Qualification> findQualificationById(Object qualificationId) {
        return Optional.ofNullable(qualificationMap.get(qualificationId));
    }

    // Die alte Methode "findQualificationByRoleId" war verwirrend. Ich habe sie umbenannt,
    // damit sie klarer ist und direkt auf die qualificationMap zugreift.
    // Die Methoden "getRequiredSkillsForQualification" und "getFollowUpSkillsForQualification"
    // bleiben gleich, wurden aber zur neuen Methode umgeleitet.
    public Optional<List<String>> getRequiredSkillsForQualification(String id) {
        // 1. Suche nach der Qualifikation anhand der übergebenen ID (könnte eine Qualifikations-ID sein)
        Optional<Qualification> optionalQualification = findQualificationById(id);

        // 2. Wenn keine Qualifikation gefunden wurde, versuche, eine Rolle mit dieser ID zu finden
        //    und rufe dann die zugehörigen Qualifikationen ab.
        if (optionalQualification.isEmpty()) {
            Optional<Role> optionalRole = findRoleById(id);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                // Die RoleId entspricht der Qualifikations-ID der Rolle
                optionalQualification = findQualificationById(role.getroleId());
            }
        }

        // 3. Wenn jetzt eine Qualifikation gefunden wurde, gib ihre RequiredSkills zurück
        if (optionalQualification.isPresent()) {
            Qualification qualification = optionalQualification.get();
            List<String> requiredSkills = qualification.getRequiredSkills();

            // Sicherstellen, dass die Liste existiert und nicht null ist
            if (requiredSkills != null) {
                return Optional.of(requiredSkills);
            }
        }

        // Wenn am Ende nichts gefunden wurde oder die Liste null ist, gib ein leeres Optional zurück
        return Optional.empty();
    }

    /**
     * Sucht eine Qualifikation anhand ihrer ID und gibt ein Optional zurück.
     * Falls die Qualifikation gefunden wird, extrahiert es mithilfe von .map()
     * die Liste der Folgefähigkeiten (Followup Skills) aus dem Qualifikation-Objekt.
     * Wenn die Qualifikation nicht existiert, wird ein leeres Optional zurückgegeben.
     * @param qualificationId anhand der QualificationId wird der benutzer ausfindit geamcht..
     * @return es werden alle Qualifications vorgeschlagen und dann nach Role und Team
     * @author joshuasperber
     */
    public Optional<List<String>> getFollowUpSkillsForQualification(String qualificationId) {
        return findQualificationById(qualificationId)
                .map(Qualification::getFollowupSkills);
    }

    public Collection<Qualification> getAllQualifications() {
        return Collections.unmodifiableCollection(qualificationMap.values());
    }

    public Optional<Role> findRoleById(String roleId) {
        return Optional.ofNullable(roleMap.get(roleId));
    }

    public Collection<Role> getAllRoles() {
        return Collections.unmodifiableCollection(roleMap.values());
    }

    public List<String> getAllRoleIds() {
        return this.getAllRoles().stream()
                .map(Role::getroleId)
                .collect(Collectors.toList());
    }

    public Optional<Team> findTeamById(String teamId) {
        return Optional.ofNullable(teamMap.get(teamId));
    }

    public Collection<Team> getAllTeams() {
        return Collections.unmodifiableCollection(teamMap.values());
    }

    public boolean updateDepartmentName(String departmentId, String newName) {
        return findDepartmentById(departmentId).map(department -> {
            department.setName(newName);
            System.out.println("Department '" + departmentId + "' wurde umbenannt zu '" + newName + "'.");
            return true;
        }).orElseGet(() -> {
            System.out.println("Department mit ID '" + departmentId + "' nicht gefunden.");
            return false;
        });
    }

    public void printLoadedDataSummary() {
        System.out.println("\n--- Geladene Unternehmensstruktur-Zusammenfassung ---");
        System.out.println("Firma: " + (company != null ? company.getName() : "N/A"));
        System.out.println("Anzahl Departments: " + departmentMap.size());
        System.out.println("Anzahl Roles: " + roleMap.size());
        System.out.println("Anzahl Teams: " + teamMap.size());
        System.out.println("Anzahl Qualifikationen: " + qualificationMap.size());
        System.out.println("----------------------------------------------");
    }
}