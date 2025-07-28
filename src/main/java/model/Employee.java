package model;

import core.EmployeeManager;
import db.dao.EmployeeDao;

import java.util.ArrayList;
import java.util.Date;

/**
 * Mitarbeiter Klasse.
 * Spiegelt die Datenbankobjekte wieder mit erweiterter Funktionalität.
 *
 * @author Elias Glauert
 * @version 1.2
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
    private EmployeeManager employeeManager;

    /**
     * Konstruktor von der Employee Klasse.
     * @author Elias Glauert
     */
    public Employee(boolean addEmployeeToDb, String username, String password, String permissionString, String firstName,
                    String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                    char gender, Date hireDate, String employmentStatus, String departmentId,
                    String teamId, String roleId, String qualifications, String completedTrainings,
                    int managerId, EmployeeManager employeeManager, EmployeeDao employeeDao) {
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
        this.employeeManager = employeeManager;

        if (addEmployeeToDb) employeeDao.addEmployeeToDb(this);
        id = Integer.parseInt(employeeDao.fetchFieldValue("id", this));

        if (addEmployeeToDb) employeeManager.addEmployee(this);
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
                ")";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (object.getClass() != this.getClass()) return false;
        Employee employee = (Employee) object;
        if (employee.getId() != getId()) return false;
        if (employee.getUsername() != getUsername()) return false;
        if (employee.getPassword() != getPassword()) return false;
        if (employee.getPermissionString() != getPermissionString()) return false;
        if (employee.getFirstName() != getFirstName()) return false;
        if (employee.getLastName() != getLastName()) return false;
        if (employee.getEmail() != getEmail()) return false;
        if (employee.getPhoneNumber() != getPhoneNumber()) return false;
        if (employee.getDateOfBirth() != getDateOfBirth()) return false;
        if (employee.getAddress() != getAddress()) return false;
        if (employee.getGender() != getGender()) return false;
        if (employee.getHireDate() != getHireDate()) return false;
        if (employee.getEmploymentStatus() != getEmploymentStatus()) return false;
        if (employee.getDepartmentId() != getDepartmentId()) return false;
        if (employee.getTeamId() != getTeamId()) return false;
        if (employee.getRoleId() != getRoleId()) return false;
        if (employee.getQualifications() != getQualifications()) return false;
        if (employee.getCompletedTrainings() != getCompletedTrainings()) return false;
        if (employee.getManagerId() != getManagerId()) return false;
        return true;
    }

    public boolean isHR() {
        // TODO create this function after the company structure is made
        return false;
    }

    public boolean isAdmin() {
        // TODO create this function after the company structure is made
        return false;
    }

    /**
     * Gibt den Manager des Mitarbeiters zurück.
     * @return Objekt des Typs Employee, welches den Manager beinhaltet
     * @author Elias Glauert
     */
    public Employee getManager() {
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        fields.add("id");
        contents.add(String.valueOf(getManagerId()));
        return employeeManager.findEmployees(fields, contents).getFirst();
    }
}