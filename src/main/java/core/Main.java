package core;

import db.DatabaseManager;
import db.dao.EmployeeDao;
import gui.GuiManager;
import gui.views.LoginView;
import util.EmployeeGenerator;

/**
 * Hauptklasse der HR-Management-Software.
 * Diese Klasse ist der Startpunkt für die gesamte Anwendung.
 *
 * @author Elias Glauert
 * @version 1.7
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
    public static void main(String[] args) {

        System.out.println("Initialisiere Kernkomponenten...");

        dbManager = new DatabaseManager(false);
        dbManager.setupDatabase();
        backupManager = new DatabaseManager(true);
        backupManager.setupDatabase();

        EmployeeManager employeeManager = new EmployeeManager(null);
        EmployeeDao employeeDao = new EmployeeDao(dbManager, employeeManager);
        employeeManager.setEmployeeDao(employeeDao);
        employeeManager.setUpEmployees();

        EventManager eventManager = new EventManager(null, null, dbManager, backupManager);
        NotificationManager notificationManager = new NotificationManager(eventManager);
        eventManager.setNotificationManager(notificationManager);

//        if (!employeeManager.hasEmployeesGenerated()) {
//            EmployeeGenerator.generateEmployees(dbManager, employeeManager, employeeDao);
//            eventManager.callEvent("createBackup", null);
//            dbManager.printTable("EMPLOYEES");
//            backupManager.printTable("EMPLOYEES");
//        }

        loginManager = new LoginManager(employeeManager, eventManager);

        System.out.println("Starte grafische Benutzeroberfläche...");
        GuiManager guiManager = new GuiManager(eventManager, loginManager);
        GuiManager.setInstance(guiManager);
        eventManager.setGuiManager(guiManager);

        eventManager.callEvent("changeView", new Object[]{new LoginView(loginManager)});

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
