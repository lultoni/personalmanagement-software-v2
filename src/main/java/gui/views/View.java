package gui.views;

import javax.swing.*;

/**
 * Vorlage für Vererbung für wie Views aussehen.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-07
 */
public class View extends JPanel {

    /**
     * Interne ID für den View. Sollte kurz gehalten werden.
     */
    private String view_id;

    /**
     * Der Gui-Name für den View, welcher angezeigt wird, wenn der View offen ist.
     */
    private String view_name;

    /**
     * Konstruktor für den View. Initialisiert die view_id und den view_name.
     *
     * @author Elias Glauert
     */
    public View() {

        this.view_id = "view_id";
        this.view_name = "view_name";

    }

    @Override
    public String toString() {
        return "View('" + getView_id() + "', '" + getView_name() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        View view = (View) obj;
        if (!view.getView_id().equals(this.getView_id())) return false;
        if (!view.getView_name().equals(this.getView_name())) return false;
        return true;
    }

    public String getView_id() {
        return view_id;
    }

    public String getView_name() {
        return view_name;
    }

    public void setView_id(String view_id) {
        this.view_id = view_id;
    }

    public void setView_name(String view_name) {
        this.view_name = view_name;
    }

}
