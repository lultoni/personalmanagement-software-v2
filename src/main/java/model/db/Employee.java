package model.db;

import db.dao.EmployeeDao;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Mitarbeiter Klasse.
 * Spiegelt die Datenbankobjekte wider, ohne direkte Abhängigkeiten zu Datenbank-Zugriffsobjekten (DAOs)
 * oder Geschäftslogik-Managern.
 *
 * @author Elias Glauert
 * @version 1.5 (Anpassung an die neue Generierungslogik und Abhängigkeitsentkopplung)
 * @since 2025-07-30
 */
public class Employee {

    private int id; // ID wird initial von der DB vergeben, wenn hinzugefügt
    private String username;
    private String password;
    private String permissionString;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String address;
    private char gender;
    private Date hireDate;
    private String employmentStatus;
    private String departmentId;
    private String teamId;
    private String roleId;
    private String qualifications;      // JSON-String
    private String completedTrainings;  // JSON-String
    private Integer managerId;          // Nutze Integer, damit es auch 'null' sein kann, falls kein Manager zugewiesen

    /**
     * Konstruktor für die Erstellung eines NEUEN Employee-Objekts,
     * dessen ID noch von der Datenbank vergeben wird.
     *
     * @param username           Benutzername des Mitarbeiters.
     * @param password           Passwort des Mitarbeiters.
     * @param permissionString   Berechtigungs-String (z.B. "admin,hr").
     * @param firstName          Vorname.
     * @param lastName           Nachname.
     * @param email              E-Mail-Adresse.
     * @param phoneNumber        Telefonnummer.
     * @param dateOfBirth        Geburtsdatum.
     * @param address            Adresse.
     * @param gender             Geschlecht ('M' oder 'F').
     * @param hireDate           Einstellungsdatum.
     * @param employmentStatus   Beschäftigungsstatus (z.B. "Active", "On Leave").
     * @param departmentId       ID der Abteilung.
     * @param teamId             ID des Teams (kann null sein, wenn kein Team zugewiesen).
     * @param roleId             ID der Rolle.
     * @param qualifications     Qualifikationen als JSON-String.
     * @param completedTrainings Abgeschlossene Trainings als JSON-String.
     * @param managerId          ID des Managers (kann null sein, wenn kein Manager zugewiesen).
     * @author Elias Glauert
     */
    public Employee(String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    Integer managerId) {
        // Die 'id' wird hier NICHT initialisiert. Sie wird NACH dem DB-Insert über setId() gesetzt.
        this.username = username;
        this.password = password;
        this.permissionString = permissionString;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
        this.hireDate = hireDate;
        this.employmentStatus = employmentStatus;
        this.departmentId = departmentId;
        this.teamId = teamId;
        this.roleId = roleId;
        this.qualifications = qualifications;
        this.completedTrainings = completedTrainings;
        this.managerId = managerId;
    }

    /**
     * Konstruktor für das Laden eines Employee-Objekts AUS der Datenbank,
     * bei dem die ID bereits bekannt ist.
     *
     * @param id                 Die eindeutige ID des Mitarbeiters aus der Datenbank.
     * @param username           Benutzername des Mitarbeiters.
     * @param password           Passwort des Mitarbeiters.
     * @param permissionString   Berechtigungs-String (z.B. "admin,hr").
     * @param firstName          Vorname.
     * @param lastName           Nachname.
     * @param email              E-Mail-Adresse.
     * @param phoneNumber        Telefonnummer.
     * @param dateOfBirth        Geburtsdatum.
     * @param address            Adresse.
     * @param gender             Geschlecht ('M' oder 'F').
     * @param hireDate           Einstellungsdatum.
     * @param employmentStatus   Beschäftigungsstatus (z.B. "Active", "On Leave").
     * @param departmentId       ID der Abteilung.
     * @param teamId             ID des Teams (kann null sein, wenn kein Team zugewiesen).
     * @param roleId             ID der Rolle.
     * @param qualifications     Qualifikationen als JSON-String.
     * @param completedTrainings Abgeschlossene Trainings als JSON-String.
     * @param managerId          ID des Managers (kann null sein, wenn kein Manager zugewiesen).
     * @param employeeDao
     */
    public Employee(int id, String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    Integer managerId, EmployeeDao employeeDao) {
        // Rufe den Hauptkonstruktor auf, um die Felder zu initialisieren
        this(username, password, permissionString, firstName, lastName, email, phoneNumber, dateOfBirth, address,
                gender, hireDate, employmentStatus, departmentId, teamId, roleId, qualifications, completedTrainings, managerId);
        this.id = id; // Setze die ID, die aus der DB kam
    }


