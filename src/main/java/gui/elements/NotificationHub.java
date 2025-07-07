package gui.elements;

import javax.swing.*;
import java.awt.*;

/**
 * Beinhaltet die Benachrichtigungen für den User.
 * Öffnet ein Pop-Up-Menü wenn der Knopf gedrückt wird.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-07
 */
public class NotificationHub extends JPanel {

    public NotificationHub() {
        setLayout(new GridLayout());
        add(new JLabel("Notification Hub"));
        // TODO Button daraus mache
        // TODO dieser button hat ein sich anpassendes icon, welches basierend auf der anzahl der aktiven benachrichtigungen auch diese zahl in weiß in einem roten kreis dort anzeigt
        // TODO pop-up menu mit all den Benachrichtigungen
        // TODO klasse für die einzelnen ben. im gui erstellen
        // TODO diese beinhaltet den titel, die desc., den knopf zum view und den knopf zum löschen
    }

}
