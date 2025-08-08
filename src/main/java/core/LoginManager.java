package core;

import gui.views.LoginView;
import model.db.Employee;
import util.PersistentInformationReader;

import java.util.ArrayList;
import java.util.List;

/**
 * LoginManager der den Login-Prozess verwaltet.
 *
 * @author Elias Glauert
 * @version 1.4
 * @since 2025-07-11
 */
public class LoginManager {

    public static final int LOGIN_SUCCESS = 101;
    public static final int USERNAME_NOT_FOUND = 401;
    public static final int PASSWORD_INCORRECT = 402;

    EmployeeManager employeeManager;
    EventManager eventManager;

    /**
     * Konstruktor für den Login Manager
     * @author Elias Glauert
     */
    public LoginManager(EmployeeManager employeeManager, EventManager eventManager) {

        this.employeeManager = employeeManager;
        this.eventManager = eventManager;
        PersistentInformationReader.clearLoggedInUser();

    }

    /**
     * Anmeldeversuch Funktion, welche überprüft, ob die Request valide ist.
     *
     * @param username Nutzername, welcher überprüft werden soll
     * @param password Passwort, welches überprüft werden soll
     * @return Status des Anmeldeversuchs (erfolgreich, Benutzername nicht gefunden, Passwort falsch)
     * @author Elias Glauert
     */
    public int attemptLogin(String username, String password) {
        System.out.println("Anmeldeversuch mit Daten:");
        System.out.println(" |  - username='" + username + "'");

        // Verwende die List-Schnittstelle, um flexibler zu sein
        List<String> fields = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        fields.add("username");
        contents.add(username);

        // Findet den Mitarbeiter basierend auf dem Benutzernamen
        // Weist die zurückgegebene List<Employee> der Variablen zu
        List<Employee> employees = employeeManager.findEmployees(fields, contents);

        // Geht zurück, wenn der Benutzername nicht vergeben ist
        if (employees.isEmpty()) {
            System.out.println(" | Benutzername nicht gefunden");
            return USERNAME_NOT_FOUND;
        }

        System.out.println(" |  - password='" + password + "'");

        Employee employee = employees.get(0);

        if (employee.getPassword().equals(password)) {
            System.out.println(" | Anmeldedaten sind valide");
            PersistentInformationReader.setLoggedInUserId(employee.getId());
            return LOGIN_SUCCESS;
        } else {
            System.out.println(" | Passwort ist falsch");
            return PASSWORD_INCORRECT;
        }
    }

    public void proceedToSoftware() {
        eventManager.callEvent("moveToHomeScreen", null);
    }

    /**
     * Loggt den Benutzer aus der Anwendung aus.
     * @author Elias Glauert
     */
    public void logout() {
        System.out.println("Logging the User out");
        PersistentInformationReader.clearLoggedInUser();
        eventManager.callEvent("changeView", new Object[]{new LoginView(eventManager, this)});
    }

    /**
     * Gibt den eingeloggten Mitarbeiter zurück.
     * @return
     * @author Elias Glauert
     */
    public Employee getLoggedInUser() {
        System.out.println(" ~ db ~ LoginManager.getLoggedInUser()");
        return employeeManager.getEmployeeById(PersistentInformationReader.getLoggedInUserId());
    }
}
