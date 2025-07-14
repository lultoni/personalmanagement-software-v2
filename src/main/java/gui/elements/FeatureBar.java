package gui.elements;

import core.EventManager;
import core.LoginManager;
import gui.views.EmployeeDataView;
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;


/**
 * Die FeatureBar beinhaltet alle Features, die dem User zur verfügung stehen.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-07
 */
public class FeatureBar extends JPanel {

    private JButton logout_button;
    private JButton myProfile_button;

    /**
     * Konstruktor für die FeatureBar.
     * @author Elias Glauert
     */
    public FeatureBar(LoginManager loginManager, EventManager eventManager) {

        setLayout(new BorderLayout());

        // Alle neuen Features kommen hier rein
        JPanel main_button_panel = new JPanel(new GridLayout());
        main_button_panel.add(new JLabel("Feature Bar WIP"));
        // TODO nutze für die features PermissionChecker.hasPermission(char permission) ob es angezeigt werden soll

        // Logout Button und MyProfile Button (Immer sichtbar)
        JPanel core_features_panel = new JPanel(new GridLayout(0, 1));
        logout_button = new JButton("Logout");
        logout_button.addActionListener(_ -> {
            System.out.println("Logout Button Pressed");
            loginManager.logout();
        });
        myProfile_button = new JButton("MyProfile");
        myProfile_button.addActionListener(_ -> {
            System.out.println("MyProfile Button Pressed");
            eventManager.callEvent("changeView", new Object[]{new EmployeeDataView(loginManager.getLoggedInUser(), loginManager.getLoggedInUser())});
        });
        core_features_panel.add(myProfile_button);
        core_features_panel.add(logout_button);

        add(main_button_panel, BorderLayout.CENTER);
        add(core_features_panel, BorderLayout.SOUTH);
    }

    /**
     * Aktualisiert, ob die Knöpfe en- oder disabled sind.
     * @author Elias Glauert
     */
    public void updateButtonEnabled() {
        System.out.println(" ~ db ~ FeatureBar.updateButtonEnabled()");
        boolean isUserLoggedIn = PersistentInformationReader.isUserLoggedIn();
        logout_button.setEnabled(isUserLoggedIn);
        myProfile_button.setEnabled(isUserLoggedIn);
    }

}
