package util;

import core.CompanyStructureManager;
import db.dao.EmployeeDao; // Keep this for inheritance
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
 * @version 2.0 (Faker und ObjectMapper direkt integriert)
 * @since 2025-07-30
 */
public class EmployeeGenerator extends EmployeeDao {

    private static final Random RANDOM = new Random();
    private final Faker faker;
    private final ObjectMapper objectMapper;

    // Private Referenzen für die Unternehmensstruktur, die nur einmalig geladen werden
    private final Company mainCompany;
    private final List<Department> allDepartmentsIDs;
    private final List<Role> allRolesIDs;
    private final List<Qualification> allQualificationsIDs;
    private final List<Team> allTeamsIDs;

    /**
     * Konstruktor des EmployeeDataGenerators.
     * Beim Erstellen einer Instanz werden die benötigten Daten aus dem CompanyStructureManager geladen.
     * Initialisiert Faker (mit deutschem Locale) und ObjectMapper.
     *
     * @throws IOException Wenn beim Laden der Unternehmensstruktur ein Fehler auftritt.
     */
    public EmployeeGenerator() throws IOException {
        super(); // Calls the no-arg constructor of EmployeeDao
        // Initialisiere Faker und ObjectMapper direkt im Konstruktor
        this.faker = new Faker(new Locale("de", "DE")); // Faker mit deutscher Lokalisierung
        this.objectMapper = new ObjectMapper(); // Standard ObjectMapper

        // Lade Unternehmensstruktur
        CompanyStructureManager manager = CompanyStructureManager.getInstance();
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
                "Julian", "Hannah", "David", "Lea", "Finn", "Emily", "Moritz", "Lilly", "Tom", "Nina"
        );

        List<String> availableLastNames = Arrays.asList(
                "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Hoffmann", "Schäfer",
                "Koch", "Bauer", "Richter", "Klein", "Wolf", "Neumann", "Schwarz", "Zimmermann", "Braun", "Krüger",
                "Hofmann", "Hartmann", "Lange", "Scholz", "Krause", "Frank", "Berger", "Meier", "Fuchs", "Jung"
        );

        // zufällig auswähl aus den gegeben Namen
        String firstName = availableFirstNames.get(RANDOM.nextInt(availableFirstNames.size()));
        String lastName = availableLastNames.get(RANDOM.nextInt(availableLastNames.size()));

        // --- Generierung der persönlichen Daten direkt mit Faker ---
        // (Da wir die Namen jetzt aus Listen ziehen, nutzen wir Faker für den Rest)
        String email = faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase());
        String phoneNumber = faker.phoneNumber().phoneNumber();

        // Geburtsdatum: Faker gibt oft Date zurück, konvertieren zu LocalDate und dann wieder zu Date für Employee
        Date dobAsFakerDate = faker.date().birthday(18, 65); // Alter zwischen 18 und 65 Jahren
        LocalDate dateOfBirthAsLocalDate = dobAsFakerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date dateOfBirth = Date.from(dateOfBirthAsLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        String address = faker.address().streetAddress() + ", " + faker.address().zipCode() + " " + faker.address().city() + ", " + faker.address().country();
        char gender = faker.options().option('M', 'F'); // Zufälliges Geschlecht

        // --- Ende der direkten Faker-Generierung ---


        // Zufälliges Department, Role und Team auswählen
        Department randomDepartment = allDepartmentsIDs.get(RANDOM.nextInt(allDepartmentsIDs.size()));
        Role randomRole = allRolesIDs.get(RANDOM.nextInt(allRolesIDs.size()));
        Team randomTeam = null;
        if (!allTeamsIDs.isEmpty()) {
            randomTeam = allTeamsIDs.get(RANDOM.nextInt(allTeamsIDs.size()));
        }

        // Zufällige Qualifikationen für den Mitarbeiter auswählen und als JSON-String konvertieren
        List<String> employeeQualificationIds = new ArrayList<>();
        int numQualsForEmployee = RANDOM.nextInt(4); // 0 bis 3 Qualifikationen pro Mitarbeiter
        for (int q = 0; q < numQualsForEmployee; q++) {
            if (!allQualificationsIDs.isEmpty()) {
                employeeQualificationIds.add(allQualificationsIDs.get(RANDOM.nextInt(allQualificationsIDs.size())).getRoleId());
            }
        }
        String qualificationsJson = "[]"; // Standardwert, falls Fehler oder keine Qualifikationen
        try {
            qualificationsJson = objectMapper.writeValueAsString(employeeQualificationIds); // Nutze den internen objectMapper
        } catch (JsonProcessingException e) {
            System.err.println("Fehler beim Konvertieren von Qualifikationen zu JSON: " + e.getMessage());
        }

        // Benutzername und E-Mail generieren (mit den ausgewählten Namen)
        String username = firstName.toLowerCase() + "." + lastName.toLowerCase();
        // Optional: Füge Index hinzu, um Einzigartigkeit zu erhöhen, falls viele generiert werden
        if (index > 0) {
            username += index;
        }
        // E-Mail wurde bereits oben generiert

        // Eintrittsdatum (in den letzten 5 Jahren)
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // Aktuelles Datum
        cal.add(Calendar.YEAR, -RANDOM.nextInt(5)); // Ziehe bis zu 5 Jahre ab
        Date hireDate = cal.getTime();

        // Beschäftigungsstatus (Beispielwerte)
        String employmentStatus = RANDOM.nextBoolean() ? "Active" : "On Leave";

        // Manager-ID (Standardwert ist null, muss später aktualisiert werden, wenn Hierarchien aufgebaut werden)
        Integer managerId = null;


        // Erstelle das Employee-Objekt OHNE den EmployeeDao Parameter
        return new Employee(
                username,
                "password" + index, // Einfaches Passwort
                PermissionChecker.getEmployeePermissionString(randomRole.getroleId(), randomDepartment.getDepartmentId()),
                firstName,
                lastName,
                email,
                phoneNumber,
                dateOfBirth, // Das aus Faker generierte Datum
                address,     // Die aus Faker generierte Adresse
                gender,      // Das aus Faker generierte Geschlecht
                hireDate,
                employmentStatus,
                randomDepartment.getDepartmentId(),
                randomTeam != null ? randomTeam.getTeamId() : null,
                randomRole.getroleId(),
                qualificationsJson,
                "[]", // Completed trainings als leerer JSON-Array
                managerId
        );
    }
}