package util;

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
     */
    private static final String full_permissions_string   = "B";
    private static final String admin_permissions_string  = "B";
    private static final String hr_permissions_string     = "-";
    private static final String normal_permissions_string = "-";
    private static final String empty_permissions_string  = "-";
    private static String       current_permissions       = "-";

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
}
