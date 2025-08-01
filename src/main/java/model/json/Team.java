package model.json;

import java.util.ArrayList;

/**
 * Team Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */
public class Team {
    private String teamId;
    private String name;
    private ArrayList<String> roleId;


    public Team(){

    }

    public Team(String teamId, String name, ArrayList<String> roleId) {
        this.teamId = teamId;
        this.name = name;
        this.roleId = roleId;
    }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ArrayList<String> getRoleId() { return roleId; }
    public void setRoleId(ArrayList<String> roles) { this.roleId = roles; }
}
