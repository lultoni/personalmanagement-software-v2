package util;

import core.CompanyStructureManager;
import db.dao.EmployeeDao;
import model.db.Employee;
import model.json.Company;
import model.json.Department;
import model.json.Qualification;
import model.json.Role;
import model.json.Team;

import com.github.javafaker.Faker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Diese Klasse generiert vollständig befüllte Employee-Objekte
 * basierend auf dynamischen Daten (mittels Faker) und der geladenen Unternehmensstruktur.
 * Sie kümmert sich NICHT um die Persistenz in der Datenbank.
 * Die erzeugten Employee-Objekte müssen anschließend über einen DAO gespeichert werden.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 2.6 (Korrekte Generierung von Role- und Qualifikations-IDs)
 * @since 2025-08-07
 */
public class EmployeeGenerator {

    private static final Random RANDOM = new Random();
    private final Faker faker;
    private final ObjectMapper objectMapper;
    private EmployeeDao  employeeDao;

    private final Company mainCompany;
    private final List<Department> allDepartments;
    private final List<Role> allRoles;
    private final List<Qualification> allQualifications;
    private final List<Team> allTeams;
    private final List<String> allRoleIds; // Liste der validen Rollen-IDs
    private final List<String> allQualificationIds; // Liste der validen Qualifikations-IDs

    CompanyStructureManager manager = CompanyStructureManager.getInstance();

    public EmployeeGenerator(EmployeeDao employeeDao) throws IOException {
        this.employeeDao = employeeDao;
        this.faker = new Faker(new Locale("de", "DE"));
        this.objectMapper = new ObjectMapper();

        this.mainCompany = manager.getCompany();
        this.allDepartments = new ArrayList<>(manager.getAllDepartments());
        this.allRoles = new ArrayList<>(manager.getAllRoles());
        this.allQualifications = new ArrayList<>(manager.getAllQualifications());
        this.allTeams = new ArrayList<>(manager.getAllTeams());
        this.allRoleIds = manager.getAllRoles().stream().map(Role::getroleId).collect(Collectors.toList());
        this.allQualificationIds = manager.getAllQualifications().stream().map(Qualification::getRoleId).collect(Collectors.toList());


        if (mainCompany == null || allDepartments.isEmpty() || allRoles.isEmpty()) {
            throw new IllegalStateException("Unternehmensstruktur unvollständig geladen. Kann keine Mitarbeiter generieren.");
        }
    }

    public Employee generateSingleEmployee(int index) {
        List<String> availableFirstNames = Arrays.asList(
                "Lukas", "Emma", "Mia", "Noah", "Leon", "Lina", "Elias", "Paul", "Ben", "Anna",
                "Luis", "Clara", "Felix", "Marie", "Jonas", "Laura", "Max", "Mila", "Tim", "Sophie",
                "Julian", "Hannah", "David", "Lea", "Finn", "Emily", "Moritz", "Lilly", "Tom", "Nina",
                "Alexander", "Amelie", "Anton", "Elisa", "Fabian", "Helena", "Jakob", "Julia", "Karl", "Karla",
                "Kevin", "Lena", "Leo", "Leni", "Leonhard", "Magdalena", "Manuel", "Marlene", "Matteo", "Maya",
                "Michael", "Nico", "Niklas", "Paula", "Philipp", "Pia", "Rafael", "Romy", "Samira", "Sandro",
                "Sarah", "Sebastian", "Silas", "Simon", "Stella", "Stefan", "Theresa", "Valentin", "Victoria", "Vincent",
                "Yannick", "Zoe", "Emil", "Greta", "Oskar", "Frieda", "Henri", "Ida", "Mathis", "Luisa"
        );

        List<String> availableLastNames = Arrays.asList(
                "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Hoffmann", "Schäfer",
                "Koch", "Bauer", "Richter", "Klein", "Wolf", "Neumann", "Schwarz", "Zimmermann", "Braun", "Krüger",
                "Hofmann", "Hartmann", "Lange", "Scholz", "Krause", "Frank", "Berger", "Meier", "Fuchs", "Jung",
                "Baumann", "Baier", "Graf", "Winter", "Herrmann", "Pfeiffer", "Haas", "Simon", "Schulz", "Schuster",
                "Huber", "Peters", "Thomas", "Keller", "Kuhn", "Seidel", "Walter", "Jäger", "Lenz", "Gärtner",
                "Vogel", "Maier", "Winkler", "Engel", "Friedrich", "Thiel", "Kaiser", "Franke", "Scherer", "Brandt",
                "Sommer", "Otto", "Wegner", "Groß", "Hahn", "Köhler", "Lehmann", "Albrecht", "Roth", "Schröder",
                "Schmitz", "Ludwig", "Seifert", "Beck", "Eichhorn", "Sauer", "Brunner", "Götz", "Kolb", "Ebert"
        );

        String firstName = availableFirstNames.get(RANDOM.nextInt(availableFirstNames.size()));
        String lastName = availableLastNames.get(RANDOM.nextInt(availableLastNames.size()));

        String email = faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase());
        String phoneNumber = faker.phoneNumber().phoneNumber();

