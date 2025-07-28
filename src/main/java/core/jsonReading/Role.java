package core.jsonReading;

import java.util.List;

public class Role {
    private String roleId;
    private String name;
    private List<String> requiredQualifications;
    public String getroleId() { return roleId; }
    public void setroleId(String id) { this.roleId = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getRequiredQualifications() { return requiredQualifications; }
    public void setRequiredQualifications(List<String> requiredQualifications) {
        this.requiredQualifications = requiredQualifications;
    }
}

