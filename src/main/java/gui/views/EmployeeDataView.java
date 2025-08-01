package gui.views;

import model.db.Employee;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Ansicht für einen einzelnen Mitarbeiter.
 * Was angezeigt wird verändert sich dynamisch auf dem eingeloggten User.
 *
 * @author Elias Glauert
 * @version 1.2
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
     * @param loggedInUser Der aktuell eingeloggte User.
     * @param employee Der Mitarbeiter, der angezeigt werden soll.
     * @author Elias Glauert
     */
    public EmployeeDataView(Employee loggedInUser, Employee employee) {
        this.loggedInUser = loggedInUser;
        this.employee = employee;

        setView_id("view-employee");
        setView_name("Mitarbeiter Ansicht von " + employee.getFirstName() + " " + employee.getLastName());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Mitarbeiter Ansicht: " + employee.getFirstName() + " " + employee.getLastName()));

        ArrayList<String> visibleFields = getVisibleFieldsForUser();

        // TODO hier ist ein placeholder way wie alle felder angezeigt werden
        for (String field : visibleFields) {
            String labelText = getFieldDisplayText(field);
            JLabel label = new JLabel(labelText);
            add(label);
        }

        // TODO hier auch, wie/wo soll der Button angezeigt werden?
        if (canEditData()) {
            JButton editButton = new JButton("Bearbeiten");
            editButton.addActionListener(_ -> {
                // TODO füge richtigen ActionListener-Code hinzu
                System.out.println(" ~ db ~ EDIT BUTTON PRESSED - no action yet defined");
            });
            add(editButton);
        }
    }

    /**
     * Bestimmt, welche Felder für den aktuellen eingeloggten User sichtbar sind.
     * @author Elias Glauert
     */
    private ArrayList<String> getVisibleFieldsForUser() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("firstName");
        fields.add("lastName");
        fields.add("username");

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

        if (loggedInUser.isHr() || loggedInUser.isItAdmin()) {
            fields.add("fullAccess");
            // TODO das field gibt nur "Vollzugriff gewährt" zurück
        }

        /*
        Alle fields die noch möglicherweise hinzugefügt werden können
        (oder mit denen was anderes gemacht werden kann noch):
        fields.add("hireDate");
        fields.add("employmentStatus"); // actually keinen plan was hier überhaupt genau drin steht :D
        fields.add("teamId");
        fields.add("qualifications");
        fields.add("completedTrainings");
        fields.add("managerId");
         */

        return fields;
    }

    /**
     * Gibt den ausgegebenen Text für ein Sichtbarkeitsfeld zurück.
     * Umfasst alle Felder, die ein Mitarbeiter hat.
     * @author Elias Glauert
     */
    // TODO könnte noch erweitert werden mit mehr sachen
    private String getFieldDisplayText(String fieldName) {
        return switch (fieldName) {
            case "firstName" -> "Vorname: " + employee.getFirstName();
            case "lastName" -> "Nachname: " + employee.getLastName();
            case "email" -> "Email: " + employee.getEmail();
            case "phoneNumber" -> "Telefonnummer: " + employee.getPhoneNumber();
            case "dateOfBirth" -> "Geburtsdatum: " + employee.getDateOfBirth();
            case "address" -> "Adresse: " + employee.getAddress();
            case "gender" -> "Geschlecht: " + employee.getGender();
            case "departmentId" -> "Abteilungs-ID: " + employee.getDepartmentId();
            case "roleId" -> "Rollen-ID: " + employee.getRoleId();
            case "fullAccess" -> "Vollzugriff gewährt";
            case "username" -> "asdf: " + employee.getUsername();
            case "hireDate" -> "asdf: " + employee.getHireDate();
            case "employmentStatus" -> "asdf: " + employee.getEmploymentStatus();
            case "teamId" -> "asdf: " + employee.getTeamId();
            case "qualifications" -> "asdf: " + employee.getQualifications();
            case "completedTrainings" -> "asdf: " + employee.getCompletedTrainings();
            case "managerId" -> "asdf: " + employee.getManagerId();
            default -> fieldName; // Fallback, falls kein Text definiert ist
        };
    }

    /**
     * Überprüft, ob der angeschaute Mitarbeiter unter dem eingeloggten User in der Hierarchie ist.
     * @author Elias Glauert
     */
    private boolean isUserInHierarchyBelow(Employee potentialHigherUpEmployee, Employee potentialBelowEmployee) {
        int currentManagerId = potentialBelowEmployee.getManagerId();
        while (currentManagerId != 0) { // Assuming 0 is the CEO ID or top of the hierarchy
            // TODO change this to correct id after employee generation has been done
            if (currentManagerId == potentialHigherUpEmployee.getId()) {
                return true;
            }
            currentManagerId = potentialBelowEmployee.getManagerId();
        }
        return false;
    }

    /**
     * Entscheidet, ob der eingeloggte User die Daten bearbeiten darf.
     * @author Elias Glauert
     */
    private boolean canEditData() {
        return loggedInUser.equals(employee) || loggedInUser.isHr() || loggedInUser.isItAdmin();
    }

    @Override
    public String toString() {
        String idHex = Integer.toHexString(System.identityHashCode(this));
        return "EmployeeDataView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "', loggedInUser:" + loggedInUser.toString() + ", employee:" + employee.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        EmployeeDataView view = (EmployeeDataView) obj;
        if (!view.getView_id().equals(this.getView_id())) return false;
        if (!view.getView_name().equals(this.getView_name())) return false;
        if (!view.employee.equals(this.employee)) return false;
        if (!view.loggedInUser.equals(this.loggedInUser)) return false;
        return true;
    }
}