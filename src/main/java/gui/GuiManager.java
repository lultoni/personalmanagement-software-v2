package gui;


import core.EventManager;
import gui.views.View;

import java.util.ArrayList;

/**
 * Diese Klasse verwaltet das GUI.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-05
 */
public class GuiManager {

    /**
     * Enthält den View-Stack der Session. Wird für den Zurück-Knopf genutzt.
     * @author Elias Glauert
     */
    private ArrayList<View> view_history;

    /**
     * Enthält den View-args-Stack der Session. Wird für den Zurück-Knopf genutzt.
     * @author Elias Glauert
     */
    private ArrayList<Object> view_history_args;
    // TODO maybe kann man das in eine eigene Klasse machen, mit view + args als variablen?

    /**
     * Das Haupt JFrame, in welches alle GUI Elemente angezeigt werden.
     * @author Elias Glauert
     */
    private MainFrame mainFrame;

    /**
     * EventManager Verbindung des GuiManagers.
     * @author Elias Glauert
     */
    private EventManager eventManager;

    /**
     * Konstruktor für den GuiManager.
     *
     * @param -
     * @author Elias Glauert
     */
    // TODO überarbeite diese beschreibung
    public GuiManager(EventManager eventManager) {

        this.eventManager = eventManager;

        view_history = new ArrayList<>();
        view_history_args = new ArrayList<>();

        mainFrame = new MainFrame();

    }

    /*
    public ArrayList<View> getView_history() {
        return view_history;
    }

    public ArrayList<Object> getView_history_args() {
        return view_history_args;
    }

    public void setView_history(ArrayList<View> view_history) {
        this.view_history = view_history;
    }

    public void setView_history_args(ArrayList<Object> view_history_args) {
        this.view_history_args = view_history_args;
    }
    */

}