    // --- Getter und Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // WICHTIG: Wird vom DAO nach DB-Insert gesetzt

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPermissionString() { return permissionString; }
    public void setPermissionString(String permissionString) { this.permissionString = permissionString; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public char getGender() { return gender; }
    public void setGender(char gender) { this.gender = gender; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public String getCompletedTrainings() { return completedTrainings; }
    public void setCompletedTrainings(String completedTrainings) { this.completedTrainings = completedTrainings; }

    public Integer getManagerId() { return managerId; } // Jetzt Integer
    public void setManagerId(Integer managerId) { this.managerId = managerId; } // Jetzt Integer

    // --- Standard Java Methoden ---
    @Override
    public String toString() {
        return "Employee(" +
                "id=" + id +
                ", username='" + username + "'" +
                ", firstName='" + firstName + "'" +
                ", lastName='" + lastName + "'" +
                ", email='" + email + "'" +
                ", phoneNumber='" + phoneNumber + "'" +
                ", departmentId='" + departmentId + "'" +
                ", roleId='" + roleId + "'" +
                ", managerId=" + managerId + // Füge managerId hinzu
                ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        // Primärschlüssel-Gleichheit ist oft am wichtigsten für Datenbankobjekte
        // Wenn die ID eindeutig ist und die primäre Identifikation, ist dieser Vergleich ausreichend.
        // Andernfalls, wenn alle Felder für Gleichheit wichtig sind, erweitere den Vergleich.
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        // Generiert einen Hash-Code basierend auf allen Feldern, die in equals() verwendet werden.
        // Wenn equals() nur auf der ID basiert, sollte hashCode() auch nur auf der ID basieren.
        // Hier basierend auf allen Feldern für einen robusteren Hash, passend zum kommentierten equals().
        return Objects.hash(id, username, password, permissionString, firstName, lastName, email, phoneNumber,
                dateOfBirth, address, gender, hireDate, employmentStatus, departmentId, teamId,
                roleId, qualifications, completedTrainings, managerId);
    }

    // --- Geschäftslogik-Methoden (TODOs bleiben bestehen) ---
    public boolean isHR() {
        // TODO: Implementiere diese Funktion, z.B. durch Überprüfung von permissionString oder roleId
        return false;
    }

    public boolean isAdmin() {
        // TODO: Implementiere diese Funktion, z.B. durch Überprüfung von permissionString
        return false;
    }

    /**
     * Gibt den Manager des Mitarbeiters zurück, indem der bereitgestellte EmployeeManager zur Suche verwendet wird.
     * Diese Methode sollte nur aufgerufen werden, wenn eine aktive Instanz von EmployeeManager verfügbar ist,
     * da das Employee-Objekt selbst keine Logik zum Suchen anderer Employees hat.
     *
     * @param employeeManager Der EmployeeManager, der für die Suche des Managers verwendet wird.
     * @return Objekt des Typs Employee, welches den Manager beinhaltet, oder null wenn kein Manager zugewiesen oder gefunden wurde.
     * @author Elias Glauert
     */
    public Employee getManager(core.EmployeeManager employeeManager) {
        if (employeeManager == null) {
            System.err.println("Fehler: EmployeeManager für getManager() ist null.");
            return null;
        }
        if (this.managerId != null && this.managerId > 0) {
            List<model.db.Employee> results = employeeManager.findEmployees(
                    new java.util.ArrayList<>(List.of("id")), // Feldname
                    new java.util.ArrayList<>(List.of(String.valueOf(this.managerId))) // Wert als String
            );
            return results.isEmpty() ? null : results.getFirst();
        }
        return null; // Kein Manager zugewiesen
    }
}