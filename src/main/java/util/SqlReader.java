package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Diese Klasse ist dafür da die in .txt-Dateien gespeicherte SQL-Queries in der Form eines Strings zurückzugeben.
 * Klasse wird statisch verwendet.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-09
 */
public class SqlReader {

    public static String giveCommand(String commandName) {
        String ret_string = "";
        String path_name = "src/main/resources/sql/" + commandName + ".txt";
        try {
            File file = new File(path_name);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                ret_string += data + "\n";
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file at '" + path_name + "'!");
            throw new RuntimeException(e);
        }
        return ret_string;
    }

}
