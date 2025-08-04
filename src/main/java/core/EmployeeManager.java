package core;

import db.dao.EmployeeDao;
import model.db.Employee;
import util.EmployeeCreationService;
import db.DatabaseManager;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.List;

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
    public EmployeeManager(DatabaseManager databaseManager) {
        this.employeeDao = employeeDao;
        this.databaseManager = databaseManager;
    }

    /**
     * Adds all Employees from the DB to the employees ArrayList.
     *
     * @author Elias Glauert
     */
    public void setUpEmployees() {
        employees = new ArrayList<>();
        employees.addAll(employeeDao.getAllEmployeesFromDb());
        System.out.println(" ~ db ~ all employees in EmployeeManager:");
        for (Employee employee : employees) System.out.println("   | " + employee.toString());
    }

    public void create100Employee() throws IOException {
        EmployeeCreationService employeeCreationService = new EmployeeCreationService(databaseManager, this, employeeDao);
        employeeCreationService.generate_x_Employees(100);
    }

    /**
     * Fügt der Mitarbeiterliste einen Mitarbeiter hinzu und erstellt und speicher ihn.
     *
     * @param employee Mitarbeiter der hinzugefügt wird.
     * @author Elias Glauert, Dorian Gläske
     */
    public void addEmployee(Employee employee, String password, String permissionString, String firstName,
                            String lastName, String email, String phoneNumber, Date dateOfBirth, String address,
                            char gender, Date hireDate, String employmentStatus, String departmentId,
                            String teamId, String roleId, String qualifications, String completedTrainings,
                            Integer managerId, boolean itAdmin, boolean hr, boolean hrHead, boolean isManager) {

        employee.setUsername(firstName.toLowerCase() + "." + lastName.toLowerCase());
        employee.setPassword("123456");
        employee.setPermissionString(permissionString);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumber(phoneNumber);
        employee.setDateOfBirth(dateOfBirth);
        employee.setAddress(address);
        employee.setGender(gender);
        Date today = new Date();
        employee.setHireDate(today);
        employee.setEmploymentStatus(employmentStatus);
        employee.setDepartmentId(departmentId);
        employee.setTeamId(teamId);
        employee.setRoleId(roleId);
        employee.setQualifications(qualifications);
        employee.setCompletedTrainings(completedTrainings);
        employee.setManagerId(managerId);
        employee.setItAdmin(itAdmin);
        employee.setHr(hr);
        employee.setHrHead(hrHead);
        employee.setIsManager(isManager);
        employeeDao.addEmployeeToDb(employee);

        employees.add(employee);
    }

    /**
     * Löscht einen Mitarbeiter aus der Mitarbeiterliste.
     *
     * @param id Mitarbeiter der gelöscht wird.
     * @author Elias Glauert
     */
    public void removeEmployee(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                employees.remove(employee);
                return;
            }
        }
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
    public ArrayList<Employee> findEmployees(ArrayList<String> fields, ArrayList<String> contents) {
        ArrayList<Employee> matchingEmployees = new ArrayList<>();

        // fields und contents müssen natürlich die gleiche Größe haben
        if (fields.size() != contents.size()) {
            throw new IllegalArgumentException("Fields and contents lists must be of the same size.");
        }

        // geht jeden mitarbeiter durch
        for (Employee employee : employees) {
            boolean match = true;

            // überprüft jedes einzelne feld und den wert
            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                String content = contents.get(i);

                if (!Objects.equals(getValueOfField(employee, field), content)) {
                    match = false;
                    break; // es wurde ein feld gefunden was nicht übereinstimmt
                }
            }

            // wenn immer noch ein match besteht, wird der mitarbeiter in die return liste hinzugefügt
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
        }
        return "UNEXPECTED_FIELD_VALUE";
    }

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public boolean hasEmployeesGenerated() {
        return !employees.isEmpty();
    }


    public EmployeeManager(EmployeeDao employeeDao, DatabaseManager dbManager) {
        this.employeeDao = employeeDao;
        this.databaseManager = dbManager;
    }

    public List<Employee> findAll() {
        return employeeDao.getAllEmployees(); //  die Methode muss in EmployeeDao existieren
    }
}
