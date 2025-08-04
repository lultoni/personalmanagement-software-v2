package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.json.Company;
import model.json.Department;
import model.json.Qualification;
import model.json.QualificationsContainer;
import model.json.Role;
import model.json.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * Mit dieser Klasse werden die Unternehmensstrukturen eingelesen in das Programm.
 * Klasse wird statisch verwendet.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 1.2 (Angepasst für QualificationsContainer und Company-Fehler)
 * @since 2025-07-29
 */
public class JsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String JSON_PFAD = "src/main/resources/json/";

    private static Company company;
    private static HashMap<String, Department> departmentMap;
    private static HashMap<String, Qualification> qualificationMap;
    private static HashMap<String, Role> roleMap;
    private static HashMap<String, Team> teamMap;
    private static final Gson gson = new Gson();

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
        // ANPASSUNG HIER:
        // Die Company.json ist jetzt als Array von Company-Objekten strukturiert.
        // Wir lesen sie als Liste und nehmen das erste Element.
        List<Company> companyList = mapper.readValue(
                new File(JSON_PFAD + "Company.json"),
                new TypeReference<List<Company>>() {} // Wichtig: Liest ein Array von Company-Objekten
        );

        if (!companyList.isEmpty()) {
            company = companyList.get(0); // Nehme das erste (und hoffentlich einzige) Objekt aus der Liste
            System.out.println("Company '" + company.getName() + "' geladen.");
        } else {
            System.err.println("Fehler: 'Company.json' ist leer oder enthält ein leeres Array. Keine Firma geladen.");
            company = null; // Setze company auf null oder ein Standard-Company-Objekt
        }
    }

    private static void ladeDepartments() throws IOException {
        // Annahme: Department.json ist ein direktes Array von Department-Objekten.
        ArrayList<Department> departments = mapper.readValue(
                new File(JSON_PFAD + "Department.json"),
                new TypeReference<ArrayList<Department>>() {}); // Expliziter TypeReference
        departmentMap = new HashMap<>();
        for (Department d : departments) {
            departmentMap.put(d.getDepartmentId(), d);
        }
    }

    private static void ladeTeams() throws IOException {
        // Annahme: Team.json ist ein direktes Array von Team-Objekten.
        ArrayList<Team> teams = mapper.readValue(
                new File(JSON_PFAD + "Team.json"),
                new TypeReference<ArrayList<Team>>() {}); // Expliziter TypeReference
        teamMap = new HashMap<>();
        for (Team t : teams) {
            teamMap.put(t.getTeamId(), t);
        }
    }

    private static void ladeRoles() throws IOException {
        // Annahme: Role.json ist ein direktes Array von Role-Objekten.
        ArrayList<Role> roles = mapper.readValue(
                new File(JSON_PFAD + "Role.json"),
                new TypeReference<ArrayList<Role>>() {}); // Expliziter TypeReference
        roleMap = new HashMap<>();
        for (Role r : roles) {
            // Stelle sicher, dass getRoleId() korrekt ist (nicht getroleId())
            roleMap.put(r.getroleId(), r);
        }
    }

    private static void ladeQualifications() throws IOException {
        // Die JSON-Datei "Qualification.json" wird als ein Array von QualificationsContainer-Objekten gelesen.
        // Laut deinem Beispiel-JSON enthält dieses Array nur ein einziges Container-Objekt.
        List<QualificationsContainer> containers = mapper.readValue(
                new File(JSON_PFAD + "Qualification.json"),
                new TypeReference<List<QualificationsContainer>>() {}); // Wichtig: Liste von Containern

        qualificationMap = new HashMap<>();

        // Da das äußere JSON-Array nur einen Container enthält (wie in deinem Beispiel),
        // nehmen wir das erste Element.
        if (!containers.isEmpty()) {
            QualificationsContainer mainContainer = containers.get(0);
            List<Qualification> qualificationsList = mainContainer.getQualifications();

            if (qualificationsList != null) {
                for (Qualification q : qualificationsList) {
                    // Verwende die roleId als Schlüssel für die Map
                    qualificationMap.put(q.getRoleId(), q);
                }
            }
        } else {
            System.out.println("Warnung: 'Qualification.json' ist leer oder enthält keine Container.");
        }
    }
    public static Map<String, Department> getDepartmentMap() throws IOException {
        if (departmentMap == null) {
            ladeDepartments();
        }
        return departmentMap;
    }

    public static Map<String, Qualification> getQualificationMap() throws IOException {
        if (qualificationMap == null) {
            ladeQualifications();
        }
        return qualificationMap;
    }

    public static Map<String, Role> getRoleMap() throws IOException {
        if (roleMap == null) {
            ladeRoles();
        }
        return roleMap;
    }

    public static Map<String, Team> getTeamMap() throws IOException {
        if (teamMap == null) {
            ladeTeams();
        }
        return teamMap;
    }
    public static List<Map<String, Object>> parseJsonArray(String json) {
        Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
}
