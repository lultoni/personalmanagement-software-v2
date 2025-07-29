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
    private ArrayList<String> roles;

    public Team(String teamId, String name, ArrayList<String> roles) {
        this.teamId = teamId;
        this.name = name;
        this.roles = roles;
    }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ArrayList<String> getRoles() { return roles; }
    public void setRoles(ArrayList<String> roles) { this.roles = roles; }
}
