package core.jsonReading;

import java.util.List;

public class Department {
    private String departmentId;
    private String name;
    private List<String> teams;
    public String getdepartmentId() { return departmentId; }
    public void setdepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getTeams() { return teams; }
    public void setTeams(List<String> teams) { this.teams = teams; }
}
