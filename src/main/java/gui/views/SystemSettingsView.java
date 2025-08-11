package gui.views;

import core.EmployeeManager;
import core.EventManager;
import model.db.Employee;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Die `SystemSettingsView` dient als Hauptmenü für HR-Verwaltungsaufgaben.
 * Sie enthält Buttons, um neue Mitarbeiter hinzuzufügen oder bestehende Mitarbeiter zu bearbeiten.
 * Die Navigation zu den entsprechenden Ansichten wird über den EventManager gesteuert.
 *
 * @author joshuasperber
 * @since 2025-07-27
 */
public class SystemSettingsView extends View {

    private final EventManager eventManager;
    private final EmployeeManager employeeManager;
    private final Employee currentUser;

    /**
     * Konstruktor für die SystemSettingsView.
     * Initialisiert die View und ihre Komponenten mit den notwendigen Managern.
     *
     * @param eventManager  Der EventManager zur Steuerung der Navigation.
     * @param employeeManager Der EmployeeManager zur Verwaltung der Mitarbeiterdaten.
     * @param currentUser   Der aktuell eingeloggte Benutzer.
     */
    public SystemSettingsView(EventManager eventManager, EmployeeManager employeeManager, Employee currentUser) {
        this.eventManager = eventManager;
        this.employeeManager = employeeManager;
        this.currentUser = currentUser;

        // Setze das Hauptlayout für diese Ansicht auf BorderLayout.
        setLayout(new BorderLayout());

        // Erstelle das Panel, das das Hintergrundbild enthält.
        JPanel backgroundPanel = createBackgroundPanel();

        // Erstelle das Panel, das die Buttons enthält.
        JPanel buttonPanel = createButtonPanel();

        // Verwende GridBagLayout im Hintergrund-Panel, um das Button-Panel zu zentrieren.
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(buttonPanel);

        // Füge das vorbereitete Hintergrund-Panel zur Hauptansicht hinzu.
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Erstellt ein JPanel mit einem transparenten Hintergrundbild.
     *
     * @return Ein JPanel mit einem gezeichneten Hintergrundbild.
     */
    private JPanel createBackgroundPanel() {
        BufferedImage backgroundImage;
        try {
            // Lade das Hintergrundbild aus dem Ressourcenordner.
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            // Wirft eine RuntimeException, wenn das Bild nicht gefunden oder geladen werden kann.
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Erstelle ein anonymes JPanel, das die paintComponent-Methode überschreibt.
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Setze die Transparenz (Alpha) für das Bild auf 20%.
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                // Zeichne das Bild, skaliert auf die Größe des Panels.
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        panel.setOpaque(false); // Mache den Panel-Hintergrund transparent, um das Bild zu zeigen.
        return panel;
    }

    /**
     * Erstellt ein JPanel, das die Navigations-Buttons enthält.
     *
     * @return Ein JPanel mit den Buttons "Mitarbeiter hinzufügen" und "Mitarbeiter bearbeiten".
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        // Verwende ein GridLayout mit zwei Zeilen, einer Spalte und einem vertikalen Abstand von 20 Pixeln.
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 20));
        buttonPanel.setOpaque(false); // Mache den Panel-Hintergrund transparent.

        // Button für die Ansicht zum Hinzufügen von Mitarbeitern.
        JButton addEmployeeButton = new JButton("Mitarbeiter hinzufügen");
        addEmployeeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        addEmployeeButton.setPreferredSize(new Dimension(300, 60));
        // Füge einen ActionListener hinzu, der die entsprechende Methode aufruft.
        addEmployeeButton.addActionListener(this::onAddEmployeeButtonClicked);

        // Button für die Ansicht zum Bearbeiten von Mitarbeitern.
        JButton editEmployeeButton = new JButton("Mitarbeiter bearbeiten");
        editEmployeeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        editEmployeeButton.setPreferredSize(new Dimension(300, 60));
        editEmployeeButton.addActionListener(this::onEditEmployeeButtonClicked);

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(editEmployeeButton);

        return buttonPanel;
    }

    /**
     * Wird aufgerufen, wenn der "Mitarbeiter hinzufügen"-Button geklickt wird.
     * Navigiert zur `AddEmployeeView`.
     *
     * @param e Das ActionEvent-Objekt.
     */
    private void onAddEmployeeButtonClicked(ActionEvent e) {
        System.out.println("Button 'Mitarbeiter hinzufügen' geklickt.");
        // Rufe das Event "changeView" auf, um zur AddEmployeeView zu wechseln.
        eventManager.callEvent("changeView", new Object[]{new AddEmployeeView(this.employeeManager, this.eventManager)});
    }

    /**
     * Wird aufgerufen, wenn der "Mitarbeiter bearbeiten"-Button geklickt wird.
     * Navigiert zur `SearchView` im Bearbeitungsmodus.
     *
     * @param e Das ActionEvent-Objekt.
     */
    private void onEditEmployeeButtonClicked(ActionEvent e) {
        System.out.println("Button 'Mitarbeiter bearbeiten' geklickt.");
        try {
            // Rufe das Event "changeView" auf, um zur SearchView im "edit_mode" zu wechseln.
            eventManager.callEvent("changeView", new Object[]{
                    new SearchView(currentUser, employeeManager, eventManager, "edit_mode")
            });
        } catch (IOException ex) {
            // Gib den Stacktrace bei einem Fehler aus und zeige eine Fehlermeldung an.
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Suchansicht für die Bearbeitung.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}