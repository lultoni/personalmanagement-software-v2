package db.dao; // Dein Paketname für DAOs

import core.EmployeeManager;
import model.db.Employee; // Importiere deine Employee-Klasse
import db.DatabaseManager; // Importiere deinen DatabaseManager
import java.sql.*; // Für Connection, PreparedStatement, ResultSet, Date, Types etc.
import java.time.LocalDate; // Für LocalDate
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Für JsonParser-Ergebnisse, falls relevant
import util.SqlReader; // Für SqlReader.giveCommand, falls verwendet

/**
 * Mitarbeiter-Datenbank-Zugriffsklasse.
 * Zwischenschicht zwischen Mitarbeiter und Datenbank.
 *
 * @author Elias Glauert, Dorian Gläske
 * @version 1.5 (Datumskonvertierung und SQL-Sicherheit behoben)
 * @since 2025-08-04
 */
public class EmployeeDao {

    private EmployeeManager employeeManager; // Wird über setEmployeeManager gesetzt
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
     * Verwendet PreparedStatement für Sicherheit und korrekte Typenbehandlung.
     * @param employee Mitarbeiter-Objekt, wessen Daten in die Datenbank eingefügt werden.
     * @author Elias Glauert, Dorian Gläske
     */
    public void addEmployeeToDb(Employee employee) {
        // SQL-Insert-Statement mit Platzhaltern (?)
        // Stelle sicher, dass "addEmployee" in SqlReader den korrekten SQL-String liefert,
        // z.B. "INSERT INTO Employees (username, password, ...) VALUES (?, ?, ...)"
        String sqlCommand = SqlReader.giveCommand("addEmployee");

        try (Connection conn = dbManager.getConnection(); // Verwende dbManager.getConnection()
             PreparedStatement pstmt = conn.prepareStatement(sqlCommand)) {

            // Parameter setzen
            pstmt.setString(1, employee.getUsername());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getPermissionString());
            pstmt.setString(4, employee.getFirstName());
            pstmt.setString(5, employee.getLastName());
            pstmt.setString(6, employee.getEmail());
            pstmt.setString(7, employee.getPhoneNumber());

            // ********************************************************************
            // KORREKTUR: LocalDate zu java.sql.Date konvertieren
            // ********************************************************************
            if (employee.getDateOfBirth() != null) {
                pstmt.setDate(8, java.sql.Date.valueOf(employee.getDateOfBirth()));
            } else {
                pstmt.setNull(8, Types.DATE); // Setze NULL, wenn Geburtsdatum nicht vorhanden
            }
            // ********************************************************************

            pstmt.setString(9, employee.getAddress());
            pstmt.setString(10, String.valueOf(employee.getGender()));

            // ********************************************************************
            // KORREKTUR: LocalDate zu java.sql.Date konvertieren
            // ********************************************************************
            if (employee.getHireDate() != null) {
                pstmt.setDate(11, java.sql.Date.valueOf(employee.getHireDate()));
            } else {
                pstmt.setNull(11, Types.DATE); // Setze NULL, wenn Einstellungsdatum nicht vorhanden
            }
            // ********************************************************************

            pstmt.setString(12, employee.getEmploymentStatus());
            pstmt.setString(13, employee.getDepartmentId());
            pstmt.setString(14, employee.getTeamId());
            pstmt.setString(15, employee.getRoleId());
            pstmt.setString(16, employee.getQualifications());
            pstmt.setString(17, employee.getCompletedTrainings());

            // ManagerId kann null sein, muss als NULL in SQL gehandhabt werden (angenommen Integer-Typ)
            // ********************************************************************
            // KORREKTUR: ManagerId als Integer behandeln
            // ********************************************************************
            if (employee.getManagerId() != null) {
                pstmt.setInt(18, employee.getManagerId());
            } else {
                pstmt.setNull(18, Types.INTEGER); // Oder der passende SQL-Typ für ManagerId
            }
            // ********************************************************************

            // Boolean-Werte direkt setzen
            pstmt.setBoolean(19, employee.isItAdmin());
            pstmt.setBoolean(20, employee.isHr());
            pstmt.setBoolean(21, employee.isHrHead());
            pstmt.setBoolean(22, employee.isManager());

            pstmt.executeUpdate();
            System.out.println("Employee added to DB: " + employee.getUsername());

        } catch (SQLException e) {
            System.err.println("Fehler beim Hinzufügen des Mitarbeiters " + employee.getUsername() + " zur DB: " + e.getMessage());
            e.printStackTrace();
            // Hier könntest du eine spezifischere Exception werfen oder behandeln
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
                        // Hier wird ein String gelesen, keine direkte Datumsbehandlung nötig,
                        // aber der Aufrufer muss das Format kennen.
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
                        // ********************************************************************
                        // KORREKTUR: java.sql.Date zu LocalDate konvertieren
                        // ********************************************************************
                        java.sql.Date sqlDateOfBirth = rs.getDate("date_of_birth");
                        LocalDate dateOfBirth = (sqlDateOfBirth != null) ? sqlDateOfBirth.toLocalDate() : null;

                        java.sql.Date sqlHireDate = rs.getDate("hire_date");
                        LocalDate hireDate = (sqlHireDate != null) ? sqlHireDate.toLocalDate() : null;
                        // ********************************************************************

                        Employee employee = new Employee(
                                rs.getInt("id"), // Annahme: ID ist int
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("permission_string"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("phone_number"),
                                dateOfBirth, // Verwende das konvertierte LocalDate
                                rs.getString("address"),
                                rs.getString("gender").charAt(0),
                                hireDate, // Verwende das konvertierte LocalDate
                                rs.getString("employment_status"),
                                rs.getString("department_id"),
                                rs.getString("team_id"),
                                rs.getString("role_id"),
                                rs.getString("qualifications"),
                                rs.getString("completed_trainings"),
                                // ********************************************************************
                                // KORREKTUR: ManagerId als Integer.class lesen
                                // ********************************************************************
                                rs.getObject("manager_id", Integer.class), // ManagerId kann null sein
                                // ********************************************************************
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

    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    public List<Employee> getAllEmployees() {
        // Diese Methode scheint eine andere Logik zu verwenden (dbManager.loadAll).
        // Die Implementierung von dbManager.loadAll(Employee.class) ist hier nicht sichtbar,
        // aber sie müsste intern ebenfalls die LocalDate-Konvertierung vornehmen.
        List<Employee> result = dbManager.loadAll(Employee.class);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Aktualisiert einen Mitarbeiter in der Datenbank.
     * Verwendet PreparedStatement für Sicherheit und korrekte Typenbehandlung.
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

                // ********************************************************************
                // KORREKTUR: LocalDate zu java.sql.Date konvertieren
                // ********************************************************************
                if (updatedEmployee.getDateOfBirth() != null) {
                    stmt.setDate(8, java.sql.Date.valueOf(updatedEmployee.getDateOfBirth()));
                } else {
                    stmt.setNull(8, Types.DATE);
                }
                // ********************************************************************

                stmt.setString(9, updatedEmployee.getAddress());
                stmt.setString(10, String.valueOf(updatedEmployee.getGender()));

                // ********************************************************************
                // KORREKTUR: LocalDate zu java.sql.Date konvertieren
                // ********************************************************************
                if (updatedEmployee.getHireDate() != null) {
                    stmt.setDate(11, java.sql.Date.valueOf(updatedEmployee.getHireDate()));
                } else {
                    stmt.setNull(11, Types.DATE);
                }
                // ********************************************************************

                stmt.setString(12, updatedEmployee.getEmploymentStatus());
                stmt.setString(13, updatedEmployee.getDepartmentId());
                stmt.setString(14, updatedEmployee.getTeamId());
                stmt.setString(15, updatedEmployee.getRoleId());
                stmt.setString(16, updatedEmployee.getQualifications());
                stmt.setString(17, updatedEmployee.getCompletedTrainings());

                // ManagerId kann null sein
                // ********************************************************************
                // KORREKTUR: ManagerId als Integer behandeln
                // ********************************************************************
                if (updatedEmployee.getManagerId() != null) {
                    stmt.setInt(18, updatedEmployee.getManagerId());
                } else {
                    stmt.setNull(18, Types.INTEGER);
                }
                // ********************************************************************

                // Boolean-Werte
                stmt.setBoolean(19, updatedEmployee.isItAdmin());
                stmt.setBoolean(20, updatedEmployee.isHr());
                stmt.setBoolean(21, updatedEmployee.isHrHead());
                stmt.setBoolean(22, updatedEmployee.isManager());

                // ID für WHERE-Klausel (angenommen, ID ist int)
                stmt.setInt(23, updatedEmployee.getId());

                // Update ausführen
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
}
