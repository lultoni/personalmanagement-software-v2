package core.events;

import db.DatabaseManager;

/**
 * Event für das Löschen von Benachrichtigungen.
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-10
 */
public class Event_LoadBackup extends Event {

    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_LoadBackup(Object[] args) {
        super(args);

        DatabaseManager mainManager = (DatabaseManager) args[0];
        DatabaseManager otherManager = (DatabaseManager) args[1];
        otherManager.copyDatabaseToOtherDbManager(mainManager);
    }

}
