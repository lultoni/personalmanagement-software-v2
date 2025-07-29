package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.json.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mit dieser Klasse werden die Unternehmensstrukturen eingelesen in das Programm.
 * Klasse wird statisch verwendet.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 1.1
 * @since 2025-07-28
 */
public class JsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String JSON_PFAD = "src/main/resources/json/";

    private static Company company;
    private static HashMap<String, Department> departmentMap;
    private static HashMap<String, Qualification> qualificationMap;
    private static HashMap<String, Role> roleMap;
    private static HashMap<String, Team> teamMap;

    public static Company getCompany() throws IOException {
        if (company == null) {
            ladeCompany();
        }
        return company;
    }

    public static Department findeDepartment(String id) throws IOException {
        if (departmentMap == null) {
            ladeDepartments();
        }
        return departmentMap.get(id);
    }

    public static Qualification findeQualification(String id) throws IOException {
        if (qualificationMap == null) {
            ladeQualifications();
        }
        return qualificationMap.get(id);
    }

    public static Role findeRole(String id) throws IOException {
        if (roleMap == null) {
            ladeRoles();
        }
        return roleMap.get(id);
    }

    public static Team findeTeam(String id) throws IOException {
        if (teamMap == null) {
            ladeTeams();
        }
        return teamMap.get(id);
    }

    private static void ladeCompany() throws IOException {
        company = mapper.readValue(new File(JSON_PFAD + "Company.json"), Company.class);
    }

    private static void ladeDepartments() throws IOException {
        ArrayList<Department> departments = mapper.readValue(
                new File(JSON_PFAD + "Department.json"),
                new TypeReference<>() {});
        departmentMap = new HashMap<>();
        for (Department d : departments) {
            departmentMap.put(d.getDepartmentId(), d);
        }
    }

    private static void ladeTeams() throws IOException {
        ArrayList<Team> teams = mapper.readValue(
                new File(JSON_PFAD + "Team.json"),
                new TypeReference<>() {});
        teamMap = new HashMap<>();
        for (Team t : teams) {
            teamMap.put(t.getTeamId(), t);
        }
    }

    private static void ladeRoles() throws IOException {
        ArrayList<Role> roles = mapper.readValue(
                new File(JSON_PFAD + "Role.json"),
                new TypeReference<>() {});
        roleMap = new HashMap<>();
        for (Role r : roles) {
            roleMap.put(r.getroleId(), r);
        }
    }

    private static void ladeQualifications() throws IOException {
        ArrayList<Qualification> qualifications = mapper.readValue(
                new File(JSON_PFAD + "Qualification.json"),
                new TypeReference<>() {});
        qualificationMap = new HashMap<>();
        for (Qualification q : qualifications) {
            qualificationMap.put(q.getRoleId(), q);
        }
    }
}
