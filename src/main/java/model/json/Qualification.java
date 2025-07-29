// src/main/java/model/json/Qualification.java
package model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
        * Qualifications Klasse.
        * Spiegelt das json-Objekt wider.
        *
        * @author Dorian Gläske
        * @version 1.3
        * @since 2025-07-28
        */

public class Qualification {

    private String roleId;
    private String description;

    @JsonProperty("requiredSkills")
    private List<String> requiredSkills;

    private int requiredYears;

    private List<String> certifications;

    @JsonProperty("followupSkills")
    private List<String> followupSkills;

    // Default constructor is crucial for Jackson
    public Qualification() {
        this.requiredSkills = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.followupSkills = new ArrayList<>();
    }

    // Parameterized constructor (optional, but good practice for object creation)
    public Qualification(String roleId, String description, List<String> requiredSkills,
                         int requiredYears, List<String> certifications, List<String> followupSkills) {
        this.roleId = roleId;
        this.description = description;
        this.requiredSkills = (requiredSkills != null) ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        this.requiredYears = requiredYears;
        this.certifications = (certifications != null) ? new ArrayList<>(certifications) : new ArrayList<>();
        this.followupSkills = (followupSkills != null) ? new ArrayList<>(followupSkills) : new ArrayList<>();
    }

    // --- Getters and Setters for all fields ---

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public int getRequiredYears() { return requiredYears; }
    public void setRequiredYears(int requiredYears) { this.requiredYears = requiredYears; }

    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }

    public List<String> getFollowupSkills() { return followupSkills; }
    public void setFollowupSkills(List<String> followupSkills) { this.followupSkills = followupSkills; }

    @Override
    public String toString() {
        return "Qualification{" +
                "roleId='" + roleId + '\'' +
                ", description='" + description + '\'' +
                ", requiredSkills=" + requiredSkills +
                ", requiredYears=" + requiredYears +
                ", certifications=" + certifications +
                ", followupSkills=" + followupSkills +
                '}';
    }
}