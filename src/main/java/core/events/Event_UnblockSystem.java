package core.events;

import util.PersistentInformationReader;

/**
 * Event für Blockieren des Systems.
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-12
 */
public class Event_UnblockSystem extends Event {

    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_UnblockSystem(Object[] args) {
        super(args);

        // TODO do we want to do anything else?
        PersistentInformationReader.setSystemBlocked(false);
    }

}
