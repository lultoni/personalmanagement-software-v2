package model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * QualificationsContainer Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */

public class QualificationsContainer {

    @JsonProperty("qualifications") // This maps to the JSON key "qualifications"
    private List<Qualification> qualifications;

    // Default constructor is crucial for Jackson
    public QualificationsContainer() {
        this.qualifications = new ArrayList<>(); // Initialize to prevent NullPointerException
    }

    // Getter for the list of qualifications
    public List<Qualification> getQualifications() {
        return qualifications;
    }

    // Setter for the list of qualifications
    public void setQualifications(List<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    @Override
    public String toString() {
        return "QualificationsContainer{" +
                "qualifications=" + qualifications +
                '}';
    }
}
