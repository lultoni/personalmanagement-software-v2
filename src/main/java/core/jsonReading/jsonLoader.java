package core.jsonReading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class jsonLoader {
    private ObjectMapper mapper = new ObjectMapper();

    private Company company;
    private Map<String, Department> departmentMap = new HashMap<>();
    private Map<String, Qualification> qualificationMap = new HashMap<>();
    private Map<String, Role> roleMap = new HashMap<>();
    private Map<String, Team> teamMap = new HashMap<>();

    public void ladeCompany(String pfad) throws IOException {
        company = mapper.readValue(new File(pfad), Company.class);
    }

    public void ladeDepartments(String pfad) throws IOException {
        List<Department> departments = mapper.readValue(new File(pfad), new TypeReference<List<Department>>() {});
        for (Department d : departments) {
            departmentMap.put(d.getdepartmentId(), d);
        }
    }
    public void ladeQualifications(String pfad) throws IOException {
        List<Qualification> qualifications = mapper.readValue(new File(pfad), new TypeReference<List<Qualification>>() {});
        for (Qualification q : qualifications) {
            qualificationMap.put(q.getRoleId(), q);
        }
    }

    public void ladeRoles(String pfad) throws IOException {
        List<Role> roles = mapper.readValue(new File(pfad), new TypeReference<List<Role>>() {});
        for (Role r : roles) {
            roleMap.put(r.getroleId(), r);
        }
    }

    public void ladeTeams(String pfad) throws IOException {
        List<Team> teams = mapper.readValue(new File(pfad), new TypeReference<List<Team>>() {});
        for (Team t : teams) {
            teamMap.put(t.getTeamId(), t);
        }
    }

    // Zugriffsmethoden
    public Company getCompany() { return company; }
    public Department findeDepartment(String id) { return departmentMap.get(id); }
    public Qualification findeQualification(String id) { return qualificationMap.get(id); }
    public Role findeRole(String id) { return roleMap.get(id); }
    public Team findeTeam(String id) { return teamMap.get(id); }

}


