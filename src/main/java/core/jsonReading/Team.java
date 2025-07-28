package core.jsonReading;

import java.util.List;

public class Team {
    private String teamId; // JSON key: "team-id"
    private String name;
    private List<String> roles;
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
