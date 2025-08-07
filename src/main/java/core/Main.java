package core;

import db.DatabaseManager;
import db.dao.EmployeeDao;
import gui.GuiManager;
import gui.views.LoginView;
import util.EmployeeCreationService;

import java.io.IOException;

/**
 * Hauptklasse der HR-Management-Software.
 * Diese Klasse ist der Startpunkt für die gesamte Anwendung.
 *
 * @author Dorian Gläske, Elias Glauert
 * @version 1.8
 * @since 2025-07-05
 */
public class Main {

    /**
     * Nur für das korrekte Schließen des Programms wird der DatabaseManager als Variable hier erstellt.
     */
    private static DatabaseManager dbManager;
    private static DatabaseManager backupManager;
    private static LoginManager loginManager;

    /**
     * Die main-Methode ist der Startpunkt des Programms.
     * Hier werden die zentralen Manager initialisiert und das GUI gestartet.
     *
     * @param args Kommandozeilenargumente (werden in dieser Anwendung nicht verwendet).
     * @author Elias Glauert
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Initialisiere Kernkomponenten...");

        //  DatabaseManager initialisieren
        dbManager = new DatabaseManager(false);
        dbManager.setupDatabase();
        backupManager = new DatabaseManager(true);
        backupManager.setupDatabase();

        // EmployeeManager UND EmployeeDao initialisieren, die sich gegenseitig benötigen
        // Beachte: Der EmployeeManager-Konstruktor benötigt den EmployeeDao
        EmployeeManager employeeManager = new EmployeeManager(null, dbManager); // Temporäre Initialisierung ohne DAO
        EmployeeDao employeeDao = new EmployeeDao(dbManager, employeeManager);
        employeeManager.setEmployeeDao(employeeDao);// Jetzt den DAO setzen
        System.out.println("Dao gestzt"+employeeDao);

        // Mitarbeiter erstellen und laden
        // Der EmployeeCreationService benötigt den EmployeeManager und den EmployeeDao
        EmployeeCreationService employeeCreationService = new EmployeeCreationService(dbManager, employeeManager, employeeDao);
        employeeCreationService.generate_x_Employees(250);

        // Mitarbeiter aus der DB in den EmployeeManager laden
        employeeManager.setUpEmployees();
        employeeManager.saveEmployeesToTxt("Employee_Info.txt"); // Dateiendung hinzugefügt

        // Andere Manager und GUI initialisieren
        EventManager eventManager = new EventManager(null, null, dbManager, backupManager, employeeManager);
        NotificationManager notificationManager = new NotificationManager(eventManager);
        eventManager.setNotificationManager(notificationManager);

        loginManager = new LoginManager(employeeManager, eventManager);

        System.out.println("Starte grafische Benutzeroberfläche...");
        GuiManager guiManager = new GuiManager(eventManager, loginManager, employeeManager);
        GuiManager.setInstance(guiManager);
        eventManager.setGuiManager(guiManager);

        eventManager.callEvent("changeView", new Object[]{new LoginView(eventManager, loginManager)});

        System.out.println("Anwendung erfolgreich gestartet.\n\n");
    }

    /**
     * Debug Methode für das Ausgeben von args in der Konsole.
     * @param args Die Argumente aus einer Methode, welche zu einer Liste in einem String verwandelt werden sollen.
     * @return String, welcher die Liste der args-Objekte ist.
     * @author Elias Glauert
     */
    public static String argsToString(Object[] args) {
        if (args == null) return "null";
        String ret_string = "[";
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];
            ret_string += object.toString() + (i == args.length - 1 ? "" : ", ");
        }
        return ret_string + "]";
    }

    /**
     * Beendet das Programm, nachdem die Datenbankverbindungen getrennt wurden und der User ausgeloggt wurde.
     * @author Elias Glauert
     */
    public static void exitProgram() {
        System.out.println("\nStarting Exit Process...");
        dbManager.disconnect();
        backupManager.disconnect();
        loginManager.logout();
        System.exit(0);
    }
}
