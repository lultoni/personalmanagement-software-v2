package db.dao;

import core.EmployeeManager;
import db.DatabaseManager;
import model.db.Employee;
import util.SqlReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Mitarbeiter-Datenbank-Zugriffsklasse.
 * Zwischenschicht zwischen Mitarbeiter und Datenbank.
 *
 * @author Elias Glauert, Dorian Gläske
 * @version 1.4
 * @since 2025-08-04
 */
public class EmployeeDao {

    private EmployeeManager employeeManager;
    private final DatabaseManager dbManager;

    public EmployeeDao (DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Konstruktor für EmployeeDao.
     * @author Elias Glauert
     */
    public EmployeeDao(DatabaseManager dbManager, EmployeeManager employeeManager) {
        this.dbManager = dbManager;
        this.employeeManager = employeeManager;
    }



    /**
     * Fügt einen Mitarbeiter in die Datenbank des DbManagers hinzu.
     * @param employee Mitarbeiter-Objekt, wessen Daten in die Datenbank eingefügt werden.
     * @author Elias Glauert, Dorian Gläske
     */
    public void addEmployeeToDb(Employee employee) {
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
                    .replace("{completedTrainings}", employee.getCompletedTrainings());

            // ManagerId can be null, handled as NULL in SQL
            sqlCommand = sqlCommand.replace("{managerId}",
                    (employee.getManagerId() != null) ? String.valueOf(employee.getManagerId()) : "NULL");

            // Update to ensure Boolean values are formatted correctly in SQL
            sqlCommand = sqlCommand.replace("{itAdmin}", employee.isItAdmin() ? "TRUE" : "FALSE");
            sqlCommand = sqlCommand.replace("{hr}", employee.isHr() ? "TRUE" : "FALSE");
            sqlCommand = sqlCommand.replace("{hrHead}", employee.isHrHead() ? "TRUE" : "FALSE");
            sqlCommand = sqlCommand.replace("{isManager}", employee.isManager() ? "TRUE" : "FALSE");

            dbManager.executeUpdate(sqlCommand);
        } catch (Exception e) {
            System.err.println("Fehler beim Hinzufügen des Mitarbeiters " + employee.getUsername() + " zur DB.");
            e.printStackTrace();
        }
    }

    private boolean doesEmployeeExistInDb(Employee employee) {
        // Diese Methode ist wie zuvor erwähnt ineffizient und sollte für Produktivumgebungen
        // durch eine direkte DB-Abfrage ersetzt werden.
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
        // Sicherstellen, dass alle neuen Spalten im SELECT-Statement enthalten sind
        String query = "SELECT id, username, password, permission_string, first_name, last_name, " +
                "email, phone_number, date_of_birth, address, gender, hire_date, employment_status, " +
                "department_id, team_id, role_id, qualifications, completed_trainings, manager_id, " +
                "it_admin, hr, hr_head, is_manager FROM Employees"; // Hinzugefügte Spalten

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
                                rs.getObject("manager_id", Integer.class),
                                // NEUE FELDER HIER LESEN
                                rs.getBoolean("it_admin"),
                                rs.getBoolean("hr"),
                                rs.getBoolean("hr_head"),
                                rs.getBoolean("is_manager") // NEUES FELD
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

    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> result = dbManager.loadAll(Employee.class);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Aktualisiert einen Mitarbeiter in der Datenbank.
     * @param updatedEmployee Der aktualisierte Mitarbeiter
     * @throws Exception Falls ein Fehler bei der Aktualisierung auftritt
     * @author [Ihr Name]
     */
    public void updateEmployee(Employee updatedEmployee) throws Exception {
        try {
            // SQL-Update-Statement mit PreparedStatement für Sicherheit
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

                // Parameter setzen
                stmt.setString(1, updatedEmployee.getUsername());
                stmt.setString(2, updatedEmployee.getPassword());
                stmt.setString(3, updatedEmployee.getPermissionString());
                stmt.setString(4, updatedEmployee.getFirstName());
                stmt.setString(5, updatedEmployee.getLastName());
                stmt.setString(6, updatedEmployee.getEmail());
                stmt.setString(7, updatedEmployee.getPhoneNumber());
                stmt.setDate(8, new java.sql.Date(updatedEmployee.getDateOfBirth().getTime()));
                stmt.setString(9, updatedEmployee.getAddress());
                stmt.setString(10, String.valueOf(updatedEmployee.getGender()));
                stmt.setDate(11, new java.sql.Date(updatedEmployee.getHireDate().getTime()));
                stmt.setString(12, updatedEmployee.getEmploymentStatus());
                stmt.setString(13, updatedEmployee.getDepartmentId());
                stmt.setString(14, updatedEmployee.getTeamId());
                stmt.setString(15, updatedEmployee.getRoleId());
                stmt.setString(16, updatedEmployee.getQualifications());
                stmt.setString(17, updatedEmployee.getCompletedTrainings());

                // ManagerId kann null sein
                if (updatedEmployee.getManagerId() != null) {
                    stmt.setInt(18, updatedEmployee.getManagerId());
                } else {
                    stmt.setNull(18, Types.INTEGER);
                }

                // Boolean-Werte
                stmt.setBoolean(19, updatedEmployee.isItAdmin());
                stmt.setBoolean(20, updatedEmployee.isHr());
                stmt.setBoolean(21, updatedEmployee.isHrHead());
                stmt.setBoolean(22, updatedEmployee.isManager());

                // ID für WHERE-Klausel
                stmt.setInt(23, updatedEmployee.getId());

                // Update ausführen
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Update fehlgeschlagen, kein Datensatz wurde aktualisiert.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Aktualisieren des Mitarbeiters " + updatedEmployee.getUsername());
            throw new Exception("Datenbankfehler beim Aktualisieren: " + e.getMessage(), e);
        }
    }
}