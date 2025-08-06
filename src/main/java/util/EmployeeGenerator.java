package util;

import model.db.Employee;
import java.util.Date; // Geändert von java.time.LocalDate
import java.util.Random;
import java.util.Calendar; // Neu hinzugefügt für Datumshandhabung

public class EmployeeGenerator {

    private static final Random RANDOM = new Random();
    private static int nextEmployeeId = 1;

    public static void setStartingId(int startId) {
        nextEmployeeId = startId;
    }

    public static Employee generateRandomEmployee() {
        int id = nextEmployeeId++;

        String username = "user" + id;
        String password = "pass" + id;
        String permissionString = "A";
        String firstName = "Max";
        String lastName = "Mustermann";
        String email = username + "@example.com";
        String phoneNumber = "0123456789";
        String address = "Musterstr. " + RANDOM.nextInt(100);
        char gender = RANDOM.nextBoolean() ? 'M' : 'F';

        // ********************************************************************
        // KORREKTUR: Datumsgenerierung mit java.util.Date und Calendar
        // ********************************************************************
        Calendar cal = Calendar.getInstance();

        // Generiere ein zufälliges Geburtsdatum (z.B. 20-60 Jahre in der Vergangenheit)
        cal.setTime(new Date()); // Aktuelles Datum setzen
        cal.add(Calendar.YEAR, -(20 + RANDOM.nextInt(41))); // 20 bis 60 Jahre alt
        Date dateOfBirth = cal.getTime();

        // Generiere ein zufälliges Einstellungsdatum (z.B. 0-5 Jahre in der Vergangenheit)
        cal.setTime(new Date()); // Aktuelles Datum setzen
        cal.add(Calendar.YEAR, -RANDOM.nextInt(6)); // 0 bis 5 Jahre eingestellt
        Date hireDate = cal.getTime();

        // Sicherstellen, dass hireDate nicht vor dateOfBirth liegt (rudimentär)
        Calendar dobCal = Calendar.getInstance();
        dobCal.setTime(dateOfBirth);
        dobCal.add(Calendar.YEAR, 18); // Angenommen, man wird nicht vor 18 eingestellt
        if (hireDate.before(dobCal.getTime())) {
            cal.setTime(dobCal.getTime());
            cal.add(Calendar.MONTH, RANDOM.nextInt(12)); // Füge bis zu 12 Monate hinzu
            hireDate = cal.getTime();
        }
        // ********************************************************************

        String[] employmentStatuses = {"Active", "On Leave", "Terminated", "Probation", "Retired"};
        String employmentStatus = employmentStatuses[RANDOM.nextInt(employmentStatuses.length)];

        String departmentId = "DEP" + (RANDOM.nextInt(5) + 1);
        String teamId = "TEAM" + (RANDOM.nextInt(10) + 1);
        String roleId = "ROLE" + (RANDOM.nextInt(3) + 1);
        String qualifications = "Java, SQL";
        String completedTrainings = "Onboarding";
        Integer managerId = null;
        boolean isItAdmin = RANDOM.nextBoolean();
        boolean isHr = RANDOM.nextBoolean();
        boolean isHrHead = RANDOM.nextBoolean();
        boolean isManager = RANDOM.nextBoolean();

        return new Employee(
                id,
                username,
                password,
                permissionString,
                firstName,
                lastName,
                email,
                phoneNumber,
                dateOfBirth, // java.util.Date
                address,
                gender,
                hireDate, // java.util.Date
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
}
