package model.db;

import java.util.*;

/**
 * Mitarbeiter-Objekt, welches alle Daten eines Mitarbeiters enthält.
 *
 * @author Elias Glauert, Dorian Gläske
 * @version 1.4
 * @since 2025-07-04
 */
public class Employee {

    private int id;
    private String username;
    private String password;
    private String permissionString;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth; // Geändert von LocalDate
    private String address;
    private char gender;
    private Date hireDate; // Geändert von LocalDate
    private String employmentStatus;
    private String departmentId;
    private String teamId;
    private String roleId;
    private String qualifications;
    private String completedTrainings;
    private Integer managerId;
    private boolean itAdmin;
    private boolean hr;
    private boolean hrHead;
    private boolean isManager;

    // Standardkonstruktor (optional, aber oft nützlich)
    public Employee() {
    }

    /**
     * Konstruktor für das Employee-Objekt ohne ID (für neue Mitarbeiter vor dem Speichern in der DB).
     * Die ID wird von der Datenbank generiert.
     *
     * @author Elias Glauert, Dorian Gläske
     */
    public Employee(String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) {
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
        this.isManager = isManager;
    }

    /**
     * Vollständiger Konstruktor für das Employee-Objekt (inkl. ID, z.B. beim Laden aus der DB).
     *
     * @author Elias Glauert, Dorian Gläske
     */
    public Employee(int id, String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) {
        this(username, password, permissionString, firstName, lastName, email, phoneNumber,
                dateOfBirth, address, gender, hireDate, employmentStatus, departmentId,
                teamId, roleId, qualifications, completedTrainings, managerId,
                itAdmin, hr, hrHead, isManager);
        this.id = id;
    }

    // --- Getter und Setter ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() { // Geändert von LocalDate
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) { // Geändert von LocalDate
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public Date getHireDate() { // Geändert von LocalDate
        return hireDate;
    }

    public void setHireDate(Date hireDate) { // Geändert von LocalDate
        this.hireDate = hireDate;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getCompletedTrainings() {
        return completedTrainings;
    }

    public void setCompletedTrainings(String completedTrainings) {
        this.completedTrainings = completedTrainings;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

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

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && gender == employee.gender && itAdmin == employee.itAdmin && hr == employee.hr && hrHead == employee.hrHead && isManager == employee.isManager && Objects.equals(username, employee.username) && Objects.equals(password, employee.password) && Objects.equals(permissionString, employee.permissionString) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(email, employee.email) && Objects.equals(phoneNumber, employee.phoneNumber) && Objects.equals(dateOfBirth, employee.dateOfBirth) && Objects.equals(address, employee.address) && Objects.equals(hireDate, employee.hireDate) && Objects.equals(employmentStatus, employee.employmentStatus) && Objects.equals(departmentId, employee.departmentId) && Objects.equals(teamId, employee.teamId) && Objects.equals(roleId, employee.roleId) && Objects.equals(qualifications, employee.qualifications) && Objects.equals(completedTrainings, employee.completedTrainings) && Objects.equals(managerId, employee.managerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, permissionString, firstName, lastName, email, phoneNumber, dateOfBirth, address, gender, hireDate, employmentStatus, departmentId, teamId, roleId, qualifications, completedTrainings, managerId, itAdmin, hr, hrHead, isManager);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", isManager=" + isManager +
                '}';
    }

    public List<String> getCompletedTrainingIds() {
        return Collections.singletonList(qualifications);
    }
}
