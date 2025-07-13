package core.events;

import gui.GuiManager;
import gui.views.View;

import static core.Main.argsToString;

/**
 * Event für das Ändern des Views im GUI.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-07
 */
public class Event_ChangeView extends Event {
    /**
     * Konstruktor für Event_ChangeView.
     * Beinhaltet nicht selber, aber erbende Events beinhalten hier alle Methoden die aufgerufen werden sollen.
     *
     * @param args Erwartet ein Array mit der Größe 2, wo das erste Objekt ein GuiManager ist und das zweite ein View.
     * @author Elias Glauert
     */
    public Event_ChangeView(Object[] args) {
        super(args);

        System.out.println(" ~ db ~ Event_ChangeView Konstruktor mit args: " + argsToString(args));

        GuiManager guiManager = (GuiManager) args[0];
        guiManager.changeView((View) args[1]);
    }
}
