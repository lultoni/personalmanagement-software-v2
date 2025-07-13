package util;

import core.EmployeeManager;
import db.DatabaseManager;
import db.dao.EmployeeDao;
import model.Employee;

import java.util.Calendar;
import java.util.Date;

/**
 * Mit dieser Klasse wird basierend auf dem Beispielszenario ein Personal generiert.
 * Klasse wird statisch verwendet.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class EmployeeGenerator {

    /**
     * Generiert einen Beispielsatz an Mitarbeitern.
     * @param dbManager
     * @param employeeManager
     * @author Elias Glauert
     */
    // TODO finish this method
    //  basiere das hier auf der company structure
    public static void generateEmployees(DatabaseManager dbManager, EmployeeManager employeeManager, EmployeeDao employeeDao) {
        Employee _ = new Employee(
                true,
                "max.mustermann",
                "12345678",
                PermissionChecker.getEmployeePermissionString("null", "null"),
                "Max",
                "Mustermann",
                "max.mustermann@btbc.de",
                "+49 1234 5678910",
                new Date(2007, Calendar.FEBRUARY, 9),
                "Musterstraße 123, 14469 Potsdam",
                'M',
                new Date(2024, Calendar.SEPTEMBER, 1),
                "null",
                "null",
                "null",
                "null",
                "null",
                "null",
                0,
                employeeManager,
                employeeDao);
        System.out.println("EmployeeGenerator hat 1 Mitarbeiter erstellt.");
    }

}
