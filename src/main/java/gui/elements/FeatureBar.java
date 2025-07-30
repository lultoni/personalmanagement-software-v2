package gui.elements;

import core.EventManager;
import core.LoginManager;
import gui.views.EmployeeDataView;
import gui.views.SearchView;
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
    private JPanel main_button_panel;
    private EventManager eventManager;

    /**
     * Konstruktor für die FeatureBar.
     * @author Elias Glauert
     */
    public FeatureBar(LoginManager loginManager, EventManager eventManager) {

        this.eventManager = eventManager;

        setLayout(new BorderLayout());

        main_button_panel = getFeatureButtonPanel();

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

    private JPanel getFeatureButtonPanel() {

        JPanel featureButtonPanel = new JPanel(new GridLayout(0, 1)); // TODO maybe passt ein box oder flow layout besser

        if (!PersistentInformationReader.isUserLoggedIn()) {
            featureButtonPanel.add(new JLabel("Vor Login sind keine Funktionen auswählbar."));
            return featureButtonPanel;
        }

        JButton searchFeatureButton = new JButton("Suche");
        searchFeatureButton.addActionListener(_ -> {
            eventManager.callEvent("changeView", new Object[]{new SearchView()});
        });
        // if (PermissionChecker.hasPermission('B')) featureButtonPanel.add(searchFeatureButton); TODO dann nutzen wenn es die permission actually existiert
        featureButtonPanel.add(searchFeatureButton);
        // TODO nutze für die features PermissionChecker.hasPermission(char permission) ob es angezeigt werden soll
        //  also muss nicht ausgeblendet werden, aber es geht darum, dass die function genutzt werden soll
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
