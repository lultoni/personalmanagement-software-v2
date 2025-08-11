package core;

import db.dao.EmployeeDao;
import db.dao.RoleDao;
import model.db.Employee;
import model.json.Role;
import util.EmployeeCreationService;
import db.DatabaseManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Der `EmployeeManager` ist die zentrale Klasse für die Verwaltung aller mitarbeiterbezogenen Vorgänge.
 * Er kümmert sich um das Abrufen, Erstellen, Aktualisieren und Löschen von Mitarbeiterdaten
 * und fungiert als Vermittler zwischen der Geschäftslogik der Anwendung und der Datenbank.
 * @author Dorian Gläske, Elias Glauert, joshuasperber
 * @version 1.7 (Added getRoleById and findById)
 * @since 2025-08-04
 */
public class EmployeeManager {

    private ArrayList<Employee> employees;
    private EmployeeDao employeeDao;
    private DatabaseManager databaseManager;

    private RoleDao roleDao;
    private Map<String, Role> roleMap;

    /**
     * Konstruktor für den EmployeeManager.
     * Initialisiert die Datenzugriffsobjekte und lokalen Datenstrukturen.
     *
     * @param employeeDao Das DAO für die Datenbankoperationen der Mitarbeiter.
     * @param dbManager Die allgemeine DatabaseManager-Instanz.
     * @author Elias Glauert, joshuasperber
     */
    public EmployeeManager(EmployeeDao employeeDao, DatabaseManager dbManager) {
        this.employeeDao = employeeDao;
        this.roleDao = roleDao;
        this.databaseManager = dbManager;
        this.employees = new ArrayList<>();
        this.roleMap = new HashMap<>();
    }

    public void setUpEmployees() {
        employees.clear();
        employees.addAll(employeeDao.getAllEmployeesFromDb());
        System.out.println(" ~ db ~ all employees in EmployeeManager:");
        for (Employee employee : employees) System.out.println("   | " + employee.toString());
    }

    /**
     * lädt alle Rollen aus der Datenabnk in die letzte locale map für eine schnellere Suche
     * hilft dabei, Rollen-ids schnell auf Role objekten abzubilden, das die datenbank wiederholt abgefrgt werden muss
     */
    public void setUpRoles() {
        roleMap.clear();
        List<Role> rolesFromDb = roleDao.getAllRolesFromDb();
        for (Role role : rolesFromDb) {
            roleMap.put(role.getroleId(), role);
        }
    }

