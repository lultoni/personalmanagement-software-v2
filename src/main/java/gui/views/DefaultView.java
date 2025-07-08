package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Standard-View für das GUI.
 * Wird auch genutzt, wenn ein Fehler auftritt beim Wechseln des Views.
 * Der Inhalt umfasst ein zentriertes JLabel mit dem Text "Default/Error View".
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-05
 */
public class DefaultView extends View {

    /**
     * Konstruktor für den DefaultView.
     *
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public DefaultView() {

        setView_id("view-default");
        setView_name("Default View");

        setLayout(new GridLayout());
        add(new JLabel("(Hier könnte ihre Werbung stehen)"));

    }

    @Override
    public String toString() {
        return "DefaultView('" + getView_id() + "', '" + getView_name() + "')";
    }

}
