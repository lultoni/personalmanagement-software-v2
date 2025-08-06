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

/*
 * Diese Klasse generiert vollständig befüllte Employee-Objekte
 * basierend auf dynamischen Daten (mittels Faker) und der geladenen Unternehmensstruktur.
 * Sie kümmert sich NICHT um die Persistenz in der Datenbank.
 * Die erzeugten Employee-Objekte müssen anschließend über einen DAO gespeichert werden.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 2.3 (Hinzufügung von hr- und hrHead-Logik basierend auf DepartmentId und TeamId)
 * @since 2025-07-30
 */
public class EmployeeGenerator {

    private static final Random RANDOM = new Random();
    private final Faker faker;
    private final ObjectMapper objectMapper;
    private EmployeeDao  employeeDao;

    // Private Referenzen für die Unternehmensstruktur, die nur einmalig geladen werden
    private final Company mainCompany;
    private final List<Department> allDepartmentsIDs;
    private final List<Role> allRolesIDs;
    private final List<Qualification> allQualificationsIDs;
    private final List<Team> allTeamsIDs;

    CompanyStructureManager manager = CompanyStructureManager.getInstance();

    /**
     * Konstruktor des EmployeeDataGenerators.
     * Beim Erstellen einer Instanz werden die benötigten Daten aus dem CompanyStructureManager geladen.
     * Initialisiert Faker (mit deutschem Locale) und ObjectMapper.
     *
     * @throws IOException Wenn beim Laden der Unternehmensstruktur ein Fehler auftritt.
     */
    public EmployeeGenerator(EmployeeDao employeeDao) throws IOException {
        this.employeeDao = employeeDao;
        // Initialisiere Faker und ObjectMapper direkt im Konstruktor
        this.faker = new Faker(new Locale("de", "DE")); // Faker mit deutscher Lokalisierung
        this.objectMapper = new ObjectMapper(); // Standard ObjectMapper

        // Lade Unternehmensstruktur
        this.mainCompany = manager.getCompany();
        this.allDepartmentsIDs = new ArrayList<>(manager.getAllDepartments());
        this.allRolesIDs = new ArrayList<>(manager.getAllRoles());
        this.allQualificationsIDs = new ArrayList<>(manager.getAllQualifications());
        this.allTeamsIDs = new ArrayList<>(manager.getAllTeams());

        if (mainCompany == null || allDepartmentsIDs.isEmpty() || allRolesIDs.isEmpty()) {
            throw new IllegalStateException("Unternehmensstruktur unvollständig geladen. Kann keine Mitarbeiter generieren.");
        }
    }

    /**
     * Generiert ein einzelnes Employee-Objekt mit dynamischen Daten.
     * Die generierte ID für den Mitarbeiter ist 0, da diese von der Datenbank vergeben wird.
     *
     * @param index Ein optionaler Index, der für die Generierung von eindeutigen Benutzernamen verwendet werden kann.
     * @return Ein fertig befülltes Employee-Objekt.
     */
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

        Department randomDepartment = allDepartmentsIDs.get(RANDOM.nextInt(allDepartmentsIDs.size()));
        Role randomRole = allRolesIDs.get(RANDOM.nextInt(allRolesIDs.size()));
        Team randomTeam = null;
        if (!allTeamsIDs.isEmpty()) {
            randomTeam = allTeamsIDs.get(RANDOM.nextInt(allTeamsIDs.size()));
        }

        String RoleId = randomRole.getroleId();

        String qualificationsJson = "[]";
        String employeeQualificationId = RoleId;
        qualificationsJson = String.valueOf(manager.getRequiredSkillsForQualification(employeeQualificationId));

        String username = firstName.toLowerCase() + "." + lastName.toLowerCase();


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -RANDOM.nextInt(5));
        Date hireDate = cal.getTime();

        String employmentStatus = RANDOM.nextBoolean() ? "Active" : "On Leave";
        Integer managerId = null;

        // Bestimme die Booleans itAdmin, hr, hrHead, isManager

        // Lokale Variablen für departmentId und teamId für bessere Lesbarkeit
        String departmentId = randomDepartment.getDepartmentId();
        String teamId = (randomTeam != null) ? randomTeam.getTeamId() : null;

        // Logik für isManager (Team-ID endet mit -lead)
        boolean isManager = (teamId != null && teamId.endsWith("-lead"));

        // Logik für itAdmin (Department-ID endet mit -it)
        boolean itAdmin = false;
        if(departmentId.endsWith("-it")){
            itAdmin = true;
        };

        // Logik für hr (Department-ID endet mit -hr)
        boolean hr = false; // Standardwert
        if (departmentId != null && departmentId.endsWith("-hr")) {
            hr = true;
        }

        // Logik für hrHead (Department-ID endet mit -hr UND Team-ID endet mit -lead)
        boolean hrHead = false; // Standardwert
        if (hr && isManager) { // Wenn bereits HR und auch Manager ist
            hrHead = true;
        }


        return new Employee(
                username,
                "123456",
                PermissionChecker.getEmployeePermissionString(RoleId, departmentId),
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
                RoleId,
                qualificationsJson,
                "[]",
                managerId,
                itAdmin,
                hr,       // Der neu berechnete Wert für hr
                hrHead,   // Der neu berechnete Wert für hrHead
                isManager
        );
    }
}