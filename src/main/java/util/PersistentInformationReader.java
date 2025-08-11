package util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Statische Klasse, welche die Informationen aus der Datei 'persistent.information' liest und schreibt.
 * @version 1.4
 * @author Elias Glauert, Joshua Sperber
 * @since 2025-07-12
 */
public class PersistentInformationReader {

    private static final String file_path = "src/main/resources/persistent.information";
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int fallback_employee_id = -1;

    private static Properties loadProperties() {
        Properties properties = new Properties();
        File file = new File(file_path);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file_path);
                } else {
                    System.err.println("Failed to create file: " + file_path);
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            properties.load(reader);
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }

    private static void saveProperties(Properties properties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_path))) {
            properties.store(writer, null);
        } catch (IOException e) {
            System.err.println("Error saving properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Date getBackupDate() {
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
        Properties properties = loadProperties();
        String blockedStr = properties.getProperty("system.blocked", "False");
        return Boolean.parseBoolean(blockedStr);
    }

    public static boolean isUserLoggedIn() {
        Properties properties = loadProperties();
        String loggedInStr = properties.getProperty("user.logged.in", "False");
        return Boolean.parseBoolean(loggedInStr);
    }

    public static int getLoggedInUserId() {
        Properties properties = loadProperties();
        String userIdStr = properties.getProperty("user.id", String.valueOf(fallback_employee_id));
        try {
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return fallback_employee_id;
        }
    }

    public static String getDatePattern() {
        return date_format.toPattern();
    }

    public static void setBackupDate(String backupDate) {
        Properties properties = loadProperties();
        properties.setProperty("backup.date", backupDate);
        saveProperties(properties);
    }

    public static void setSystemBlocked(boolean isSystemBlocked) {
        Properties properties = loadProperties();
        properties.setProperty("system.blocked", Boolean.toString(isSystemBlocked));
        saveProperties(properties);
    }

    private static void setUserLoggedIn(boolean isLoggedIn) {
        Properties properties = loadProperties();
        properties.setProperty("user.logged.in", Boolean.toString(isLoggedIn));
        saveProperties(properties);
    }

    public static void setLoggedInUserId(int userId) {
        Properties properties = loadProperties();
        properties.setProperty("user.id", Integer.toString(userId));
        saveProperties(properties);
        setUserLoggedIn(userId != fallback_employee_id);
    }

    public static void clearLoggedInUser() {
        setLoggedInUserId(fallback_employee_id);
    }
}