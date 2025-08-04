package model.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Skills Klasse.
 * Spiegelt das json-Objekt wider.
 *
 * @author Dorian Gläske
 * @version 1.0
 * @since 2025-07-28
 */
public class Skills {
    private List<String> Skills;

    public Skills() {
    }
    public Skills(List<String> Skills) {
        this.Skills = Skills;
    }

    public List<String> getSkills() {
        return Skills;
    }
    public void setSkills(List<String> Skills) {
        this.Skills = Skills;
    }

}