    public void saveEmployeesToTxt(String fileName) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees available to save.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Employee employee : employees) {
                writer.write(employee.toString());
                writer.newLine();
            }
            System.out.println("Employee data successfully saved to '" + fileName + "'.");
        } catch (IOException e) {
            System.err.println("Error saving employee data to file '" + fileName + "': " + e.getMessage());
        }
    }

    public void create100Employee() throws IOException {
        EmployeeCreationService employeeCreationService = new EmployeeCreationService(databaseManager, this, employeeDao);
        employeeCreationService.generate_x_Employees(100);
        setUpEmployees();
    }

    /**
     * Creates a new employee, adds them to the database and the local list.
     *
     * @param username           Username of the employee.
     * @param password           Password of the employee.
     * @param permissionString   Permission string (e.g., "admin,hr").
     * @param firstName          First name.
     * @param lastName           Last name.
     * @param email              Email address.
     * @param phoneNumber        Phone number.
     * @param dateOfBirth        Date of birth.
     * @param address            Address.
     * @param gender             Gender ('M' or 'F').
     * @param hireDate           Hire date.
     * @param employmentStatus   Employment status (e.g., "Active", "On Leave").
     * @param departmentId       ID of the department.
     * @param teamId             ID of the team (can be null if no team is assigned).
     * @param roleId             ID of the role.
     * @param qualifications     Qualifications as a JSON string.
     * @param completedTrainings Completed trainings as a JSON string.
     * @param managerId          ID of the manager (can be null if no manager is assigned).
     * @param itAdmin            Is the employee an IT admin?
     * @param hr                 Is the employee in the HR department?
     * @param hrHead             Is the employee the HR head?
     * @param isManager          Is the employee a manager?
     */
    public void addEmployee(String username, String password, String permissionString, String firstName,
                            String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                            char gender, Date hireDate, String employmentStatus, String departmentId,
                            String teamId, String roleId, String qualifications, String completedTrainings,
                            Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) {

        Employee newEmployee = new Employee(username, password, permissionString, firstName, lastName, email, phoneNumber,
                dateOfBirth, address, gender, hireDate, employmentStatus, departmentId, teamId, roleId,
                qualifications, completedTrainings, managerId, itAdmin, hr, hrHead, isManager);

        employeeDao.addEmployeeToDb(newEmployee);
        setUpEmployees();
    }

    /**
     * Entfert einen Mitarebiter aus der Datenabank und aktualisiert die loakle Map.
     * @param id die ID des zu entfernenden Mitarbeiter
     */
    public void removeEmployee(int id) {
        employeeDao.removeEmployee(id);
        setUpEmployees();
    }

    /**
     * Sucht nach Mitarbeitern, absierent auf einer Liste von Feldern und deren entsprechenden Inhalten
     * @param fields eine Liste von Feldnamen ( Firstname, lastName..)
     * @param contents Eine Liste von Inhalts-Strings, die mit den Feldern abgeglichen werden sollen
     * @return Eine Liste von Employee-Objekten,
     * @author joshuasperber
     */
    public List<Employee> findEmployees(List<String> fields, List<String> contents) {
        setUpEmployees();

        List<Employee> matchingEmployees = new ArrayList<>();

        if (fields.size() != contents.size()) {
            throw new IllegalArgumentException("Fields and contents lists must be of the same size.");
        }

        for (Employee employee : employees) {
            boolean match = true;
            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                String content = contents.get(i);

                String fieldValue = getValueOfField(employee, field);
                if (!Objects.equals(fieldValue, content)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                matchingEmployees.add(employee);
            }
        }
        return matchingEmployees;
    }

    /**
     * Sucht nach einem Mitarbeiter anhand seiner String-ID.
     * Dies ist eine Komfortmethode, die die String-ID in einen Integer umwandelt und die bestehende Methode aufruft.
     *
     * @param id Die ID des zu suchenden Mitarbeiters als String.
     * @return Das Employee-Objekt oder null, wenn kein Mitarbeiter mit dieser ID gefunden wird oder die ID ungültig ist.
     * @author joshuasperber
     */
    public Employee findById(String id) {
        try {
            int intId = Integer.parseInt(id);
            return getEmployeeById(intId);
        } catch (NumberFormatException e) {
            return null; // ID is not a valid number
        }
    }

    /**
     * Sucht nach einer Rolle anhand ihrer ID.
     * Diese Methode ruft ein Role-Objekt aus der lokalen roleMap ab.
     *
     * @param roleId Die ID der zu suchenden Rolle.
     * @return Das Role-Objekt oder null, wenn keine Rolle mit dieser ID gefunden wird.
     * @author joshuasperber
     */

    public Role getRoleById(String roleId) {
        return roleMap.get(roleId);
    }

    public Employee getEmployeeById(int id) {
        setUpEmployees();

        System.out.println("EmployeeManager.employees.size() = " + employees.size());
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Sucht nach einem Mitarbeiter anhand seiner eindeutigen Integer-ID.
     *
     * @param fieldName Die Integer-ID des Mitarbeiters.
     * @return Das Employee-Objekt oder null, wenn kein Mitarbeiter mit dieser ID gefunden wird.
     * @author Elias Glauert
     */
    private String getValueOfField(Employee employee, String fieldName) {
        switch (fieldName) {
            case "id" -> {
                return String.valueOf(employee.getId());
            }
            case "username" -> {
                return employee.getUsername();
            }
            case "password" -> {
                return employee.getPassword();
            }
            case "permissionString" -> {
                return employee.getPermissionString();
            }
            case "firstName" -> {
                return employee.getFirstName();
            }
            case "lastName" -> {
                return employee.getLastName();
            }
            case "email" -> {
                return employee.getEmail();
            }
            case "phoneNumber" -> {
                return employee.getPhoneNumber();
            }
            case "dateOfBirth" -> {
                return employee.getDateOfBirth() != null ? employee.getDateOfBirth().toString() : null;
            }
            case "address" -> {
                return employee.getAddress();
            }
            case "gender" -> {
                return String.valueOf(employee.getGender());
            }
            case "hireDate" -> {
                return employee.getHireDate() != null ? employee.getHireDate().toString() : null;
            }
            case "employmentStatus" -> {
                return employee.getEmploymentStatus();
            }
            case "departmentId" -> {
                return employee.getDepartmentId();
            }
            case "teamId" -> {
                return employee.getTeamId();
            }
            case "roleId" -> {
                return employee.getRoleId();
            }
            case "qualifications" -> {
                return employee.getQualifications();
            }
            case "completedTrainings" -> {
                return employee.getCompletedTrainings();
            }
            case "managerId" -> {
                return employee.getManagerId() != null ? String.valueOf(employee.getManagerId()) : null;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Setzt die Instanz des EmployeeDao.
     *
     * @param employeeDao Der neue EmployeeDao.
     * @author joshuasperber
     */

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public boolean hasEmployeesGenerated() {
        setUpEmployees();
        return employees != null && !employees.isEmpty();
    }

    public List<Employee> findAll() {
        setUpEmployees();
        return new ArrayList<>(this.employees);
    }

    public void updateEmployee(Employee updatedEmployee) throws Exception {
        if (employeeDao != null) {
            employeeDao.updateEmployee(updatedEmployee);
            setUpEmployees();
        } else {
            for (int i = 0; i < employees.size(); i++) {
                if (employees.get(i).getId() == updatedEmployee.getId()) {
                    employees.set(i, updatedEmployee);
                    return;
                }
            }
            throw new Exception("Employee not found");
        }
    }
    public void getEmployeeQualifikation(Employee employee) {
        employee.getQualifications();
    }
}
