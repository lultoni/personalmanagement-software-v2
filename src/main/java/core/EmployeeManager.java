package core;

import db.dao.EmployeeDao;
import model.db.Employee;
import util.EmployeeCreationService;
import db.DatabaseManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date; // Geändert von java.time.LocalDate
import java.util.Objects;
import java.util.List;
// import java.time.LocalDate; // Entfernt

/**
 * Mitarbeiter Verwaltung Klasse.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 1.6 (Datumstypen von LocalDate zu Date geändert)
 * @since 2025-08-04
 */
public class EmployeeManager {

    private ArrayList<Employee> employees;
    private EmployeeDao employeeDao;
    private DatabaseManager databaseManager;

    public EmployeeManager(EmployeeDao employeeDao, DatabaseManager dbManager) {
        this.employeeDao = employeeDao;
        this.databaseManager = dbManager;
        this.employees = new ArrayList<>();
        setUpEmployees();
    }

    public void setUpEmployees() {
        employees.clear();
        employees.addAll(employeeDao.getAllEmployeesFromDb());
        System.out.println(" ~ db ~ all employees in EmployeeManager:");
        for (Employee employee : employees) System.out.println("   | " + employee.toString());
    }

    public void saveEmployeesToTxt(String fileName) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("Keine Mitarbeiter zum Speichern vorhanden.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Employee employee : employees) {
                writer.write(employee.toString());
                writer.newLine();
            }
            System.out.println("Mitarbeiterdaten erfolgreich in '" + fileName + "' gespeichert.");
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Mitarbeiterdaten in Datei '" + fileName + "': " + e.getMessage());
        }
    }

    public void create100Employee() throws IOException {
        EmployeeCreationService employeeCreationService = new EmployeeCreationService(databaseManager, this, employeeDao);
        employeeCreationService.generate_x_Employees(100);
        setUpEmployees();
    }

    /**
     * Erstellt einen neuen Mitarbeiter, fügt ihn der Datenbank und der lokalen Liste hinzu.
     *
     * @param username           Benutzername des Mitarbeiters.
     * @param password           Passwort des Mitarbeiters.
     * @param permissionString   Berechtigungs-String (z.B. "admin,hr").
     * @param firstName          Vorname.
     * @param lastName           Nachname.
     * @param email              E-Mail-Adresse.
     * @param phoneNumber        Telefonnummer.
     * @param dateOfBirth        Geburtsdatum. // Geändert von LocalDate
     * @param address            Adresse.
     * @param gender             Geschlecht ('M' oder 'F').
     * @param hireDate           Einstellungsdatum. // Geändert von LocalDate
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
     * @author Elias Glauert, Dorian Gläske
     */
    public void addEmployee(String username, String password, String permissionString, String firstName,
                            String lastName, String email, String phoneNumber, Date dateOfBirth, String address, // Geändert
                            char gender, Date hireDate, String employmentStatus, String departmentId, // Geändert
                            String teamId, String roleId, String qualifications, String completedTrainings,
                            Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) {

        Employee newEmployee = new Employee(username, password, permissionString, firstName, lastName, email, phoneNumber,
                dateOfBirth, address, gender, hireDate, employmentStatus, departmentId, teamId, roleId,
                qualifications, completedTrainings, managerId, itAdmin, hr, hrHead, isManager);

        employeeDao.addEmployeeToDb(newEmployee);
        setUpEmployees();
    }

    public void removeEmployee(int id) {
        employeeDao.removeEmployee(id);
        setUpEmployees();
    }

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
                // ********************************************************************
                // KORREKTUR: Formatierung von Date zu String
                // ********************************************************************
                return employee.getDateOfBirth() != null ? employee.getDateOfBirth().toString() : null;
                // Oder mit SimpleDateFormat für spezifisches Format:
                // return employee.getDateOfBirth() != null ? new SimpleDateFormat("yyyy-MM-dd").format(employee.getDateOfBirth()) : null;
                // ********************************************************************
            }
            case "address" -> {
                return employee.getAddress();
            }
            case "gender" -> {
                return String.valueOf(employee.getGender());
            }
            case "hireDate" -> {
                // ********************************************************************
                // KORREKTUR: Formatierung von Date zu String
                // ********************************************************************
                return employee.getHireDate() != null ? employee.getHireDate().toString() : null;
                // Oder mit SimpleDateFormat für spezifisches Format:
                // return employee.getHireDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(employee.getHireDate()) : null;
                // ********************************************************************
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

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
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
            throw new Exception("Employee nicht gefunden");
        }
    }
}
