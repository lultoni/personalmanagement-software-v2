package core;

import db.dao.EmployeeDao;
import model.Employee;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Mitarbeiter Verwaltung Klasse.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-11
 */
public class EmployeeManager {

    /**
     * Liste aller Mitarbeiter, welche existieren
     */
    private ArrayList<Employee> employees;
    private EmployeeDao employeeDao;

    /**
     * Konstruktor für die EmployeeManager Klasse.
     * @author Elias Glauert
     */
    public EmployeeManager(EmployeeDao employeeDao) {
        this. employeeDao = employeeDao;
    }

    /**
     * Adds all Employees from the DB to the employees ArrayList.
     * @author Elias Glauert
     */
    public void setUpEmployees() {
        employees = new ArrayList<>();
        employees.addAll(employeeDao.getAllEmployeesFromDb());
        System.out.println(" ~ db ~ all employees in EmployeeManager:");
        for (Employee employee: employees) System.out.println("   | " + employee.toString());
    }

    /**
     * Fügt der Mitarbeiterliste einen Mitarbeiter hinzu.
     * @param employee Mitarbeiter der hinzugefügt wird.
     * @author Elias Glauert
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    /**
     * Löscht einen Mitarbeiter aus der Mitarbeiterliste.
     * @param id Mitarbeiter der gelöscht wird.
     * @author Elias Glauert
     */
    public void removeEmployee(int id) {
        for (Employee employee: employees) {
            if (employee.getId() == id) {
                employees.remove(employee);
                return;
            }
        }
    }

    /**
     * Findet alle Mitarbeiter in der Datenbank mit den passenden Daten in den Feldern.
     * Kann gleich gesehen werden wie ein SELECT Command bei SQL.
     * @param fields Eine Liste von Feldnamen, die geprüft werden sollen.
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
        for (Employee employee: employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Gibt den Wert eines Feldes zurück anhand des namens des Feldes.
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
            case "managerId" -> {
                return String.valueOf(employee.getManager().getId());
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
}
