package gui.elements;

import core.EmployeeManager;
import core.EventManager;
import core.LoginManager;
import gui.views.*;
import util.PermissionChecker;
import util.PersistentInformationReader;
import model.db.Employee;
import db.dao.EmployeeDao;
import db.DatabaseManager;
import model.db.Employee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;


/**
 * Die FeatureBar beinhaltet alle Features, die dem User zur verfügung stehen.
 *
 * @author Elias Glauert
 * @version 1.3
 * @since 2025-07-07
 */
public class FeatureBar extends JPanel {

    private JButton logout_button;
    private JButton myProfile_button;
    private JPanel main_button_panel;
    private EventManager eventManager;
    private LoginManager loginManager;
    private final Dimension standardButtonSize = new Dimension(140, 40);

    /**
     * Konstruktor für die FeatureBar.
     * @author Elias Glauert, Joshua Sperber
     */
    public FeatureBar(LoginManager loginManager, EventManager eventManager) {

        this.eventManager = eventManager;
        this.loginManager = loginManager;

        setLayout(new BorderLayout());

        main_button_panel = getFeatureButtonPanel();

        // Logout Button und MyProfile Button (Immer sichtbar)
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        myProfile_button = createButton("MyProfile", standardButtonSize, _ -> {
            System.out.println("MyProfile Button Pressed");
            eventManager.callEvent("changeView", new Object[]{new EmployeeDataView(loginManager.getLoggedInUser(), loginManager.getLoggedInUser())});
        });

        logout_button = createButton("Logout", new Dimension(100, 30), _ -> {
            System.out.println("Logout Button Pressed");
            loginManager.logout();
        });

        footerPanel.add(myProfile_button);
        footerPanel.add(Box.createVerticalStrut(30));  // zusätzlicher Abstand
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
     * Gibt alle Feature Knöpfe in einem JPanel zurück.
     * @author Elias Glauert, Joshua Sperber
     */
    private JPanel getFeatureButtonPanel() {

        JPanel featureButtonPanel = new JPanel(new GridLayout(0, 1)); // TODO maybe passt ein box oder flow layout besser

        if (!PersistentInformationReader.isUserLoggedIn()) {
            // featureButtonPanel.add(new JLabel("Vor Login sind keine Funktionen auswählbar."));
            return featureButtonPanel;
        }
        JButton welcomeButton = new JButton("🏠 Startseite");
        welcomeButton.addActionListener(_ -> {
            eventManager.callEvent("moveToHomeScreen", null);
            myProfile_button.setMaximumSize(standardButtonSize);
            logout_button.setMaximumSize(standardButtonSize);

        });
        featureButtonPanel.add(welcomeButton);

        JButton searchFeatureButton = new JButton("🔎 Suche");
        searchFeatureButton.setPreferredSize(standardButtonSize);
        searchFeatureButton.addActionListener(_ -> {
            Employee currentUser = loginManager.getLoggedInUser();

            DatabaseManager dbManager = new DatabaseManager(false);
            EmployeeManager employeeManager = new EmployeeManager(dbManager);
            EmployeeDao employeeDao = new EmployeeDao(dbManager, employeeManager);
            employeeManager.setEmployeeDao(employeeDao);
            List<Employee> allEmployees = employeeManager.findAll();

            SearchView searchView = null;
            try {
                searchView = new SearchView(currentUser, allEmployees);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            eventManager.callEvent("changeView", new Object[]{searchView});

            myProfile_button.setMaximumSize(standardButtonSize);
            logout_button.setMaximumSize(standardButtonSize);
        });
        featureButtonPanel.add(searchFeatureButton);

        // if (PermissionChecker.hasPermission('B')) featureButtonPanel.add(searchFeatureButton); TODO dann nutzen wenn es die permission actually existiert
        featureButtonPanel.add(searchFeatureButton);
        // TODO nutze für die features PermissionChecker.hasPermission(char permission) ob es angezeigt werden soll
        //  also muss nicht ausgeblendet werden, aber es geht darum, dass die function genutzt werden soll


        JButton trainingButton = new JButton("📚 Schulungen");
        trainingButton.setPreferredSize(standardButtonSize);
        trainingButton.addActionListener(_ -> {
            eventManager.callEvent("changeView", new Object[]{new SchulungView()});
            myProfile_button.setMaximumSize(standardButtonSize);
            logout_button.setMaximumSize(standardButtonSize);

        });



        featureButtonPanel.add(trainingButton);

        JButton shutdownButton = new JButton("💣 Systemeinstellungen");
        shutdownButton.setPreferredSize(standardButtonSize);
        shutdownButton.addActionListener(_ -> {
            eventManager.callEvent("changeView", new Object[]{new gui.views.ShutdownView()});
            myProfile_button.setMaximumSize(standardButtonSize);
            logout_button.setMaximumSize(standardButtonSize);

        });
        if (PermissionChecker.hasPermission('S') && PermissionChecker.hasPermission('B') ) featureButtonPanel.add(shutdownButton);

        return featureButtonPanel;
    }


    /**
     * Aktualisiert, ob die Knöpfe en- oder disabled sind.
     * @author Elias Glauert
     */
    public void updateContent() {
        System.out.println(" ~ db ~ FeatureBar.updateContent()");
        boolean isUserLoggedIn = PersistentInformationReader.isUserLoggedIn();
        logout_button.setEnabled(isUserLoggedIn);
        myProfile_button.setEnabled(isUserLoggedIn);

        Component activeViewComponent = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (activeViewComponent != null) {
            remove(activeViewComponent);
        }

        main_button_panel = getFeatureButtonPanel();
        add(main_button_panel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

}