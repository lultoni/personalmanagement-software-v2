package util;

import model.db.Employee;
import core.EmployeeManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility-Klasse zur Überprüfung der Zugriffsberechtigungen auf Mitarbeiterdaten.
 * Basierend auf der Rolle des aktuellen Benutzers und der Vorgesetzten-Hierarchie.
 * @author Joshua Sperber
 */
public class EmployeeFieldAccessEvaluator {

    // Rollen, die vollen Zugriff auf alle Mitarbeiterdaten haben
    private static final String[] FULL_ACCESS_ROLES = {"hr", "hr_head", "it_admin"};

    /**
     * Prüft, ob der aktuelle Benutzer die vollen Daten eines Mitarbeiters einsehen kann.
     * Vollen Zugriff haben:
     * - Der Mitarbeiter selbst
     * - Benutzer mit den Rollen hr, hr_head, it_admin
     * - Manager für Mitarbeiter, die ihnen unterstellt sind (direkt oder indirekt)
     *
     * @param currentUser Der aktuell angemeldete Benutzer.
     * @param targetEmployee Der Mitarbeiter, dessen Daten angezeigt werden sollen.
     * @param employeeManager Eine Instanz des EmployeeManagers zur Überprüfung der Hierarchie.
     * @return true, wenn der Benutzer die vollen Daten einsehen darf, sonst false.
     */
    public static boolean canViewFullData(Employee currentUser, Employee targetEmployee, EmployeeManager employeeManager) {
        // Ein Benutzer kann immer seine eigenen Daten einsehen
        if (currentUser.getId() == targetEmployee.getId()) {
            return true;
        }

        // Prüft, ob der aktuelle Benutzer eine Rolle mit vollem Zugriff hat
        for (String role : FULL_ACCESS_ROLES) {
            if (currentUser.getRoleId().equalsIgnoreCase(role)) {
                return true;
            }
        }

        // Prüft, ob der aktuelle Benutzer ein Manager ist und der Ziel-Mitarbeiter
        // ihm unterstellt ist.
        if (currentUser.getRoleId().equalsIgnoreCase("manager")) {
            return isManagerOf(currentUser, targetEmployee, employeeManager);
        }

        // Andernfalls ist das Einsehen der vollen Daten nicht erlaubt
        return false;
    }

    /**
     * Prüft, ob der aktuelle Benutzer die vollen Daten eines Mitarbeiters bearbeiten kann.
     * Die Berechtigungen sind die gleichen wie beim Einsehen.
     *
     * @param currentUser Der aktuell angemeldete Benutzer.
     * @param targetEmployee Der Mitarbeiter, dessen Daten bearbeitet werden sollen.
     * @param employeeManager Eine Instanz des EmployeeManagers zur Überprüfung der Hierarchie.
     * @return true, wenn der Benutzer die vollen Daten bearbeiten darf, sonst false.
     */
    public static boolean canEditFullData(Employee currentUser, Employee targetEmployee, EmployeeManager employeeManager) {
        // Ein Benutzer kann immer seine eigenen Daten bearbeiten
        if (currentUser.getId() == targetEmployee.getId()) {
            return true;
        }

        // Prüft, ob der aktuelle Benutzer eine Rolle mit vollem Zugriff hat
        for (String role : FULL_ACCESS_ROLES) {
            if (currentUser.getRoleId().equalsIgnoreCase(role)) {
                return true;
            }
        }

        // Prüft, ob der aktuelle Benutzer ein Manager ist und der Ziel-Mitarbeiter
        // ihm unterstellt ist.
        if (currentUser.getRoleId().equalsIgnoreCase("manager")) {
            return isManagerOf(currentUser, targetEmployee, employeeManager);
        }

        return false;
    }

    /**
     * Hilfsmethode, um festzustellen, ob ein Manager in der Vorgesetzten-Kette
     * eines Mitarbeiters steht.
     * @param manager Der Manager, der überprüft wird.
     * @param employee Der Mitarbeiter, von dem die Kette ausgeht.
     * @param employeeManager Eine Instanz des EmployeeManagers zur Suche von Mitarbeitern.
     * @return true, wenn der Manager Vorgesetzter ist, sonst false.
     * @author joshuasperber
     */
    private static boolean isManagerOf(Employee manager, Employee employee, EmployeeManager employeeManager) {
        // Wenn der Mitarbeiter keinen Vorgesetzten hat, ist die Kette zu Ende
        if (employee.getManagerId() == null) {
            return false;
        }

        // Überprüft, ob der direkte Vorgesetzte des Mitarbeiters der Manager ist
        if (employee.getManagerId() == manager.getId()) {
            return true;
        }

        // Holt den nächsten Vorgesetzten in der Kette und prüft rekursiv
        Employee nextManager = employeeManager.findById(String.valueOf(employee.getManagerId()));
        if (nextManager != null) {
            return isManagerOf(manager, nextManager, employeeManager);
        }

        return false; // Manager wurde in der Kette nicht gefunden
    }

    /**
     * Methode, die prüft, ob grundlegende Daten eines Mitarbeiters
     * eingesehen werden können.
     * @param currentUser Der aktuelle Benutzer.
     * @param targetEmployee Der Ziel-Mitarbeiter.
     * @return true, da grundlegende Daten für alle sichtbar sind.
     */
    public static boolean canViewBasicData(Employee currentUser, Employee targetEmployee) {
        // Grundlegende Daten (Name, E-Mail) sind für alle sichtbar
        return true;
    }
}
