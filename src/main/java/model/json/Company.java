package model.json;

import java.util.List;

/**
 * Company Klasse.
 * Spiegelt das JSON-Objekt wider.
 */
public class Company {

    private String companyId;
    private String name;
    private List<String> departments;

    // Verwende deine vorhandene Container-Klasse
    private List<QualificationsContainer> qualificationProfile;

    public Company() {}

    public Company(String companyId, String name, List<String> departments) {
        this.companyId = companyId;
        this.name = name;
        this.departments = departments;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }
}


