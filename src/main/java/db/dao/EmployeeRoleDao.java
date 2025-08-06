package db.dao;

import model.json.Role;
import java.util.List;

/**
 * Data Access Object (DAO) interface for EmployeeRole objects.
 * This interface defines the methods for interacting with employee role data.
 */
public interface EmployeeRoleDao {

    /**
     * Retrieves a list of all roles from the database.
     * @return A list of EmployeeRole objects.
     */
    List<Role> getAllRolesFromDb();
}