        Date dobAsFakerDate = faker.date().birthday(18, 65);
        LocalDate dateOfBirthAsLocalDate = dobAsFakerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date dateOfBirth = Date.from(dateOfBirthAsLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        String address = faker.address().streetAddress() + ", " + faker.address().zipCode() + " " + faker.address().city() + ", " + faker.address().country();
        char gender = faker.options().option('M', 'F');

        Department randomDepartment = allDepartments.get(RANDOM.nextInt(allDepartments.size()));

        // **Korrigierte Logik für Rollen- und Qualifikationsauswahl**

        // 1. Wähle eine zufällige, gültige Qualifikation aus der JSON-Datei
        String qualificationsJson = "[]";
        String qualificationId = null;
        String roleId = null;

        if (!allQualificationIds.isEmpty()) {
            qualificationId = allQualificationIds.get(RANDOM.nextInt(allQualificationIds.size()));

            // 2. Leite die Rollen-ID aus der Qualifikations-ID ab
            // Dies ist sicher, da wir wissen, dass die Qualifikations-ID existiert
            if (qualificationId.startsWith("qual-")) {
                roleId = qualificationId.substring("qual-".length());
            } else {
                // Fallback, wenn die ID nicht dem erwarteten Format entspricht
                roleId = qualificationId;
            }

            // Optional: Füge die Qualifikations-ID der Liste der erforderlichen Skills hinzu
            List<String> requiredSkills = new ArrayList<>();
            requiredSkills.add(qualificationId);

            try {
                qualificationsJson = objectMapper.writeValueAsString(requiredSkills);
            } catch (JsonProcessingException e) {
                System.err.println("Fehler beim Konvertieren der Qualifikationen in JSON: " + e.getMessage());
            }
        } else {
            throw new IllegalStateException("Keine Qualifikationen in der Unternehmensstruktur gefunden.");
        }

        // 3. Finde die entsprechende Rolle, um sicherzustellen, dass sie existiert
        Role randomRole = manager.findRoleById(roleId).orElse(null);
        if (randomRole == null) {
            throw new IllegalStateException("Rolle für die generierte Qualifikation nicht gefunden: " + roleId);
        }

        Team randomTeam = null;
        if (!allTeams.isEmpty()) {
            List<Team> teamsInDepartment = allTeams.stream()
                    .filter(team -> team.getTeamId().equals(randomDepartment.getDepartmentId()))
                    .collect(Collectors.toList());

            if (!teamsInDepartment.isEmpty()) {
                randomTeam = teamsInDepartment.get(RANDOM.nextInt(teamsInDepartment.size()));
            }
        }

        String username = firstName.toLowerCase() + "." + lastName.toLowerCase();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -RANDOM.nextInt(5));
        Date hireDate = cal.getTime();

        String employmentStatus = RANDOM.nextBoolean() ? "Active" : "On Leave";
        Integer managerId = null;

        String departmentId = randomDepartment.getDepartmentId();
        String teamId = (randomTeam != null) ? randomTeam.getTeamId() : null;

        boolean isManager = (teamId != null && teamId.endsWith("-lead"));

        boolean itAdmin = false;
        if(departmentId.endsWith("-it")){
            itAdmin = true;
        };

        boolean hr = false;
        if (departmentId != null && departmentId.endsWith("-hr")) {
            hr = true;
        }

        boolean hrHead = false;
        if (hr && isManager) {
            hrHead = true;
        }

        return new Employee(
                username,
                "123456",
                PermissionChecker.getEmployeePermissionString(roleId, departmentId),
                firstName,
                lastName,
                email,
                phoneNumber,
                dateOfBirth,
                address,
                gender,
                hireDate,
                employmentStatus,
                departmentId,
                teamId,
                roleId,
                qualificationsJson,
                "[]",
                managerId,
                itAdmin,
                hr,
                hrHead,
                isManager
        );
    }
}