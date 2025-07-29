package model.json;

import java.util.List;
import java.util.ArrayList; // Notwendig, um die Listen zu initialisieren

/**
        * Role Klasse.
        * Spiegelt das json-Objekt wider.
        *
        * @author Dorian Gläske
 * @version 1.1
        * @since 2025-07-29
        */

public class Role {

    private String roleId;
    private String name;
    private List<String> requiredQualifications;

    // Standardkonstruktor (No-Arg Constructor)
    public Role() {

    }

    // Parametrisierter Konstruktor
    public Role(String roleId, String name, List<String> requiredQualifications) {
        this.roleId = roleId;
        this.name = name;
        this.requiredQualifications = (requiredQualifications != null) ? new ArrayList<>(requiredQualifications) : new ArrayList<>();
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
        // Auch hier: Sicherstellen, dass die Liste kopiert wird und nicht nur eine Referenz gesetzt wird.
        this.requiredQualifications = (requiredQualifications != null) ? new ArrayList<>(requiredQualifications) : new ArrayList<>();
    }

}
