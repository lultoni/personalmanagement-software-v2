package util; // Dein gewähltes Paket

import core.EmployeeManager;
import db.DatabaseManager;
import db.dao.EmployeeDao;
import gui.views.EmployeeInfoView;
import model.db.Employee;
import org.h2.engine.Database;
import org.h2.jmx.DatabaseInfoMBean;
import util.EmployeeGenerator; // Importiere deinen EmployeeGenerator

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service-Klasse zur Generierung einer bestimmten Anzahl von Mitarbeiter-Objekten.
 * Kapselt die Logik des EmployeeGenerators und stellt eine einfache Methode zur Verfügung,
 * um mehrere Mitarbeiter auf einmal zu erstellen.
 * Diese Klasse ist NICHT für die Persistenz in der Datenbank zuständig.
 *
 * @author Elias Glauert (basierend auf vorherigen Diskussionen)
 * @version 1.0
 * @since 2025-07-31
 */
public class EmployeeCreationService {

    private final EmployeeGenerator employeeGenerator;
    private DatabaseManager databaseManager;
    private EmployeeManager employeeManager;
    private EmployeeDao employeeDao;

    /**
     * Konstruktor für EmployeeCreationService.
     * Initialisiert eine Instanz des EmployeeGenerators, der die Unternehmensstruktur lädt.
     *
     * @throws IOException Wenn beim Initialisieren des EmployeeGenerators ein Fehler auftritt (z.B. beim Laden der JSON-Dateien).
     * @throws IllegalStateException Wenn die Unternehmensstruktur unvollständig geladen wurde.
     */
    public EmployeeCreationService(DatabaseManager databaseManager, EmployeeManager employeeManager, EmployeeDao employeeDao) throws IOException, IllegalStateException {
        this.employeeGenerator = new EmployeeGenerator(employeeDao);
        this.databaseManager = databaseManager;
        this.employeeDao = employeeDao;
        System.out.println("EmployeeCreationService: EmployeeGenerator erfolgreich initialisiert.");
    }

    /**
     * Generiert eine spezifizierte Anzahl von Employee-Objekten.
     * Die 'isManager'-Eigenschaft der generierten Mitarbeiter wird im EmployeeGenerator
     * basierend auf der 'teamId' gesetzt (endet sie mit '-lead', ist isManager true).
     *
     * @param numberOfEmployees Die gewünschte Anzahl der zu generierenden Mitarbeiter.
     * @return Eine Liste von generierten Employee-Objekten.
     */
    public List<Employee> generateEmployees(int numberOfEmployees) {
        if (numberOfEmployees < 0) {
            throw new IllegalArgumentException("Anzahl der Mitarbeiter darf nicht negativ sein.");
        }

        List<Employee> generatedEmployees = new ArrayList<>(numberOfEmployees); // Vorbelegung der Kapazität
        System.out.println("\nEmployeeCreationService: Starte Generierung von " + numberOfEmployees + " Mitarbeitern...");

        for (int i = 0; i < numberOfEmployees; i++) {
            // Rufe die generateSingleEmployee Methode des EmployeeGenerators auf.
            // Diese Methode enthält jetzt die Logik für 'isManager'.
            Employee employee = employeeGenerator.generateSingleEmployee(i);
            employee.setId(1000000+i);
            generatedEmployees.add(employee);
            // Optional: Fortschrittsanzeige
            if ((i + 1) % 10 == 0 || (i + 1) == numberOfEmployees) {
                System.out.println("  " + (i + 1) + " Mitarbeiter generiert.");
            }
        }
        System.out.println("EmployeeCreationService: Generierung abgeschlossen.");
        return generatedEmployees;
    }

    // TODO wandel diese methode dazu um, dass einfach nur die andere methode aufgerufen wird mit dem parameter 100
    public void generate100Employees() {
        try {
            int employeesToCreate = 100; // Oder jede andere gewünschte Menge
            List<Employee> employees = generateEmployees(employeesToCreate);

            System.out.println("\n--- Zusammenfassung ---");
            System.out.println("Total generierte Mitarbeiter: " + employees.size());

            // Zeige die ersten 5 Mitarbeiter zur Verifizierung
            System.out.println("\nDie ersten 5 generierten Mitarbeiter:");
            for (int i = 0; i < Math.min(5, employees.size()); i++) {
                Employee emp = employees.get(i);
                // Zusätzliche Ausgabe, um 'isManager' zu prüfen
                System.out.println("  " + emp.toString() + " | Is Manager: " + emp.isManager());
            }

            for (Employee emp : employees) {
                employeeDao.addEmployeeToDb(emp);
            }

        } catch (IllegalStateException e) {
            System.err.println("Fehler in der Unternehmensstruktur (CompanyStructureManager): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
            e.printStackTrace();
        }
    }
}