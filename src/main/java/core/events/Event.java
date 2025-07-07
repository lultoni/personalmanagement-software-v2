package core.events;

/**
 * Fachklasse für Events.
 * Wird als Blaupause für alle anderen Custom Events genutzt.
 *
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-07
 */
public class Event {

    /**
     * Konstruktor für Event.
     * Beinhaltet nichts selber, aber erbende Events beinhalten hier alle Methoden die aufgerufen werden sollen.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event(Object[] args) {

    }

}
