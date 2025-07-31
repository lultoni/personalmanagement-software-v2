package model.db;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Mitarbeiter Klasse.
 * Spiegelt die Datenbankobjekte wider, ohne direkte Abhängigkeiten zu Datenbank-Zugriffsobjekten (DAOs)
 * oder Geschäftslogik-Managern.
 *
 * @author Elias Glauert
 * @version 1.6 (Hinzufügung von isManager)
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

    // NEUE FELDER HIER
    private boolean itAdmin;
    private boolean hr;
    private boolean hrHead;
    private boolean isManager; // NEUE VARIABLE

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
     * @param itAdmin            Ist der Mitarbeiter IT-Admin?
     * @param hr                 Ist der Mitarbeiter in der HR-Abteilung?
     * @param hrHead             Ist der Mitarbeiter HR-Leiter?
     * @param isManager          Ist der Mitarbeiter ein Manager?
     * @author Elias Glauert
     */
    public Employee(String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) { // NEUE PARAMETER
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
        this.itAdmin = itAdmin;
        this.hr = hr;
        this.hrHead = hrHead;
        this.isManager = isManager; // NEUES FELD INITIALISIEREN
    }

    /**
     * Konstruktor für das Laden eines Employee-Objekts AUS der Datenbank,
     * bei dem die ID bereits bekannt ist.
     *
     * @param id               Die eindeutige ID des Mitarbeiters aus der Datenbank.
     * @param username         Benutzername des Mitarbeiters.
     * @param password         Passwort des Mitarbeiters.
     * @param permissionString Berechtigungs-String (z.B. "admin,hr").
     * @param firstName        Vorname.
     * @param lastName         Nachname.
     * @param email            E-Mail-Adresse.
     * @param phoneNumber      Telefonnummer.
     * @param dateOfBirth      Geburtsdatum.
     * @param address          Adresse.
     * @param gender           Geschlecht ('M' oder 'F').
     * @param hireDate         Einstellungsdatum.
     * @param employmentStatus Beschäftigungsstatus (z.B. "Active", "On Leave").
     * @param departmentId     ID der Abteilung.
     * @param teamId           ID des Teams (kann null sein, wenn kein Team zugewiesen).
     * @param roleId           ID der Rolle.
     * @param qualifications   Qualifikationen als JSON-String.
     * @param completedTrainings Abgeschlossene Trainings als JSON-String.
     * @param managerId        ID des Managers (kann null sein, wenn kein Manager zugewiesen).
     * @param itAdmin          Ist der Mitarbeiter IT-Admin?
     * @param hr               Ist der Mitarbeiter in der HR-Abteilung?
     * @param hrHead           Ist der Mitarbeiter HR-Leiter?
     * @param isManager        Ist der Mitarbeiter ein Manager?
     */
    public Employee(int id, String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) { // NEUE PARAMETER
        this(username, password, permissionString, firstName, lastName, email, phoneNumber, dateOfBirth, address,
                gender, hireDate, employmentStatus, departmentId, teamId, roleId, qualifications, completedTrainings, managerId,
                itAdmin, hr, hrHead, isManager); // WEITERGABE DER NEUEN PARAMETER
        this.id = id; // Setze die ID, die aus der DB kam
    }


    // --- Getter und Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    // GETTER UND SETTER FÜR NEUE BOOLEAN-FELDER
    public boolean isItAdmin() {
        return itAdmin;
    }

    public void setItAdmin(boolean itAdmin) {
        this.itAdmin = itAdmin;
    }

    public boolean isHr() {
        return hr;
    }

    public void setHr(boolean hr) {
        this.hr = hr;
    }

    public boolean isHrHead() {
        return hrHead;
    }

    public void setHrHead(boolean hrHead) {
        this.hrHead = hrHead;
    }

    public boolean isManager() { // NEUER GETTER
        return isManager;
    }

    public void setIsManager(boolean manager) { // NEUER SETTER
        isManager = manager;
    }

    // --- Standard Java Methoden (equals, hashCode, toString) ---
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
                ", managerId=" + managerId +
                ", itAdmin=" + itAdmin +
                ", hr=" + hr +
                ", hrHead=" + hrHead +
                ", isManager=" + isManager + // Hinzufügen
                ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        // Primärschlüssel-Gleichheit ist oft am wichtigsten für Datenbankobjekte
        return id == employee.id;
        /*
        // Für einen strengeren Vergleich aller relevanten Felder (wenn ID allein nicht reicht):
        return id == employee.id &&
               Objects.equals(username, employee.username) &&
               Objects.equals(password, employee.password) &&
               Objects.equals(permissionString, employee.permissionString) &&
               Objects.equals(firstName, employee.firstName) &&
               Objects.equals(lastName, employee.lastName) &&
               Objects.equals(email, employee.email) &&
               Objects.equals(phoneNumber, employee.phoneNumber) &&
               Objects.equals(dateOfBirth, employee.dateOfBirth) &&
               Objects.equals(address, employee.address) &&
               gender == employee.gender &&
               Objects.equals(hireDate, employee.hireDate) &&
               Objects.equals(employmentStatus, employee.employmentStatus) &&
               Objects.equals(departmentId, employee.departmentId) &&
               Objects.equals(teamId, employee.teamId) &&
               Objects.equals(roleId, employee.roleId) &&
               Objects.equals(qualifications, employee.qualifications) &&
               Objects.equals(completedTrainings, employee.completedTrainings) &&
               Objects.equals(managerId, employee.managerId) &&
               itAdmin == employee.itAdmin &&
               hr == employee.hr &&
               hrHead == employee.hrHead &&
               isManager == employee.isManager; // Hinzufügen
        */
    }

    @Override
    public int hashCode() {
        // Hinzufügen der neuen Felder zum hashCode
        return Objects.hash(id, username, password, permissionString, firstName, lastName, email, phoneNumber,
                dateOfBirth, address, gender, hireDate, employmentStatus, departmentId, teamId,
                roleId, qualifications, completedTrainings, managerId, itAdmin, hr, hrHead, isManager);
    }

    // --- Geschäftslogik-Methoden (TODOs bleiben bestehen) ---
    public boolean isHR() {
        return hr;
    }

    public boolean isAdmin() {
        return itAdmin;
    }

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