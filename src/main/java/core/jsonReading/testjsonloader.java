package core.jsonReading;

import java.io.IOException;

public class testjsonloader {
    public static void main(String[] args) throws IOException {
        jsonLoader system = new jsonLoader();

        system.ladeCompany("src/main/resources/json/01_company.json");
        system.ladeDepartments("src/main/resources/json/02_department.json");

        System.out.println("Firma: " + system.getCompany().getName());
        System.out.println("Department: " + system.findeDepartment("dept-compliance"));
        System.out.println("Qualifikation: " + system.findeQualification("qual-advanced-brickwork"));


    }
}
