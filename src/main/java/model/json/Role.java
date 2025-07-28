package model.json;

import java.util.List;

/**
 * Role Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */
public class Role {

    private String roleId;
    private String name;
    private List<String> requiredQualifications;

    public Role() {
        // TODO write this constructor
    }

    public String getroleId() {
        return roleId;
    }

    public void setroleId(String id) {
        this.roleId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRequiredQualifications() {
        return requiredQualifications;
    }

    public void setRequiredQualifications(List<String> requiredQualifications) {
        this.requiredQualifications = requiredQualifications;
    }

}

