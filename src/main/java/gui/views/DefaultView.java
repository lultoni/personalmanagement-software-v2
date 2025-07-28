package gui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Standard-View für das GUI.
 * Wird auch genutzt, wenn ein Fehler auftritt beim Wechseln des Views.
 * Der Inhalt umfasst ein zentriertes JLabel mit dem Text "Default/Error View".
 *
 * @author Elias Glauert
 * @version 1.3
 * @since 2025-07-05
 */
public class DefaultView extends View {

    /**
     * Konstruktor für den DefaultView.
     *
     * @author Elias Glauert
     */
    public DefaultView() {

        setView_id("view-default");
        setView_name("Default View");

        setLayout(new GridLayout());
        add(new JLabel("(Hier könnte ihre Werbung stehen)"));

    }

    /**
     * Gives back the View as a String.
     * @return All describing characteristics of the object with its hex code in the form of a string.
     * @author Elias Glauert
     */
    @Override
    public String toString() {
        String idHex = Integer.toHexString(System.identityHashCode(this));
        return "DefaultView@" + idHex + "('" + getView_id() + "', '" + getView_name() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (obj.getClass() != this.getClass()) return false;
        return true;
    }

}
