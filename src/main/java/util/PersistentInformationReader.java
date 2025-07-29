package util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Statische Klasse, welche die Informationen aus der Datei 'persistent.information' liest und schreibt.
 * @version 1.3
 * @author Elias Glauert
 * @since 2025-07-12
 */
public class PersistentInformationReader {

    private static final String file_path = "src/main/resources/persistent.information";
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /* Setup und Dateiverwaltung */

    private static Properties loadProperties() {
        Properties properties = new Properties();
        File file = new File(file_path);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("loadProperties() - File created: " + file_path);
                } else {
                    System.err.println("loadProperties() - Failed to create file: " + file_path);
                }
            } catch (IOException e) {
                System.err.println("loadProperties() - Error creating file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            properties.load(reader);
            System.out.println("loadProperties() - Properties loaded: " + properties);
        } catch (IOException e) {
            System.err.println("loadProperties() - Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }

    private static void saveProperties(Properties properties) {
        System.out.println("saveProperties() - Saving properties to file: " + file_path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_path))) {
            properties.store(writer, null);
            System.out.println("saveProperties() - Properties saved.");
        } catch (IOException e) {
            System.err.println("saveProperties() - Error saving properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Getter-Methoden */

    public static Date getBackupDate() {
        System.out.println(getCallingMethodName() + " -> getBackupDate()");
        Properties properties = loadProperties();
        String dateStr = properties.getProperty("backup.date", "");
        if (dateStr.isEmpty()) {
            return null;
        }
        try {
            return date_format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isSystemBlocked() {
        System.out.println(getCallingMethodName() + " -> isSystemBlocked()");
        Properties properties = loadProperties();
        String blockedStr = properties.getProperty("system.blocked", "False");
        return Boolean.parseBoolean(blockedStr);
    }

    public static boolean isUserLoggedIn() {
        System.out.println(getCallingMethodName() + " -> isUserLoggedIn()");
        Properties properties = loadProperties();
        String loggedInStr = properties.getProperty("user.logged.in", "False");
        return Boolean.parseBoolean(loggedInStr);
    }

    public static int getLoggedInUserId() {
        System.out.println(getCallingMethodName() + " -> getLoggedInUserId()");
        Properties properties = loadProperties();
        String userIdStr = properties.getProperty("user.id", "-1");
        try {
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getDatePattern() {
        return date_format.toPattern();
    }

    /* Setter-Methoden */

    public static void setBackupDate(String backupDate) {
        System.out.println(getCallingMethodName() + " -> setBackupDate()");
        Properties properties = loadProperties();
        properties.setProperty("backup.date", backupDate);
        saveProperties(properties);
    }

    public static void setSystemBlocked(boolean isSystemBlocked) {
        System.out.println(getCallingMethodName() + " -> setSystemBlocked()");
        Properties properties = loadProperties();
        properties.setProperty("system.blocked", Boolean.toString(isSystemBlocked));
        saveProperties(properties);
    }

    private static void setUserLoggedIn(boolean isLoggedIn) {
        System.out.println(getCallingMethodName() + " -> setUserLoggedIn() - Setting user logged in status to: " + isLoggedIn);
        Properties properties = loadProperties();
        properties.setProperty("user.logged.in", Boolean.toString(isLoggedIn));
        saveProperties(properties);
        System.out.println(getCallingMethodName() + " -> setUserLoggedIn() - Properties saved. user.logged.in: " + properties.getProperty("user.logged.in"));
    }

    public static void setLoggedInUserId(int userId) {
        System.out.println(getCallingMethodName() + " -> setLoggedInUserId()");
        Properties properties = loadProperties();
        properties.setProperty("user.id", Integer.toString(userId));
        saveProperties(properties);
        setUserLoggedIn(userId != -1);
    }

    /* Hilfsmethoden */

    public static void clearLoggedInUser() {
        System.out.println(getCallingMethodName() + " -> clearLoggedInUser() - Logging the User out");
        setLoggedInUserId(-1);
        System.out.println(getCallingMethodName() + " -> clearLoggedInUser() - User logged out. UserID set to -1.");
    }

    private static String getCallingMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3].getMethodName() : "Unknown Caller";
    }
}