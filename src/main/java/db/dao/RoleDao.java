package db.dao;

import model.json.Role;
import java.util.List;

/**
 * dies ist eine Interface für Role Objekte
 * hiermit sollen alle Rollen aus der Datenbank aufgelistet werden, sodass sie danach bearbeitbar werden
 * @author joshuasperber
*/
public interface RoleDao {

    /**
     * @return eine Liste vom Objekt Role
     */
    List<Role> getAllRolesFromDb();
    // Listet alle Rollen aus der erstellten Datenbank auf, damit diese bearbeitbar werden
}
