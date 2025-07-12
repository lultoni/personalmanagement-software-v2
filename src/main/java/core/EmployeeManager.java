package core;

import db.dao.EmployeeDao;
import model.Employee;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Mitarbeiter Verwaltung Klasse.
 *
 * @author Elias Glauert
 * @version 1.0
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
     * Findet einen Mitarbeiter in der Datenbank mit den passenden Daten in den Feldern.
     * @return Gibt entweder gefunden Mitarbeiter zurück, sonst null.
     * @author Elias Glauert
     */
    // TODO änder diese Methode zu einem "SELECT * WHERE"-äquivalent ab mit listen für fields und content und dann wird
    //  auch eine liste zurückgegeben
    public Employee findEmployee(String field1, String content1, String field2, String content2) {
        for (Employee employee: employees) {
            if (Objects.equals(getValueOfField(employee, field1), content1) && Objects.equals(getValueOfField(employee, field2), content2)) {
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
    // TODO finish this class
    private String getValueOfField(Employee employee, String fieldName) {
        switch (fieldName) {
            case "username" -> {
                return employee.getUsername();
            }
            case "password" -> {
                return employee.getPassword();
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
