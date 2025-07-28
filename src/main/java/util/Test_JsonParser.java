package util;

import java.io.IOException;

/**
 * Testklasse für den JsonParser.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */
public class Test_JsonParser {

    public static void main(String[] args) throws IOException {

        System.out.println("Firma: " + JsonParser.getCompany().getName());
        System.out.println("Department: " + JsonParser.findeDepartment("dept-compliance"));
        System.out.println("Qualifikation: " + JsonParser.findeQualification("qual-advanced-brickwork"));

    }

}
