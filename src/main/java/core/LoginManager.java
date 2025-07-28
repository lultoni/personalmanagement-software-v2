package core;

import gui.views.LoginView;
import model.Employee;
import util.PersistentInformationReader;

import java.util.ArrayList;

/**
 * LoginManager der den Login-Prozess verwaltet.
 *
 * @author Elias Glauert
 * @version 1.3
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
     *
     * @param username Nutzername, welcher überprüft werden soll
     * @param password Passwort, welches überprüft werden soll
     * @return Ob der Anmeldeversuch akzeptiert wurde oder nicht
     * @author Elias Glauert
     */
    public boolean attemptLogin(String username, String password) {
        System.out.println("Anmeldeversuch mit Daten:");
        System.out.println(" - username=" + username);
        System.out.println(" - password=" + password);

        ArrayList<String> fields = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        fields.add("username");
        contents.add(username);
        fields.add("password");
        contents.add(password);

        Employee employee = employeeManager.findEmployees(fields, contents).getFirst();
        if (employee != null) {
            System.out.println("Anmeldedaten sind Valide");
            PersistentInformationReader.setUserLoggedIn(true);
            PersistentInformationReader.setLoggedInUserId(employee.getId());
            return true;
        }
        return false;
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
        PersistentInformationReader.setUserLoggedIn(false);
        eventManager.callEvent("changeView", new Object[]{new LoginView(this)});
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
