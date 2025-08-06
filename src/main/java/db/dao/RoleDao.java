package db.dao;

import model.json.Role;
import java.util.List;

/**
 * Data Access Object (DAO) interface for Role objects.
 * This interface defines the methods for interacting with role data.
 */
public interface RoleDao {

    /**
     * Retrieves a list of all roles from the database.
     * @return A list of Role objects.
     */
    List<Role> getAllRolesFromDb();
}
