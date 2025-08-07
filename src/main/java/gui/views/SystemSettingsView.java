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
 * Die SystemSettingsView dient als Hauptmenü für HR-Verwaltungsaufgaben.
 * Sie enthält Buttons, um neue Mitarbeiter hinzuzufügen oder bestehende Mitarbeiter zu bearbeiten.
 * Die Navigation zu den entsprechenden Ansichten wird über den EventManager gesteuert.
 */
public class SystemSettingsView extends View {

    private final EventManager eventManager;
    private final EmployeeManager employeeManager;
    private final Employee currentUser;

    public SystemSettingsView(EventManager eventManager, EmployeeManager employeeManager, Employee currentUser) {
        this.eventManager = eventManager;
        this.employeeManager = employeeManager;
        this.currentUser = currentUser;

        // Setze das Layout für diese Ansicht
        setLayout(new BorderLayout());

        // Hintergrundbild-Panel erstellen
        JPanel backgroundPanel = createBackgroundPanel();

        // Button-Panel erstellen und zentrieren
        JPanel buttonPanel = createButtonPanel();

        // Füge das Button-Panel in die Mitte des Hintergrund-Panels
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(buttonPanel);

        // Füge das Hintergrund-Panel zur Hauptansicht hinzu
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JPanel createBackgroundPanel() {
        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 20));
        buttonPanel.setOpaque(false); // Macht den Panel-Hintergrund transparent

        // Button für das Hinzufügen von Mitarbeitern
        JButton addEmployeeButton = new JButton("Mitarbeiter hinzufügen");
        addEmployeeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        addEmployeeButton.setPreferredSize(new Dimension(300, 60));
        addEmployeeButton.addActionListener(this::onAddEmployeeButtonClicked);

        // Button für das Bearbeiten von Mitarbeitern
        JButton editEmployeeButton = new JButton("Mitarbeiter bearbeiten");
        editEmployeeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        editEmployeeButton.setPreferredSize(new Dimension(300, 60));
        editEmployeeButton.addActionListener(this::onEditEmployeeButtonClicked);

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(editEmployeeButton);

        return buttonPanel;
    }


    private void onAddEmployeeButtonClicked(ActionEvent e) {
        System.out.println("Button 'Mitarbeiter hinzufügen' geklickt.");
        eventManager.callEvent("changeView", new Object[]{new AddEmployeeView(this.employeeManager, this.eventManager)});
    }

    private void onEditEmployeeButtonClicked(ActionEvent e) {
        System.out.println("Button 'Mitarbeiter bearbeiten' geklickt.");
        try {
            // Navigiere zur SearchView im Bearbeitungsmodus
            eventManager.callEvent("changeView", new Object[]{
                    new SearchView(currentUser, employeeManager, eventManager, "edit_mode")
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Suchansicht für die Bearbeitung.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}