package db.dao;

import model.json.Role;
import java.util.ArrayList;
import java.util.List;

/**
 * Example implementation of the RoleDao interface.
 * This mock class stores roles in a simple in-memory list instead of a real database.
 * You should replace this with a class that connects to your actual database.
 */
public class RoleDaoImpl implements RoleDao {

    @Override
    public List<Role> getAllRolesFromDb() {
        // This is a mock implementation.
        // In a real application, you would connect to a database here
        // and retrieve the role data.

        List<Role> roles = new ArrayList<>();
        roles.add(new Role("admin", "Administrator", List.of("A_ADMIN_QUALIFICATION")));
        roles.add(new Role("hr", "Human Resources", List.of("A_HR_QUALIFICATION", "A_PAYROLL_ACCESS")));
        roles.add(new Role("it_admin", "IT Admin", List.of("A_IT_QUALIFICATION", "A_SYSTEM_ACCESS")));
        roles.add(new Role("hr_head", "HR Head", List.of("A_HR_HEAD_QUALIFICATION")));
        roles.add(new Role("manager", "Manager", List.of("A_MANAGER_QUALIFICATION")));
        roles.add(new Role("dev", "Developer", List.of("A_CODE_ACCESS")));

        return roles;
    }
}
