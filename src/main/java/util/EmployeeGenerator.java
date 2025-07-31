package util;

import core.CompanyStructureManager;
import model.db.Employee;
import model.json.Company;
import model.json.Department;
import model.json.Qualification;
import model.json.Role;
import model.json.Team;

import com.github.javafaker.Faker; // <-- Direkter Import für Faker
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- Direkter Import für ObjectMapper

import java.io.IOException;
import java.time.LocalDate; // Für modernes Datum
import java.time.ZoneId;    // Für LocalDate zu Date Konvertierung
import java.util.*;

/**
 * Diese Klasse generiert vollständig befüllte Employee-Objekte
 * basierend auf dynamischen Daten (mittels Faker) und der geladenen Unternehmensstruktur.
 * Sie kümmert sich NICHT um die Persistenz in der Datenbank.
 * Die erzeugten Employee-Objekte müssen anschließend über einen DAO gespeichert werden.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 2.0 (Faker und ObjectMapper direkt integriert)
 * @since 2025-07-30
 */
public class EmployeeGenerator {

    private static final Random RANDOM = new Random();
    private final Faker faker; // Faker-Instanz direkt hier
    private final ObjectMapper objectMapper; // ObjectMapper-Instanz direkt hier

    // Private Referenzen für die Unternehmensstruktur, die nur einmalig geladen werden
    private final Company mainCompany;
    private final List<Department> allDepartments;
    private final List<Role> allRoles;
    private final List<Qualification> allQualifications;
    private final List<Team> allTeams;

    /**
     * Konstruktor des EmployeeDataGenerators.
     * Beim Erstellen einer Instanz werden die benötigten Daten aus dem CompanyStructureManager geladen.
     * Initialisiert Faker (mit deutschem Locale) und ObjectMapper.
     *
     * @throws IOException Wenn beim Laden der Unternehmensstruktur ein Fehler auftritt.
     */
    public EmployeeGenerator() throws IOException {
        // Initialisiere Faker und ObjectMapper direkt im Konstruktor
        this.faker = new Faker(new Locale("de", "DE")); // Faker mit deutscher Lokalisierung
        this.objectMapper = new ObjectMapper(); // Standard ObjectMapper

        // Lade Unternehmensstruktur
        CompanyStructureManager manager = CompanyStructureManager.getInstance();
        this.mainCompany = manager.getCompany();
        this.allDepartments = new ArrayList<>(manager.getAllDepartments());
        this.allRoles = new ArrayList<>(manager.getAllRoles());
        this.allQualifications = new ArrayList<>(manager.getAllQualifications());
        this.allTeams = new ArrayList<>(manager.getAllTeams());

        if (mainCompany == null || allDepartments.isEmpty() || allRoles.isEmpty()) {
            throw new IllegalStateException("Unternehmensstruktur unvollständig geladen. Kann keine Mitarbeiter generieren.");
        }
    }

    /**
     * Generiert ein einzelnes Employee-Objekt mit dynamischen Daten.
     * Die generierte ID für den Mitarbeiter ist 0, da diese von der Datenbank vergeben wird.
     *
     * @param index Ein optionaler Index, der für die Generierung von eindeutigen Benutzernamen verwendet werden kann.
     * @param availableFirstNames Eine Liste von verfügbaren Vornamen.
     * @param availableLastNames Eine Liste von verfügbaren Nachnamen.
     * @return Ein fertig befülltes Employee-Objekt.
     */
    public Employee generateSingleEmployee(int index, List<String> availableFirstNames, List<String> availableLastNames) {
        if (availableFirstNames == null || availableFirstNames.isEmpty() || availableLastNames == null || availableLastNames.isEmpty()) {
            throw new IllegalArgumentException("Listen für Vornamen und/oder Nachnamen dürfen nicht leer sein.");
        }

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
        Department randomDepartment = allDepartments.get(RANDOM.nextInt(allDepartments.size()));
        Role randomRole = allRoles.get(RANDOM.nextInt(allRoles.size()));
        Team randomTeam = null;
        if (!allTeams.isEmpty()) {
            randomTeam = allTeams.get(RANDOM.nextInt(allTeams.size()));
        }

        // Zufällige Qualifikationen für den Mitarbeiter auswählen und als JSON-String konvertieren
        List<String> employeeQualificationIds = new ArrayList<>();
        int numQualsForEmployee = RANDOM.nextInt(4); // 0 bis 3 Qualifikationen pro Mitarbeiter
        for (int q = 0; q < numQualsForEmployee; q++) {
            if (!allQualifications.isEmpty()) {
                employeeQualificationIds.add(allQualifications.get(RANDOM.nextInt(allQualifications.size())).getRoleId());
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

        // Erstelle das Employee-Objekt
        return new Employee(
                false, username,
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
                managerId,
                employeeManager, this);
    }
}
