package core.events;

import db.DatabaseManager;
import util.PersistentInformationReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Event für das Löschen von Benachrichtigungen.
 * @author Elias Glauert
 * @version 1.1
 * @since 2025-07-10
 */
public class Event_CreateBackup extends Event {

    /**
     * Konstruktor für Event.
     *
     * @param args Parameters for specific function calls upon the events creation.
     * @author Elias Glauert
     */
    public Event_CreateBackup(Object[] args) {
        super(args);

        DatabaseManager mainManager = (DatabaseManager) args[0];
        DatabaseManager otherManager = (DatabaseManager) args[1];
        mainManager.copyDatabaseToOtherDbManager(otherManager);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PersistentInformationReader.getDatePattern());
        String formattedDate = now.format(formatter);
        PersistentInformationReader.setBackupDate(formattedDate);
    }

}
