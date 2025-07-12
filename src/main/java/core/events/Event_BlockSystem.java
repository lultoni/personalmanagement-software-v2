package core.events;

import util.PersistentInformationReader;

/**
 * Event für Blockieren des Systems.
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-12
 */
public class Event_BlockSystem extends Event {

    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_BlockSystem(Object[] args) {
        super(args);

        // TODO do we want to change the view here already, because only the administrator will be able to call this event
        PersistentInformationReader.setSystemBlocked(true);
    }

}
