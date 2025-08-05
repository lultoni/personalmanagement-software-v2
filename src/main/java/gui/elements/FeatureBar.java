package gui.elements;

import core.EmployeeManager;
import core.EventManager;
import core.LoginManager;
import db.DatabaseManager;
import gui.views.*;
import model.db.Employee;
import util.PermissionChecker;
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;


/**
 * Die FeatureBar beinhaltet alle Features, die dem User zur verfügung stehen.
 * Sie verwendet die zentralen Manager-Instanzen, die ihr übergeben werden.
 *
 * @author Elias Glauert, Joshua Sperber
 * @version 1.5 (Problem mit Buttons behoben)
 * @since 2025-07-07
 */
public class FeatureBar extends JPanel {

    private JButton logout_button;
    private JButton myProfile_button;
    private JPanel main_button_panel; // Dieses Panel wird jetzt wiederverwendet
    private EventManager eventManager;
    private LoginManager loginManager;
    private EmployeeManager employeeManager;
    private final Dimension standardButtonSize = new Dimension(140, 40);

    /**
     * Konstruktor für die FeatureBar.
     * Erwartet alle notwendigen Manager als Parameter.
     * @param loginManager Die Instanz des LoginManager.
     * @param eventManager Die Instanz des EventManager.
     * @param employeeManager Die Instanz des EmployeeManager.
     * @author Elias Glauert, Joshua Sperber
     */
    public FeatureBar(LoginManager loginManager, EventManager eventManager, EmployeeManager employeeManager) {
        this.eventManager = eventManager;
        this.loginManager = loginManager;
        this.employeeManager = employeeManager;

        setLayout(new BorderLayout());

        // Das Panel wird einmal erstellt und danach nur der Inhalt geändert
        main_button_panel = new JPanel(new GridLayout(0, 1));

        // Logout Button und MyProfile Button (Immer sichtbar)
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        myProfile_button = createButton("MyProfile", standardButtonSize, _ -> {
            System.out.println("MyProfile Button Pressed");
            Employee currentUser = loginManager.getLoggedInUser();
            eventManager.callEvent("changeView", new Object[]{
                    new EmployeeDataView(currentUser, currentUser, this.employeeManager, eventManager)
            });
        });

        logout_button = createButton("Logout", new Dimension(100, 30), _ -> {
            System.out.println("Logout Button Pressed");
            loginManager.logout();
        });

        footerPanel.add(myProfile_button);
        footerPanel.add(Box.createVerticalStrut(30));
        footerPanel.add(logout_button);

        add(main_button_panel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Helfer-Methode für das Erstellen von Buttons.
     * @param text
     * @param size
     * @param actionListener
     * @return
     * @author Joshua Sperber
     */
    private JButton createButton(String text, Dimension size, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * Erstellt und gibt alle Feature Knöpfe zurück.
     * @return Eine Liste von Buttons, die auf dem Feature-Panel angezeigt werden sollen.
     * @author Elias Glauert, Joshua Sperber
     */
    private void createFeatureButtons() {
        // Zuerst den alten Inhalt löschen
        main_button_panel.removeAll();

        // Nur Buttons hinzufügen, wenn der Benutzer eingeloggt ist
        if (!PersistentInformationReader.isUserLoggedIn()) {
            // Optional: Hier eine Nachricht oder ein leeres Panel anzeigen
            main_button_panel.revalidate();
            main_button_panel.repaint();
            return;
        }

        JButton welcomeButton = new JButton("🏠 Startseite");
        welcomeButton.addActionListener(_ -> {
            eventManager.callEvent("moveToHomeScreen", null);
        });
        main_button_panel.add(welcomeButton);

        JButton searchFeatureButton = new JButton("🔎 Suche");
        searchFeatureButton.setPreferredSize(standardButtonSize);
        searchFeatureButton.addActionListener(_ -> {
            Employee currentUser = loginManager.getLoggedInUser();
            List<Employee> allEmployees = this.employeeManager.findAll();

            SearchView searchView = null;
            try {
                searchView = new SearchView(currentUser, allEmployees);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            eventManager.callEvent("changeView", new Object[]{searchView});
        });
        main_button_panel.add(searchFeatureButton);

        // TODO nutze für die features PermissionChecker.hasPermission(char permission) ob es angezeigt werden soll
        //  also muss nicht ausgeblendet werden, aber es geht darum, dass die function genutzt werden soll
        // Beispiel: if (PermissionChecker.hasPermission('B')) featureButtonPanel.add(searchFeatureButton);

        JButton trainingButton = new JButton("📚 Schulungen");
        trainingButton.setPreferredSize(standardButtonSize);
        trainingButton.addActionListener(_ -> {
            eventManager.callEvent("changeView", new Object[]{new SchulungView()});
        });
        main_button_panel.add(trainingButton);

        JButton shutdownButton = new JButton("💣 Systemeinstellungen");
        shutdownButton.setPreferredSize(standardButtonSize);
        shutdownButton.addActionListener(_ -> {
            eventManager.callEvent("changeView", new Object[]{new gui.views.ShutdownView()});
        });
        main_button_panel.add(shutdownButton);

        main_button_panel.revalidate();
        main_button_panel.repaint();
    }


    /**
     * Aktualisiert, ob die Knöpfe en- oder disabled sind und erneuert das Panel mit den Feature-Buttons.
     * @author Elias Glauert
     */
    public void updateContent() {
        System.out.println(" ~ db ~ FeatureBar.updateContent()");
        boolean isUserLoggedIn = PersistentInformationReader.isUserLoggedIn();
        logout_button.setEnabled(isUserLoggedIn);
        myProfile_button.setEnabled(isUserLoggedIn);

        // Die Methode zum Erstellen und Hinzufügen der Buttons aufrufen
        createFeatureButtons();
    }

}