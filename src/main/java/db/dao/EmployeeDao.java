package db.dao;

import db.DatabaseManager;
import model.Employee;
import util.SqlReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mitarbeiter-Datenbank-Zugriffsklasse.
 * Zwischenschicht zwischen Mitarbeiter und Datenbank.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-09
 */
public class EmployeeDao {

    private DatabaseManager dbManager;
    private Employee employee;

    /**
     * Konstruktor für EmployeeDao.
     * @author Elias Glauert
     */
    public EmployeeDao(DatabaseManager dbManager, Employee employee) {
        this.dbManager = dbManager;
        this.employee = employee;
    }

    /**
     * Fügt einen Mitarbeiter in die Datenbank des DbManagers hinzu.
     * @param employee Mitarbeiter-Objekt, wessen Daten in die Datenbank eingefügt werden.
     * @author Elias Glauert
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
                    .replace("{completedTrainings}", employee.getCompletedTrainings())
                    .replace("{managerId}", String.valueOf(employee.getManagerId()));

            dbManager.executeUpdate(sqlCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gibt den Wert des angefragten Fields zurück.
     * @param field Feld, von welchem der Wert zurückgegeben werden soll.
     * @return Der Wert des angefragten Feldes.
     * @author Elias Glauert
     */
    public String fetchFieldValue(String field) {
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
}