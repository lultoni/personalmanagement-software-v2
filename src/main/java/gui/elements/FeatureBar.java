package gui.elements;

import core.EmployeeManager;
import core.EventManager;
import core.LoginManager;
// import db.DatabaseManager; // Nicht verwendet, kann entfernt werden wenn unnötig
import gui.views.*; // Importiere alle Views
import model.db.Employee;
// import util.PermissionChecker; // Nicht direkt verwendet
import util.PersistentInformationReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;


/**
 * Die FeatureBar beinhaltet alle Features, die dem User zur verfuegung stehen.
 * Sie verwendet die zentralen Manager-Instanzen, die ihr uebergeben werden.
 *
 * @author Elias Glauert, Joshua Sperber
 * @version 1.7 (Hinzufuegen von 'Mitarbeiter bearbeiten'-Button, Entfernen von 'Hinzufuegen')
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
     * Konstruktor fuer die FeatureBar.
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

        // Das Panel wird einmal erstellt und danach nur der Inhalt geaendert
        main_button_panel = new JPanel(new GridLayout(0, 1));

        // Logout Button und MyProfile Button (Immer sichtbar)
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        myProfile_button = createButton("MyProfile", standardButtonSize, _ -> {
            System.out.println("MyProfile Button Pressed");
            Employee currentUser = loginManager.getLoggedInUser();
            if (currentUser != null) {
                eventManager.callEvent("changeView", new Object[]{
                        new EmployeeDataView(currentUser, currentUser, this.employeeManager, eventManager)
                });
            } else {
                JOptionPane.showMessageDialog(this, "Kein eingeloggter Benutzer gefunden.", "Fehler", JOptionPane.WARNING_MESSAGE);
            }
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
     * Helfer-Methode fuer das Erstellen von Buttons.
     * @param text Der Text des Buttons.
     * @param size Die bevorzugte Groesse des Buttons.
     * @param actionListener Der ActionListener fuer den Button.
     * @return Der erstellte JButton.
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
     * Erstellt und gibt alle Feature Knoepfe zurueck.
     * @author Elias Glauert, Joshua Sperber
     */
    private void createFeatureButtons() {
        // Zuerst den alten Inhalt loeschen
        main_button_panel.removeAll();

        // Nur Buttons hinzufuegen, wenn der Benutzer eingeloggt ist
        if (!PersistentInformationReader.isUserLoggedIn()) {
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
        searchFeatureButton.addActionListener(_ -> {
            Employee currentUser = loginManager.getLoggedInUser();
            try {
                // Die SearchView benoetigt jetzt den EmployeeManager
                SearchView searchView = new SearchView(currentUser, this.employeeManager, eventManager); // EventManager uebergeben
                eventManager.callEvent("changeView", new Object[]{searchView});
            } catch (IOException e) {
                System.err.println("Fehler beim Laden der Suchansicht: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Ein Fehler ist aufgetreten: " + e.getMessage() + "\nBitte ueberpruefen Sie die Konsolenausgabe fuer Details.",
                        "Fehler beim Laden der Suche",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage() + "\nBitte ueberpruefen Sie die Konsolenausgabe fuer Details.",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
        main_button_panel.add(searchFeatureButton);

        JButton trainingButton = new JButton("📚 Schulungen");
        trainingButton.setPreferredSize(standardButtonSize);
        trainingButton.addActionListener(_ -> {
            Employee currentUser = loginManager.getLoggedInUser();
            try {
                eventManager.callEvent("changeView", new Object[]{new SchulungView(currentUser, currentUser, this.employeeManager, eventManager)});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        main_button_panel.add(trainingButton);

        // ********************************************************************
        // NEU: Button zum Bearbeiten von Mitarbeitern (ersetzt 'Hinzufuegen')
        // Nur fuer HR, HR-Heads oder IT-Admins sichtbar
        // ********************************************************************
        Employee currentUser = loginManager.getLoggedInUser();
        if (currentUser != null && (currentUser.isHr() || currentUser.isItAdmin() || currentUser.isHrHead())) {
            JButton editEmployeeButton = new JButton("✏️ Mitarbeiter bearbeiten"); // Text geaendert
            editEmployeeButton.setPreferredSize(standardButtonSize);
            editEmployeeButton.addActionListener(_ -> {
                // Navigiert zur Suchansicht, wo Mitarbeiter zur Bearbeitung ausgewaehlt werden koennen
                try {
                    SearchView searchViewForEdit = new SearchView(currentUser, this.employeeManager, eventManager); // EventManager uebergeben
                    // Optional: Einen Modus an die SearchView uebergeben, um anzuzeigen, dass sie im Bearbeitungsmodus ist
                    // searchViewForEdit.setMode(SearchView.Mode.EDIT);
                    eventManager.callEvent("changeView", new Object[]{searchViewForEdit});
                } catch (IOException e) {
                    System.err.println("Fehler beim Laden der Suchansicht fuer Bearbeitung: " + e.getMessage());
                    JOptionPane.showMessageDialog(this,
                            "Ein Fehler ist aufgetreten: " + e.getMessage() + "\nBitte ueberpruefen Sie die Konsolenausgabe fuer Details.",
                            "Fehler beim Laden der Bearbeitungssuche",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
            main_button_panel.add(editEmployeeButton);
        }
        // ********************************************************************

        // Shutdown Button nur fuer IT-Admins anzeigen
        if (currentUser != null && currentUser.isItAdmin()) {
            JButton shutdownButton = new JButton("💣 Systemeinstellungen");
            shutdownButton.setPreferredSize(standardButtonSize);
            shutdownButton.addActionListener(_ -> {
                eventManager.callEvent("changeView", new Object[]{
                        new gui.views.ShutdownView(loginManager, eventManager, currentUser)
                });
            });
            main_button_panel.add(shutdownButton);
        }

        main_button_panel.revalidate();
        main_button_panel.repaint();
    }


    /**
     * Aktualisiert, ob die Knoepfe en- oder disabled sind und erneuert das Panel mit den Feature-Buttons.
     * @author Elias Glauert
     */
    public void updateContent() {
        System.out.println(" ~ db ~ FeatureBar.updateContent()");
        boolean isUserLoggedIn = PersistentInformationReader.isUserLoggedIn();
        System.out.println("Benutzer eingeloggt? " + isUserLoggedIn);
        logout_button.setEnabled(isUserLoggedIn);
        myProfile_button.setEnabled(isUserLoggedIn);

        // Die Methode zum Erstellen und Hinzufuegen der Buttons aufrufen
        System.out.println("Erstelle Feature Buttons jetzt...");
        createFeatureButtons();
    }

}
