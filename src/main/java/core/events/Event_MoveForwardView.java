package core.events;

import gui.GuiManager;

import static core.Main.argsToString;

/**
 * Event für das Vorwärts des Views im GUI.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-29
 */
public class Event_MoveForwardView extends Event{

    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_MoveForwardView(Object[] args) {
        super(args);

        System.out.println(" ~ db ~ Event_MoveForwardView Konstruktor mit args: " + argsToString(args));

        GuiManager guiManager = (GuiManager) args[0];
        guiManager.goToNextView();
    }
}
