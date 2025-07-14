package gui.views;

import model.Employee;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Ansicht für einen einzelnen Mitarbeiter.
 * Was angezeigt wird verändert sich dynamisch auf dem eingeloggten User.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-14
 */
public class EmployeeDataView extends View {

    /**
     * Der aktuell eingeloggte User.
     */
    private final Employee loggedInUser;

    /**
     * Mitarbeiter der Ansicht.
     */
    private final Employee employee;

    /**
     * Konstruktor für den EmployeeDataView.
     *
     * @param loggedInUser Der aktuell eingeloggte User
     * @param employee Der Mitarbeiter, der angezeigt wird
     */
    public EmployeeDataView(Employee loggedInUser, Employee employee) {
        this.loggedInUser = loggedInUser;
        this.employee = employee;

        setView_id("view-employee");
        setView_name("Mitarbeiter Ansicht von " + employee.getFirstName() + " " + employee.getLastName());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Mitarbeiter Ansicht: " + employee.getFirstName() + " " + employee.getLastName()));

        List<String> visibleFields = getVisibleFieldsForUser();

        // Hinzufügen der relevanten Felder als JLabel
        for (String field : visibleFields) {
            String labelText = getFieldDisplayText(field);
            JLabel label = new JLabel(labelText);
            add(label);
        }

        // Hinzufügen der Bearbeiten-Schaltfläche, wenn der User die Berechtigung hat
        if (canEditData()) {
            JButton editButton = new JButton("Bearbeiten");
            // füge ActionListener hinzu, falls notwendig
            add(editButton);
        }
    }

    /**
     * Bestimmt, welche Felder für den aktuellen eingeloggten User sichtbar sind.
     */
    private List<String> getVisibleFieldsForUser() {
        List<String> fields = new ArrayList<>();
        fields.add("firstName");
        fields.add("lastName");

        if (loggedInUser.equals(employee)) {
            fields.add("email");
            fields.add("phoneNumber");
            fields.add("dateOfBirth");
            fields.add("address");
            fields.add("gender");
        } else if (isUserInHierarchyBelow(loggedInUser, employee)) {
            fields.add("email");
            fields.add("departmentId");
            fields.add("roleId");
        }

        if (loggedInUser.isHR() || loggedInUser.isAdmin()) {
            fields.add("fullAccess");
        }

        return fields;
    }

    /**
     * Gibt den ausgegebenen Text für ein Sichtbarkeitsfeld zurück.
     */
    private String getFieldDisplayText(String fieldName) {
        switch (fieldName) {
            case "firstName":
                return "Vorname: " + employee.getFirstName();
            case "lastName":
                return "Nachname: " + employee.getLastName();
            case "email":
                return "Email: " + employee.getEmail();
            case "phoneNumber":
                return "Telefonnummer: " + employee.getPhoneNumber();
            case "dateOfBirth":
                return "Geburtsdatum: " + employee.getDateOfBirth();
            case "address":
                return "Adresse: " + employee.getAddress();
            case "gender":
                return "Geschlecht: " + employee.getGender();
            case "departmentId":
                return "Abteilungs-ID: " + employee.getDepartmentId();
            case "roleId":
                return "Rollen-ID: " + employee.getRoleId();
            case "fullAccess":
                return "Vollzugriff gewährt";
            default:
                return fieldName; // Fallback, falls kein Text definiert ist
        }
    }

    /**
     * Überprüft, ob der angeschaute Mitarbeiter unter dem eingeloggten User in der Hierarchie ist.
     */
    private boolean isUserInHierarchyBelow(Employee loggedInUser, Employee employee) {
        int currentManagerId = employee.getManagerId();
        while (currentManagerId != 0) {  // Assuming 0 is the CEO Id or top of the hierarchy
            if (currentManagerId == loggedInUser.getId()) {
                return true;
            }
            Employee manager = employee.getManager();
            currentManagerId = manager.getManagerId();
        }
        return false;
    }

    /**
     * Entscheidet, ob der eingeloggte User die Daten bearbeiten darf.
     */
    private boolean canEditData() {
        return loggedInUser.equals(employee) || loggedInUser.isHR() || loggedInUser.isAdmin();
    }
}