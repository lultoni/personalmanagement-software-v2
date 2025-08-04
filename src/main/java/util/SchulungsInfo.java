package util;

import model.db.Employee;
import core.CompanyStructureManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SchulungsInfo {
    private List<String> absolvierteSchulungen;
    private Optional<List<String>> möglicheSchulungen;
    private ArrayList<String> offeneSchulungen;
    private Employee employee;

    CompanyStructureManager manager = CompanyStructureManager.getInstance();

    public SchulungsInfo(Employee employee) throws IOException {
        this.employee = employee;
        setAbsolvierenteSchulungen(employee);
        setMöglicheSchulungen(employee);
        this.offeneSchulungen = new ArrayList<>();
    }

    public void setOffeneSchulungen(String schulungen) {
        this.offeneSchulungen = (ArrayList<String>) Arrays.asList(schulungen.split(","));
    }
    public ArrayList<String> getOffeneSchulungen() {
        return offeneSchulungen;
    }
    public void removeOffeneSchulungen(String schulung) {
        this.offeneSchulungen.remove(schulung);
    }

    public void setAbsolvierenteSchulungen(Employee employee) {
        this.absolvierteSchulungen = Arrays.asList(employee.getQualifications().split(","));
    }
    public void setAbsolvierenteSchulungen(String schulung) {
        this.absolvierteSchulungen = Arrays.asList(schulung.split(","));
    }
    public List<String> getAbsolvierenteSchulungen(){
        return absolvierteSchulungen;
    }

    public void setMöglicheSchulungen(Employee employee) {
        this.möglicheSchulungen =manager.getFollowUpSkillsForQualification(employee.getQualifications());
    }
    public Optional<List<String>> getMöglicheSchulungen() {
        return möglicheSchulungen;
    }

}

