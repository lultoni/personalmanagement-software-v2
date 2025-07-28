package model.json;

import java.util.List;

/**
 * Qualification Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */
public class Qualification {

    private String roleId;
    private int requiredYears;
    private List<String> certifications;
    private String description;
    private List<String> followupSkills;
    private List<String> requiredSkills;

    public Qualification() {
        // TODO write this constructor
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
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
        this.certifications = certifications;
    }

    public List<String> getFollowupSkills() {
        return followupSkills;
    }

    public void setFollowupSkills(List<String> followupSkills) {
        this.followupSkills = followupSkills;
    }
}
