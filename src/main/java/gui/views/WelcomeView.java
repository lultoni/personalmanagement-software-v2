package gui.views;

import core.EmployeeManager;
import db.dao.EmployeeDao; // Nicht direkt verwendet, kann entfernt werden wenn unnötig
import model.db.Employee;
import util.PersistentInformationReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Die `WelcomeView` ist die Startseite oder Willkommensansicht der Anwendung.
 * Sie begrüßt den eingeloggten Mitarbeiter persönlich und zeigt grundlegende
 * Informationen wie seinen Namen und seine Rolle in der Firma an.
 *
 * @author joshuasperber
 * @version 1.1
 * @since 2025-07-27
 */
public class WelcomeView extends View {

    /**
     * Konstruktor für die WelcomeView.
     * Erstellt die grafische Benutzeroberfläche der Willkommensansicht.
     *
     * @param employeeManager Der EmployeeManager, der für den Zugriff auf Mitarbeiterdaten benötigt wird.
     */
    public WelcomeView(EmployeeManager employeeManager) {
        // Setze die ID und den Namen der View.
        setView_id("view-welcome");
        setView_name("Willkommensansicht");

        BufferedImage backgroundImage;
        try {
            // Lade das Hintergrundbild aus den Ressourcen.
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            // Behandle den Fehler, falls das Hintergrundbild nicht geladen werden kann.
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        // Erstelle ein JPanel, das das Hintergrundbild zeichnet und Transparenz hinzufügt.
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Setze die globale Alpha-Komponente für das Zeichnen (0.2f für 20% Deckkraft).
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                // Zeichne das Hintergrundbild, skaliert auf die Größe des Panels.
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout()); // Setze das Layout für das Hintergrundpanel.
        backgroundPanel.setOpaque(false); // Mache den Hintergrund des Panels transparent.

        // Hole den aktuell eingeloggten Mitarbeiter über den EmployeeManager und die gespeicherte ID.
        Employee loggedInEmployee = employeeManager.getEmployeeById(PersistentInformationReader.getLoggedInUserId());
        String userName = "";
        if (loggedInEmployee != null) {
            // Wenn ein Mitarbeiter gefunden wurde, erstelle den vollständigen Namen.
            userName = loggedInEmployee.getFirstName() + " " + loggedInEmployee.getLastName();
        }

        // Erstelle das Titel-Label zur Begrüßung des Benutzers.
        JLabel titleLabel = new JLabel("Willkommen, " + userName + "!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK); // Setze die Textfarbe auf Schwarz.

        // Erstelle das Untertitel-Label, das die Rolle des Mitarbeiters anzeigt.
        JLabel subtitleLabel = new JLabel("BOB the Builder Company – " + getRoleString(employeeManager), SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.DARK_GRAY); // Setze die Textfarbe auf Dunkelgrau.

        // Erstelle ein Haupt-Content-Panel, das die Begrüßungs- und Untertitel-Labels enthält.
        JPanel mainContentPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // Layout mit vertikalem Abstand.
        mainContentPanel.setOpaque(false); // Mache dieses Panel ebenfalls transparent, um das Hintergrundbild sichtbar zu lassen.
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20)); // Füge optional einen Rahmen hinzu.

        // Füge die Labels dem Haupt-Content-Panel hinzu.
        mainContentPanel.add(titleLabel);
        mainContentPanel.add(subtitleLabel);

        // Füge das Haupt-Content-Panel zum Hintergrund-Panel hinzu, um es zu zentrieren.
        backgroundPanel.add(mainContentPanel, BorderLayout.CENTER);

        // Setze das Layout der Haupt-View und füge das gesamte Hintergrundpanel hinzu.
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Gibt einen String zurück, der die spezifische Rolle des eingeloggten Mitarbeiters darstellt.
     * Prüft auf IT-Admin, HR-Head, HR-Mitarbeiter und Manager.
     *
     * @param employeeManager Der EmployeeManager, um auf die Mitarbeiterdetails zugreifen zu können.
     * @return Ein String, der die Rolle des Mitarbeiters beschreibt. Standard ist "Mitarbeiter".
     */
    private String getRoleString(EmployeeManager employeeManager) {
        String roleString = "Mitarbeiter";
        // Hole den eingeloggten Mitarbeiter erneut, um die aktuellsten Rollen zu prüfen.
        Employee loggedInEmployee = employeeManager.getEmployeeById(PersistentInformationReader.getLoggedInUserId());
        // Sicherstellen, dass loggedInEmployee nicht null ist, bevor Rollen geprüft werden.
        if (loggedInEmployee != null) {
            if (loggedInEmployee.isItAdmin()) return "IT-Admin";
            if (loggedInEmployee.isHrHead()) return "HR-Head";
            if (loggedInEmployee.isHr()) return "HR-Mitarbeiter";
            if (loggedInEmployee.isManager()) return "Manager";
        }
        return roleString;
    }

    /**
     * Gibt eine String-Repräsentation des Objekts zurück.
     * @return Die String-Repräsentation der View.
     */
    @Override
    public String toString() {
        // Ruft die toString-Methode der Superklasse auf, um die View-ID und den Namen zu erhalten.
        return super.toString();
    }

    /**
     * Vergleicht dieses Objekt mit einem anderen auf Gleichheit.
     * @param obj Das zu vergleichende Objekt.
     * @return True, wenn die Objekte gleich sind, ansonsten False.
     */
    @Override
    public boolean equals(Object obj) {
        // Ruft die equals-Methode der Superklasse auf.
        return super.equals(obj);
    }
}