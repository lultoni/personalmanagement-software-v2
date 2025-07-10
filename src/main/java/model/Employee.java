package model;

import db.DatabaseManager;
import db.dao.EmployeeDao;

import java.util.Date;

/**
 * Mitarbeiter Klasse.
 * Spiegelt die Datenbankobjekte wieder mit erweiterter Funktionalität.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-09
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
    private Date dateOfBirth;
    private String address;
    private char gender;
    private Date hireDate;
    private String employmentStatus;
    private String departmentId;
    private String teamId;
    private String roleId;
    private String qualifications;
    private String completedTrainings;
    private int managerId;

    private EmployeeDao employeeDao;

    /**
     * Konstruktor von der Employee Klasse.
     * @param id
     * @param username
     * @param password
     * @param permissionString
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param dateOfBirth
     * @param address
     * @param gender
     * @param hireDate
     * @param employmentStatus
     * @param departmentId
     * @param teamId
     * @param roleId
     * @param qualifications
     * @param completedTrainings
     * @param managerId
     * @author Elias Glauert
     */
    public Employee(String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    int managerId, DatabaseManager dbManager) {
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

        employeeDao = new EmployeeDao(dbManager, this);
        employeeDao.addEmployeeToDb(this);
        id = Integer.valueOf(employeeDao.fetchFieldValue("id"));
    }

    // Getters and Setters
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
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

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
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

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() { // TODO maybe mehr daten noch hinzufügen
        return "Employee(" +
                "id=" + id +
                ", username='" + username + "'" +
                ", firstName='" + firstName + "'" +
                ", lastName='" + lastName + "'" +
                ", email='" + email + "'" +
                ", phoneNumber='" + phoneNumber + "'" +
                ", departmentId='" + departmentId + "'" +
                ", roleId='" + roleId + "'" +
                ")";
    }
}