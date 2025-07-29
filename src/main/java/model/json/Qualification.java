package model.json;

import java.util.List;
import java.util.ArrayList; // Empfohlen für die Initialisierung von Listen

/**
 * Qualification Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.2
 * @since 2025-07-29
 */

public class Qualification {

    private String roleId;
    private int requiredYears;
    private List<String> certifications;
    private String description;
    private List<String> followupSkills;
    private List<String> requiredSkills;

    // Standardkonstruktor (No-Arg Constructor)
    public Qualification() {
        // Optional: Initialisierung der Listen, um NullPointerExceptions zu vermeiden
        this.certifications = new ArrayList<>();
        this.followupSkills = new ArrayList<>();
        this.requiredSkills = new ArrayList<>();
    }

    // Parametrisierter Konstruktor
    public Qualification(String roleId, int requiredYears, List<String> certifications,
                         String description, List<String> followupSkills, List<String> requiredSkills) {
        this.roleId = roleId;
        this.requiredYears = requiredYears;
        this.certifications = (certifications != null) ? new ArrayList<>(certifications) : new ArrayList<>();
        this.description = description;
        this.followupSkills = (followupSkills != null) ? new ArrayList<>(followupSkills) : new ArrayList<>();
        this.requiredSkills = (requiredSkills != null) ? new ArrayList<>(requiredSkills) : new ArrayList<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public int getRequiredYears() {
        return requiredYears;
    }

    public void setRequiredYears(int requiredYears) {
        this.requiredYears = requiredYears;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = (certifications != null) ? new ArrayList<>(certifications) : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFollowupSkills() {
        return followupSkills;
    }

    public void setFollowupSkills(List<String> followupSkills) {
        this.followupSkills = (followupSkills != null) ? new ArrayList<>(followupSkills) : new ArrayList<>();
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = (requiredSkills != null) ? new ArrayList<>(requiredSkills) : new ArrayList<>();
    }
}
