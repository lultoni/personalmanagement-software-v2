package core;

import db.dao.EmployeeDao;
import model.db.Employee;
import util.EmployeeCreationService;
import db.DatabaseManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.List;
import java.time.LocalDate;

/**
 * Mitarbeiter Verwaltung Klasse.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 1.3
 * @since 2025-08-04
 */
public class EmployeeManager {

    /**
     * Liste aller Mitarbeiter, welche existieren
     */
    private ArrayList<Employee> employees;
    private EmployeeDao employeeDao;
    private DatabaseManager databaseManager;

    /**
     * Konstruktor für die EmployeeManager Klasse.
     *
     * @author Elias Glauert
     */
    public EmployeeManager(EmployeeDao employeeDao, DatabaseManager dbManager) {
        this.employeeDao = employeeDao;
        this.databaseManager = dbManager;
        this.employees = new ArrayList<>();
    }

    /**
     * Adds all Employees from the DB to the employees ArrayList.
     *
     * @author Elias Glauert
     */
    public void setUpEmployees() {
        employees.clear(); // Sicherstellen, dass die Liste leer ist, bevor wir sie füllen
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
     * @author Elias Glauert, Dorian Gläske
     */
    public void addEmployee(String username, String password, String permissionString, String firstName,
                            String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String address,
                            char gender, LocalDate hireDate, String employmentStatus, String departmentId,
                            String teamId, String roleId, String qualifications, String completedTrainings,
                            Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) {

        Employee newEmployee = new Employee(username, password, permissionString, firstName, lastName, email, phoneNumber,
                dateOfBirth, address, gender, hireDate, employmentStatus, departmentId, teamId, roleId,
                qualifications, completedTrainings, managerId, itAdmin, hr, hrHead, isManager);

        employeeDao.addEmployeeToDb(newEmployee);
        employees.add(newEmployee);
    }

    /**
     * Löscht einen Mitarbeiter aus der Mitarbeiterliste.
     *
     * @param id Mitarbeiter der gelöscht wird.
     * @author Elias Glauert
     */
    public void removeEmployee(int id) {
        employees.removeIf(employee -> employee.getId() == id);
    }

    /**
     * Findet alle Mitarbeiter in der Datenbank mit den passenden Daten in den Feldern.
     * Kann gleich gesehen werden wie ein SELECT Command bei SQL.
     *
     * @param fields   Eine Liste von Feldnamen, die geprüft werden sollen.
     * @param contents Eine Liste von Inhalten, die mit den Feldern verglichen werden sollen.
     * @return Gibt eine Liste von gefundenen Mitarbeitern zurück.
     * @author Elias Glauert
     */
    public List<Employee> findEmployees(List<String> fields, List<String> contents) {
        List<Employee> matchingEmployees = new ArrayList<>();

        if (fields.size() != contents.size()) {
            throw new IllegalArgumentException("Fields and contents lists must be of the same size.");
        }

        for (Employee employee : employees) {
            boolean match = true;
            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                String content = contents.get(i);

                if (!Objects.equals(getValueOfField(employee, field), content)) {
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
        System.out.println("EmployeeManager.employees.size() = " + employees.size());
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Gibt den Wert eines Feldes zurück anhand des namens des Feldes.
     *
     * @return Wert wird als String zurückgegeben.
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
                return employee.getDateOfBirth().toString();
            }
            case "address" -> {
                return employee.getAddress();
            }
            case "gender" -> {
                return String.valueOf(employee.getGender());
            }
            case "hireDate" -> {
                return employee.getHireDate().toString();
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
            default -> {
                return null;
            }
        }
    }

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public boolean hasEmployeesGenerated() {
        return employees != null && !employees.isEmpty();
    }

    public List<Employee> findAll() {
        return employeeDao.getAllEmployees();
    }

    public void updateEmployee(Employee updatedEmployee) throws Exception {
        if (employeeDao != null) {
            employeeDao.updateEmployee(updatedEmployee);
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