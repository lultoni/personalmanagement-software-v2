package util; // Passe dies an dein tatsächliches Paket an, z.B. util oder core

import model.db.Employee; // Importiere deine Employee-Klasse
import java.time.LocalDate; // Für LocalDate
import java.util.Random; // Für Zufallszahlen
// import java.util.Calendar; // Nicht mehr benötigt, kann entfernt werden
// import java.util.Date; // Nicht mehr benötigt, kann entfernt werden

// Annahme: Du hast eine Methode, um die nächste verfügbare ID zu erhalten
// Annahme: Du hast Zugriff auf EmployeeManager oder eine ähnliche Logik zum Hinzufügen von Mitarbeitern

public class EmployeeGenerator {

    private static final Random RANDOM = new Random();

    // Beispielmethode zum Generieren eines einzelnen Mitarbeiters
    public static Employee generateRandomEmployee(int currentMaxId) {
        String username = "user" + (currentMaxId + 1);
        String password = "pass" + (currentMaxId + 1);
        String permissionString = "A"; // Beispiel
        String firstName = "Max";
        String lastName = "Mustermann";
        String email = username + "@example.com";
        String phoneNumber = "0123456789";
        String address = "Musterstr. " + RANDOM.nextInt(100);
        char gender = RANDOM.nextBoolean() ? 'M' : 'F'; // Zufälliges Geschlecht

        // ********************************************************************
        // KORREKTUR: Datumsgenerierung mit LocalDate
        // ********************************************************************
        // Generiere ein zufälliges Geburtsdatum (z.B. 20-60 Jahre in der Vergangenheit)
        LocalDate dateOfBirth = LocalDate.now().minusYears(20 + RANDOM.nextInt(41)); // 20 bis 60 Jahre alt

        // Generiere ein zufälliges Einstellungsdatum (z.B. 0-5 Jahre in der Vergangenheit)
        LocalDate hireDate = LocalDate.now().minusYears(RANDOM.nextInt(6)); // 0 bis 5 Jahre eingestellt
        // Sicherstellen, dass hireDate nicht vor dateOfBirth liegt (rudimentär)
        if (hireDate.isBefore(dateOfBirth.plusYears(18))) { // Angenommen, man wird nicht vor 18 eingestellt
            hireDate = dateOfBirth.plusYears(18).plusMonths(RANDOM.nextInt(12));
        }
        // ********************************************************************

        String employmentStatus = "Active";
        String departmentId = "DEP" + (RANDOM.nextInt(5) + 1); // Beispiel: DEP1-DEP5
        String teamId = "TEAM" + (RANDOM.nextInt(10) + 1); // Beispiel: TEAM1-TEAM10
        String roleId = "ROLE" + (RANDOM.nextInt(3) + 1); // Beispiel: ROLE1-ROLE3
        String qualifications = "Java, SQL";
        String completedTrainings = "Onboarding";
        Integer managerId = null; // Kann null sein
        boolean isItAdmin = RANDOM.nextBoolean();
        boolean isHr = RANDOM.nextBoolean();
        boolean isHrHead = RANDOM.nextBoolean();
        boolean isManager = RANDOM.nextBoolean();

        return new Employee(
                currentMaxId + 1, // ID
                username,
                password,
                permissionString,
                firstName,
                lastName,
                email,
                phoneNumber,
                dateOfBirth, // LocalDate
                address,
                gender,
                hireDate, // LocalDate
                employmentStatus,
                departmentId,
                teamId,
                roleId,
                qualifications,
                completedTrainings,
                managerId,
                isItAdmin,
                isHr,
                isHrHead,
                isManager
        );
    }

    // Beispiel für eine Methode, die mehrere Mitarbeiter generiert und hinzufügt
    // Annahme: Du hast eine Instanz von EmployeeManager
    /*
    public static void generateAndAddEmployees(int numberOfEmployees, EmployeeManager employeeManager) {
        for (int i = 0; i < numberOfEmployees; i++) {
            // Annahme: employeeManager.getNextAvailableId() gibt die nächste ID zurück
            // oder du musst die ID-Generierung hier selbst verwalten
            int nextId = employeeManager.getEmployees().size() + 1; // Vereinfacht
            Employee newEmployee = generateRandomEmployee(nextId - 1); // generateRandomEmployee erwartet die Basis-ID

            employeeManager.addEmployee(
                newEmployee.getUsername(),
                newEmployee.getPassword(),
                newEmployee.getPermissionString(),
                newEmployee.getFirstName(),
                newEmployee.getLastName(),
                newEmployee.getEmail(),
                newEmployee.getPhoneNumber(),
                newEmployee.getDateOfBirth(),
                newEmployee.getAddress(),
                newEmployee.getGender(),
                newEmployee.getHireDate(),
                newEmployee.getEmploymentStatus(),
                newEmployee.getDepartmentId(),
                newEmployee.getTeamId(),
                newEmployee.getRoleId(),
                newEmployee.getQualifications(),
                newEmployee.getCompletedTrainings(),
                newEmployee.getManagerId(),
                newEmployee.isItAdmin(),
                newEmployee.isHr(),
                newEmployee.isHrHead(),
                newEmployee.isManager()
            );
        }
        System.out.println(numberOfEmployees + " zufällige Mitarbeiter generiert und hinzugefügt.");
    }
    */
}
