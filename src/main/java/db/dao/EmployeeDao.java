package db.dao;

import model.db.Employee;
import db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date; // Geändert von java.time.LocalDate
import java.util.List;
import java.util.Map;
import util.SqlReader;

/**
 * Mitarbeiter-Datenbank-Zugriffsklasse.
 * Zwischenschicht zwischen Mitarbeiter und Datenbank.
 *
 * @author Elias Glauert, Dorian Gläske
 * @version 1.7 (Datumstypen von LocalDate zu Date geändert)
 * @since 2025-08-04
 */
public class EmployeeDao {

    private core.EmployeeManager employeeManager;
    private final DatabaseManager dbManager;

    public EmployeeDao (DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public EmployeeDao(DatabaseManager dbManager, core.EmployeeManager employeeManager) {
        this.dbManager = dbManager;
        this.employeeManager = employeeManager;
    }

    /**
     * Fügt einen Mitarbeiter in die Datenbank des DbManagers hinzu.
     * Verwendet PreparedStatement für Sicherheit und korrekte Typenbehandlung.
     * @param employee Mitarbeiter-Objekt, wessen Daten in die Datenbank eingefügt werden.
     * @author Elias Glauert, Dorian Gläske
     */
    public void addEmployeeToDb(Employee employee) {
        String sqlCommand = SqlReader.giveCommand("addEmployee");

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCommand)) {

            pstmt.setString(1, employee.getUsername());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getPermissionString());
            pstmt.setString(4, employee.getFirstName());
            pstmt.setString(5, employee.getLastName());
            pstmt.setString(6, employee.getEmail());
            pstmt.setString(7, employee.getPhoneNumber());


            if (employee.getDateOfBirth() != null) {
                pstmt.setDate(8, new java.sql.Date(employee.getDateOfBirth().getTime()));
            } else {
                pstmt.setNull(8, Types.DATE);
            }

            pstmt.setString(9, employee.getAddress());
            pstmt.setString(10, String.valueOf(employee.getGender()));


            if (employee.getHireDate() != null) {
                pstmt.setDate(11, new java.sql.Date(employee.getHireDate().getTime()));
            } else {
                pstmt.setNull(11, Types.DATE);
            }

            pstmt.setString(12, employee.getEmploymentStatus());
            pstmt.setString(13, employee.getDepartmentId());
            pstmt.setString(14, employee.getTeamId());
            pstmt.setString(15, employee.getRoleId());
            pstmt.setString(16, employee.getQualifications());
            pstmt.setString(17, employee.getCompletedTrainings());

            if (employee.getManagerId() != null) {
                pstmt.setInt(18, employee.getManagerId());
            } else {
                pstmt.setNull(18, Types.INTEGER);
            }

            pstmt.setBoolean(19, employee.isItAdmin());
            pstmt.setBoolean(20, employee.isHr());
            pstmt.setBoolean(21, employee.isHrHead());
            pstmt.setBoolean(22, employee.isManager());

