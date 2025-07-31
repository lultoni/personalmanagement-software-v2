package db.dao;

import core.EmployeeManager;
import db.DatabaseManager;
import model.db.Employee;
import util.SqlReader;

import java.sql.*;
import java.util.ArrayList;

/**
 * Mitarbeiter-Datenbank-Zugriffsklasse.
 * Zwischenschicht zwischen Mitarbeiter und Datenbank.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-09
 */
public class EmployeeDao {

    private DatabaseManager dbManager;
    private EmployeeManager employeeManager;

    /**
     * Konstruktor für EmployeeDao.
     * @author Elias Glauert
     */
    public EmployeeDao(DatabaseManager dbManager, EmployeeManager employeeManager) {
        this.dbManager = dbManager;
        this.employeeManager = employeeManager;
    }

    public EmployeeDao() {
    }

    /**
     * Fügt einen Mitarbeiter in die Datenbank des DbManagers hinzu.
     * @param employee Mitarbeiter-Objekt, wessen Daten in die Datenbank eingefügt werden.
     * @author Elias Glauert
     */
    public void addEmployeeToDb(Employee employee) {
        if (doesEmployeeExistInDb(employee)) return;
        try {
            String sqlCommand = SqlReader.giveCommand("addEmployee");

            sqlCommand = sqlCommand
                    .replace("{username}", employee.getUsername())
                    .replace("{password}", employee.getPassword())
                    .replace("{permissionString}", employee.getPermissionString())
                    .replace("{firstName}", employee.getFirstName())
                    .replace("{lastName}", employee.getLastName())
                    .replace("{email}", employee.getEmail())
                    .replace("{phoneNumber}", employee.getPhoneNumber())
                    .replace("{dateOfBirth}", new java.sql.Date(employee.getDateOfBirth().getTime()).toString())
                    .replace("{address}", employee.getAddress())
                    .replace("{gender}", String.valueOf(employee.getGender()))
                    .replace("{hireDate}", new java.sql.Date(employee.getHireDate().getTime()).toString())
                    .replace("{employmentStatus}", employee.getEmploymentStatus())
                    .replace("{departmentId}", employee.getDepartmentId())
                    .replace("{teamId}", employee.getTeamId())
                    .replace("{roleId}", employee.getRoleId())
                    .replace("{qualifications}", employee.getQualifications())
                    .replace("{completedTrainings}", employee.getCompletedTrainings())
                    .replace("{managerId}", String.valueOf(employee.getManagerId()));

            dbManager.executeUpdate(sqlCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean doesEmployeeExistInDb(Employee employee) {
        for (Employee list_emp: getAllEmployeesFromDb()) {
            if (employee.equals(list_emp)) return true;
        }
        return false;
    }

    /**
     * Gibt den Wert des angefragten Fields zurück.
     * @param field Feld, von welchem der Wert zurückgegeben werden soll.
     * @return Der Wert des angefragten Feldes.
     * @author Elias Glauert
     */
    public String fetchFieldValue(String field, Employee employee) {
        String fieldValue = null;
        try {
            String sqlQuery;

            if (employee.getId() > 0) {
                sqlQuery = "SELECT " + field + " FROM Employees WHERE id = ?";
            } else {
                sqlQuery = "SELECT " + field + " FROM Employees WHERE username = ?";
            }

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

                if (employee.getId() > 0) {
                    stmt.setInt(1, employee.getId());
                } else {
                    stmt.setString(1, employee.getUsername());
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        fieldValue = rs.getString(field);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching field value", e);
        }
        return fieldValue;
    }

    public ArrayList<Employee> getAllEmployeesFromDb() {
        System.out.println(" ~ getAllEmployeesFromDb()");
        ArrayList<Employee> ret_list = new ArrayList<>();
        String query = "SELECT * FROM Employees";

        try (Connection conn = dbManager.getConnection()) {
            System.out.println("Connection established: " + !conn.isClosed());
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                try {
                    while (rs.next()) {
                        // Debugging data collection
                        System.out.println("Creating Employee object for: " + rs.getString("username"));

                        Employee employee = new Employee(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("permission_string"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("phone_number"),
                                rs.getDate("date_of_birth"),
                                rs.getString("address"),
                                rs.getString("gender").charAt(0),
                                rs.getDate("hire_date"),
                                rs.getString("employment_status"),
                                rs.getString("department_id"),
                                rs.getString("team_id"),
                                rs.getString("role_id"),
                                rs.getString("qualifications"),
                                rs.getString("completed_trainings"),
                                rs.getInt("manager_id"),
                                this
                        );
                        System.out.println("Employee object created successfully.");
                        ret_list.add(employee);
                    }
                } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
                    System.out.println("DATABASE LOADING 'ERROR' - HARMLESS - " +
                            "(EmployeeDao; rs.next gives an error, " +
                            "because the connection to the db is already closed for an unknown reason, " +
                            "was unable to be fixed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper error handling for connections.
        }

        return ret_list;
    }

    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }
}