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
    private Role role;

    // Verwende deine vorhandene Container-Klasse
    private List<QualificationsContainer> qualificationProfile;

    public Company() {}

    public Company(String companyId, String name, List<String> departments, Role role, List<QualificationsContainer> qualificationProfile) {
        this.companyId = companyId;
        this.name = name;
        this.departments = departments;
        this.role = role;
        this.qualificationProfile = qualificationProfile;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<QualificationsContainer> getQualificationProfile() {
        return qualificationProfile;
    }

    public void setQualificationProfile(List<QualificationsContainer> qualificationProfile) {
        this.qualificationProfile = qualificationProfile;
    }
}


