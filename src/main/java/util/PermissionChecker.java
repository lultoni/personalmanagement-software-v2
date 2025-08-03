package util;

import model.db.Employee;

/**
 * Überprüft den Permission String jedes Mitarbeiters, ob bestimmte Features angezeigt werden sollen.
 * Klasse wird statisch verwendet.
 * @version 1.0
 * @since 2025-07-13
 * @author Elias Glauert
 */
public class PermissionChecker {

    /**
     * Alle möglichen Features für die Permissions gebraucht werden.
     * B - Backup.
     * S - Shutdown
     * H - head (Mitarbeitern Training geben)
     * T - Trainings an HR Mitarbeiter
     * t - Training erteilen
     */
    private static final String full_permissions_string   = "BSHTt";
    private static final String admin_permissions_string  = "BSHTt";
    private static final String hr_permission_head_string = "--HTt";
    private static final String hr_permissions_string     = "--H-t";
    private static final String head_permission_string    = "--H--";
    private static final String normal_permissions_string = "-----";
    private static final String empty_permissions_string  = "-----";
    private static String       current_permissions       = "-----";


    // Aktuell eingeloggter Mitarbeiter
    private static Employee     currentEmployee           = null;

    /**
     * Gibt die relevanten Berechtigungen zurück.
     * @param department_id DepartmentID von dem Mitarbeiter
     * @param role_id RoleID von dem Mitarbeiter
     * @return String von den Permissions, basierend auf den Parametern
     * @author Elias Glauert
     */
    public static String getEmployeePermissionString(String department_id, String role_id) {
        // TODO later on change this to work correctly (params can be changed!)
        return admin_permissions_string;
    }

    public static void setCurrent_permissions(String permissions) {

        current_permissions = permissions;
    }

    public static String getCurrent_permissions() {

        return current_permissions;
    }

    public static void resetCurrent_permissions() {
        setCurrent_permissions(empty_permissions_string);
    }

    /**
     * Überprüft, ob eine bestimme Permission in den jetzigen des Users vorhanden ist.
     * @param permission Permission auf die Überprüft wird
     * @return Ob der User die angefragte Berechtigung hat
     * @author Elias Glauert
     */
    public static boolean hasPermission(char permission) {
        return getCurrent_permissions().contains(String.valueOf(permission));

    }

    /**
     * Prüft, ob ein Mitarbeiter einem anderen Schulungen zuweisen darf.
     * Regeln:
     * - HR-Head: darf allen zuweisen
     * - HR-Mitarbeiter: darf nur Nicht-IT-Admins und Nicht-Heads zuweisen
     * - Head: darf nur sich selbst und Teammitglieder zuweisen
     * - Alle anderen: dürfen niemandem zuweisen
     */
    public static boolean canAssignTraining(Employee from, Employee to) {
        if (from == null || to == null) return false;

        String fromRole = from.getRoleId();
        String toRole   = to.getRoleId();

        // HR-Head darf allen zuweisen
        if (fromRole.equals("role-hr-head")) {
            return true;
        }

        // HR-Mitarbeiter dürfen nur an Nicht-IT-Admins und Nicht-Heads
        if (isHrEmployee(fromRole)) {
            return !isItAdmin(toRole) && !isHead(toRole);
        }

        // Heads dürfen nur an sich selbst und ihr Team zuweisen
        if (isHead(fromRole)) {
            return from.getId() == to.getId() ||
                    (from.getTeamId() != null && from.getTeamId().equals(to.getTeamId()));
        }

        // Standard: keine Berechtigung
        return false;
    }


    // Hilfsmethoden (private oder public je nach Bedarf)
    private static boolean isHrEmployee(String roleId) {
        return roleId.equals("role-hr-advisor") ||
                roleId.equals("role-payroll-admin") ||
                roleId.equals("role-personnel-consultant");
    }

    private static boolean isItAdmin(String roleId) {
        return roleId.equals("role-it-systems-admin") ||
                roleId.equals("role-it-head");
    }

    private static boolean isHead(String roleId) {
        return roleId != null && roleId.endsWith("-head");
    }


}
// TOdO - nach einem erfolgreichen Login-attempot soll vom current persission checker die Permissiion auf den aktuell nutzenden Permission setzen
// ToDo - employee generator/ employee generator service soll beim Erstellen des Mitarbeiters, soll gleich der passende Permission Sting gegeben werden