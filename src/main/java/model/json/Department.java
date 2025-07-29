package model.json;

import java.util.ArrayList;

/**
 * Department Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.1
 * @since 2025-07-28
 */

public class Department {

    private String departmentId;
    private String name;
    private ArrayList<String> teams;

    // Standardkonstruktor (No-Arg Constructor)
    public Department() {
        this.teams = new ArrayList<>();
    }

    // Parametrisierter Konstruktor
    public Department(String departmentId, String name, ArrayList<String> teams) {

    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<String> teams) {
        // Auch hier: Sicherstellen, dass die Liste kopiert wird und nicht nur eine Referenz gesetzt wird.
        this.teams = (teams != null) ? new ArrayList<>(teams) : new ArrayList<>();
    }
}
