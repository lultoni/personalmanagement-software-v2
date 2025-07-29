package model.json;

import java.util.ArrayList;

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
    private ArrayList<String> certifications;
    private String description;
    private ArrayList<String> followupSkills;
    private ArrayList<String> requiredSkills;

    public Qualification() {
        // Optional: Initialisierung der Listen, um NullPointerExceptions zu vermeiden
        this.certifications = new ArrayList<>();
        this.followupSkills = new ArrayList<>();
        this.requiredSkills = new ArrayList<>();
    }

    public Qualification(String roleId, int requiredYears, ArrayList<String> certifications,
                         String description, ArrayList<String> followupSkills, ArrayList<String> requiredSkills) {
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

    public ArrayList<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(ArrayList<String> certifications) {
        this.certifications = (certifications != null) ? new ArrayList<>(certifications) : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getFollowupSkills() {
        return followupSkills;
    }

    public void setFollowupSkills(ArrayList<String> followupSkills) {
        this.followupSkills = (followupSkills != null) ? new ArrayList<>(followupSkills) : new ArrayList<>();
    }

    public ArrayList<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(ArrayList<String> requiredSkills) {
        this.requiredSkills = (requiredSkills != null) ? new ArrayList<>(requiredSkills) : new ArrayList<>();
    }
}
