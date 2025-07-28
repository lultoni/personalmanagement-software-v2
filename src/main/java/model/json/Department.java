package model.json;

import java.util.ArrayList;

/**
 * Department Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */
public class Department {

    private String departmentId;
    private String name;
    private ArrayList<String> teams;

    public Department(String departmentId, String name, ArrayList<String> teams) {
        this.departmentId = departmentId;
        this.name = name;
        this.teams = teams;
    }

    public String getdepartmentId() {
        return departmentId;
    }

    public void setdepartmentId(String departmentId) {
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
        this.teams = teams;
    }

}
