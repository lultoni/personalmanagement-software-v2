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
 * Die Startseite / Willkommensansicht für das IT-Admin-System.
 * @author Joshua Sperber
 * @version 1.1 (Personalisierte Begrüßung hinzugefügt)
 */
public class WelcomeView extends View {

    public WelcomeView(EmployeeManager employeeManager) {
        setView_id("view-welcome");
        setView_name("Willkommensansicht");

        // ********************************************************************
        // HINTERGRUNDDESIGN-INTEGRATION START (wie in anderen Views)
        // ********************************************************************
        BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("icons/Hintergrundbild.png"));
        } catch (IOException e) {
            throw new RuntimeException("Hintergrundbild konnte nicht geladen werden.", e);
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(false);
        //

        Employee loggedInEmployee = employeeManager.getEmployeeById(PersistentInformationReader.getLoggedInUserId());
        String userName = "";
        if (loggedInEmployee != null) {
            userName = loggedInEmployee.getFirstName() + " " + loggedInEmployee.getLastName();
        }

        JLabel titleLabel = new JLabel("Willkommen, " + userName + "!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK); // Textfarbe für bessere Lesbarkeit auf transparentem Hintergrund

        JLabel subtitleLabel = new JLabel("BOB the Builder Company – " + getRoleString(employeeManager), SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.DARK_GRAY); // Textfarbe für bessere Lesbarkeit

        // Haupt-Content-Panel, das alle spezifischen UI-Elemente enthält
        JPanel mainContentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        mainContentPanel.setOpaque(false); // Wichtig: Macht das Panel transparent
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20)); // Optional: Abstand zum Rand

        mainContentPanel.add(titleLabel);
        mainContentPanel.add(subtitleLabel);

        // Füge das mainContentPanel zum backgroundPanel hinzu
        backgroundPanel.add(mainContentPanel, BorderLayout.CENTER);

        // Setze das Layout der Haupt-View und füge das Hintergrundpanel hinzu
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private String getRoleString(EmployeeManager employeeManager) {
        String roleString = "Mitarbeiter";
        Employee loggedInEmployee = employeeManager.getEmployeeById(PersistentInformationReader.getLoggedInUserId());
        // Sicherstellen, dass loggedInEmployee nicht null ist
        if (loggedInEmployee != null) {
            if (loggedInEmployee.isItAdmin()) return "IT-Admin";
            if (loggedInEmployee.isHrHead()) return "HR-Head";
            if (loggedInEmployee.isHr()) return "HR-Mitarbeiter";
            if (loggedInEmployee.isManager()) return "Manager";
        }
        return roleString;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