            pstmt.executeUpdate();
            System.out.println("Employee added to DB: " + employee.getUsername());

        } catch (SQLException e) {
            System.err.println("Fehler beim Hinzufügen des Mitarbeiters " + employee.getUsername() + " zur DB: " + e.getMessage());
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
        String query = "SELECT id, username, password, permission_string, first_name, last_name, " +
                "email, phone_number, date_of_birth, address, gender, hire_date, employment_status, " +
                "department_id, team_id, role_id, qualifications, completed_trainings, manager_id, " +
                "it_admin, hr, hr_head, is_manager FROM Employees";

        try (Connection conn = dbManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Fehler: Datenbankverbindung ist null oder geschlossen.");
                return ret_list;
            }
            System.out.println("Connection established: " + !conn.isClosed());
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                try {
                    while (rs.next()) {
                        java.sql.Date sqlDateOfBirth = rs.getDate("date_of_birth");
                        Date dateOfBirth = (sqlDateOfBirth != null) ? new Date(sqlDateOfBirth.getTime()) : null;

                        java.sql.Date sqlHireDate = rs.getDate("hire_date");
                        Date hireDate = (sqlHireDate != null) ? new Date(sqlHireDate.getTime()) : null;

                        Employee employee = new Employee(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("permission_string"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("phone_number"),
                                dateOfBirth,
                                rs.getString("address"),
                                rs.getString("gender").charAt(0),
                                hireDate,
                                rs.getString("employment_status"),
                                rs.getString("department_id"),
                                rs.getString("team_id"),
                                rs.getString("role_id"),
                                rs.getString("qualifications"),
                                rs.getString("completed_trainings"),
                                rs.getObject("manager_id", Integer.class),
                                rs.getBoolean("it_admin"),
                                rs.getBoolean("hr"),
                                rs.getBoolean("hr_head"),
                                rs.getBoolean("is_manager")
                        );
                        ret_list.add(employee);
                    }
                } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
                    System.err.println("DATABASE LOADING 'ERROR' - HARMLESS IF DATA IS LOADED (EmployeeDao; H2 issue): " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Fehler beim Laden aller Mitarbeiter aus der DB: " + e.getMessage());
            e.printStackTrace();
        }

        return ret_list;
    }

    public void setEmployeeManager(core.EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    public List<Employee> getAllEmployees() {
       //Diese Methode ruft alle Mitarbetier aus der Datenbank auf
        List<Employee> result = dbManager.loadAll(Employee.class);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Aktualisiert einen Mitarbeiter in der Datenbank.
     * Verwendet PreparedStatement für Sicherheit und korrekte Typenbehandlung.
     * @param updatedEmployee Der aktualisierte Mitarbeiter
     * @throws Exception Falls ein Fehler bei der Aktualisierung auftritt
     * @author Joshua Sperber
     */
    public void updateEmployee(Employee updatedEmployee) throws Exception {
        try {
            String sql = "UPDATE Employees SET " +
                    "username = ?, " +
                    "password = ?, " +
                    "permission_string = ?, " +
                    "first_name = ?, " +
                    "last_name = ?, " +
                    "email = ?, " +
                    "phone_number = ?, " +
                    "date_of_birth = ?, " +
                    "address = ?, " +
                    "gender = ?, " +
                    "hire_date = ?, " +
                    "employment_status = ?, " +
                    "department_id = ?, " +
                    "team_id = ?, " +
                    "role_id = ?, " +
                    "qualifications = ?, " +
                    "completed_trainings = ?, " +
                    "manager_id = ?, " +
                    "it_admin = ?, " +
                    "hr = ?, " +
                    "hr_head = ?, " +
                    "is_manager = ? " +
                    "WHERE id = ?";

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, updatedEmployee.getUsername());
                stmt.setString(2, updatedEmployee.getPassword());
                stmt.setString(3, updatedEmployee.getPermissionString());
                stmt.setString(4, updatedEmployee.getFirstName());
                stmt.setString(5, updatedEmployee.getLastName());
                stmt.setString(6, updatedEmployee.getEmail());
                stmt.setString(7, updatedEmployee.getPhoneNumber());

                if (updatedEmployee.getDateOfBirth() != null) {
                    stmt.setDate(8, new java.sql.Date(updatedEmployee.getDateOfBirth().getTime()));
                } else {
                    stmt.setNull(8, Types.DATE);
                }

                stmt.setString(9, updatedEmployee.getAddress());
                stmt.setString(10, String.valueOf(updatedEmployee.getGender()));

                if (updatedEmployee.getHireDate() != null) {
                    stmt.setDate(11, new java.sql.Date(updatedEmployee.getHireDate().getTime()));
                } else {
                    stmt.setNull(11, Types.DATE);
                }

                stmt.setString(12, updatedEmployee.getEmploymentStatus());
                stmt.setString(13, updatedEmployee.getDepartmentId());
                stmt.setString(14, updatedEmployee.getTeamId());
                stmt.setString(15, updatedEmployee.getRoleId());
                stmt.setString(16, updatedEmployee.getQualifications());
                stmt.setString(17, updatedEmployee.getCompletedTrainings());

                if (updatedEmployee.getManagerId() != null) {
                    stmt.setInt(18, updatedEmployee.getManagerId());
                } else {
                    stmt.setNull(18, Types.INTEGER);
                }

                stmt.setBoolean(19, updatedEmployee.isItAdmin());
                stmt.setBoolean(20, updatedEmployee.isHr());
                stmt.setBoolean(21, updatedEmployee.isHrHead());
                stmt.setBoolean(22, updatedEmployee.isManager());

                stmt.setInt(23, updatedEmployee.getId());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Update fehlgeschlagen, kein Datensatz wurde aktualisiert.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Aktualisieren des Mitarbeiters " + updatedEmployee.getUsername() + ": " + e.getMessage());
            throw new Exception("Datenbankfehler beim Aktualisieren: " + e.getMessage(), e);
        }
    }

    /**
     * Löscht einen Mitarbeiter aus der Datenbank anhand seiner ID.
     * @param id Die ID des zu löschenden Mitarbeiters.
     * @author Elias Glauert
     */
    public void removeEmployee(int id) {
        String sqlCommand = SqlReader.giveCommand("removeEmployee");

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCommand)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Employee removed from DB: " + id);

        } catch (SQLException e) {
            System.err.println("Error removing employee from DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
