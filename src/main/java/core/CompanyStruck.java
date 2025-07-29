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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Für sicherere Rückgaben bei find-Methoden

/**
 * Verwaltet die eingelesenen Unternehmensstrukturdaten (Company, Departments, Roles, Teams, Qualifications).
 * Diese Klasse lädt die Daten einmalig über den JsonParser und bietet Methoden zum Zugriff und zur einfachen Verwaltung.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-29
 */
public class CompanyStructureManager {

    private Company company;
    private Map<String, Department> departmentMap;
    private Map<String, Qualification> qualificationMap;
    private Map<String, Role> roleMap;
    private Map<String, Team> teamMap;

    // Verwende einen privaten Konstruktor und eine statische Instanz,
    // um ein Singleton-Muster zu implementieren oder die Initialisierung zu steuern.
    // Alternativ könnte dies auch ein normales, über Dependency Injection verwaltetes Bean sein.
    // Für diesen Anwendungsfall (Daten einmalig laden) ist ein Singleton oft passend.
    private static CompanyStructureManager instance;

    /**
     * Privater Konstruktor, um die Instanziierung von außen zu verhindern.
     * Lädt alle Daten beim ersten Erzeugen der Instanz.
     * @throws IOException Wenn beim Laden der JSON-Dateien ein Fehler auftritt.
     */
    private CompanyStructureManager() throws IOException {
        loadAllData();
    }

    /**
     * Gibt die Singleton-Instanz des CompanyStructureManager zurück.
     * @return Die einzige Instanz des CompanyStructureManager.
     * @throws IOException Wenn beim ersten Laden der Daten ein Fehler auftritt.
     */
    public static CompanyStructureManager getInstance() throws IOException {
        if (instance == null) {
            instance = new CompanyStructureManager();
        }
        return instance;
    }

    /**
     * Lädt alle Unternehmensstrukturdaten über den JsonParser.
     * Diese Methode wird beim ersten Aufruf von getInstance() ausgeführt.
     * @throws IOException Wenn Fehler beim Lesen der JSON-Dateien auftreten.
     */
    private void loadAllData() throws IOException {
        System.out.println("Lade Unternehmensstrukturdaten...");
        this.company = JsonParser.getCompany();
        this.departmentMap = JsonParser.getDepartmentMap(); // Annahme: getDepartmentMap() gibt die Map zurück
        this.qualificationMap = JsonParser.getQualificationMap(); // Annahme: getQualificationMap() gibt die Map zurück
        this.roleMap = JsonParser.getRoleMap(); // Annahme: getRoleMap() gibt die Map zurück
        this.teamMap = JsonParser.getTeamMap(); // Annahme: getTeamMap() gibt die Map zurück
        System.out.println("Unternehmensstrukturdaten erfolgreich geladen.");
    }

    // --- Getter-Methoden für die geladenen Daten ---

    /**
     * Gibt das geladene Company-Objekt zurück.
     * @return Das Company-Objekt.
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Findet ein Department anhand seiner ID.
     * @param departmentId Die ID des Departments.
     * @return Ein Optional, das das Department enthält, falls gefunden, ansonsten leer.
     */
    public Optional<Department> findDepartmentById(String departmentId) {
        return Optional.ofNullable(departmentMap.get(departmentId));
    }

    /**
     * Gibt eine unveränderliche Sammlung aller geladenen Departments zurück.
     * @return Eine Collection von Department-Objekten.
     */
    public Collection<Department> getAllDepartments() {
        return Collections.unmodifiableCollection(departmentMap.values());
    }

    /**
     * Findet eine Qualification anhand ihrer Role-ID.
     * @param roleId Die Role-ID, die der Qualifikation zugeordnet ist.
     * @return Ein Optional, das die Qualification enthält, falls gefunden, ansonsten leer.
     */
    public Optional<Qualification> findQualificationByRoleId(String roleId) {
        return Optional.ofNullable(qualificationMap.get(roleId));
    }

    /**
     * Gibt eine unveränderliche Sammlung aller geladenen Qualifikationen zurück.
     * @return Eine Collection von Qualification-Objekten.
     */
    public Collection<Qualification> getAllQualifications() {
        return Collections.unmodifiableCollection(qualificationMap.values());
    }

    /**
     * Findet eine Role anhand ihrer ID.
     * @param roleId Die ID der Role.
     * @return Ein Optional, das die Role enthält, falls gefunden, ansonsten leer.
     */
    public Optional<Role> findRoleById(String roleId) {
        return Optional.ofNullable(roleMap.get(roleId));
    }

    /**
     * Gibt eine unveränderliche Sammlung aller geladenen Roles zurück.
     * @return Eine Collection von Role-Objekten.
     */
    public Collection<Role> getAllRoles() {
        return Collections.unmodifiableCollection(roleMap.values());
    }

    /**
     * Findet ein Team anhand seiner ID.
     * @param teamId Die ID des Teams.
     * @return Ein Optional, das das Team enthält, falls gefunden, ansonsten leer.
     */
    public Optional<Team> findTeamById(String teamId) {
        return Optional.ofNullable(teamMap.get(teamId));
    }

    /**
     * Gibt eine unveränderliche Sammlung aller geladenen Teams zurück.
     * @return Eine Collection von Team-Objekten.
     */
    public Collection<Team> getAllTeams() {
        return Collections.unmodifiableCollection(teamMap.values());
    }

    // --- Beispiel für eine Bearbeitungslogik (z.B. Department umbenennen) ---
    // Beachte: Diese Änderung ist nur im Speicher, nicht persistent in der JSON-Datei!
    /**
     * Ändert den Namen eines Departments.
     * @param departmentId Die ID des zu ändernden Departments.
     * @param newName Der neue Name.
     * @return true, wenn das Department gefunden und umbenannt wurde, sonst false.
     */
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

    // Füge hier ähnliche Methoden für die Bearbeitung anderer Objekte hinzu, falls benötigt.
    // Denk daran, dass diese Änderungen nur im Arbeitsspeicher sind!

    /**
     * Gibt eine Zusammenfassung der geladenen Daten aus.
     */
    public void printLoadedDataSummary() {
        System.out.println("\n--- Geladene Unternehmensstruktur-Zusammenfassung ---");
        System.out.println("Firma: " + (company != null ? company.getCompanyName() : "N/A"));
        System.out.println("Anzahl Departments: " + departmentMap.size());
        System.out.println("Anzahl Roles: " + roleMap.size());
        System.out.println("Anzahl Teams: " + teamMap.size());
        System.out.println("Anzahl Qualifikationen: " + qualificationMap.size());
        System.out.println("----------------------------------------------");
    }
}
