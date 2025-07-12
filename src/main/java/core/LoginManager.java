package core;

import model.Employee;

/**
 * LoginManager der den Login-Prozess verwaltet.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-11
 */
public class LoginManager {

    EmployeeManager employeeManager;
    EventManager eventManager;

    /**
     * Konstruktor für den Login Manager
     * @author Elias Glauert
     */
    public LoginManager(EmployeeManager employeeManager, EventManager eventManager) {

        this.employeeManager = employeeManager;
        this.eventManager = eventManager;

    }

    /**
     * Anmeldeversuch Funktion, welche überprüft, ob die Request Valide ist.
     * @param username Nutzername, welcher überprüft werden soll
     * @param password Passwort, welches überprüft werden soll
     * @author Elias Glauert
     */
    public void attemptLogin(String username, String password) {
        System.out.println("Anmeldeversuch mit Daten:");
        System.out.println(" - username=" + username);
        System.out.println(" - password=" + password);
        Employee employee = employeeManager.findEmployee("username", username, "password", password);
        if (employee != null) {
            System.out.println("Anmeldedaten sind Valide");
            eventManager.callEvent("moveToHomeScreen", null);
        }
    }

}
