package models.lecturer;

import models.all.Result;

import java.io.Serializable;
import java.util.List;

public class LecturerStudentResultClass implements Serializable{

    private int classID;
    private String moduleName;
    private List<Result> results;

    public LecturerStudentResultClass(int classID, String moduleName, List<Result> results) {
        this.classID = classID;
        this.moduleName = moduleName;
        this.results = results;
    }

    public int getClassID() {
        return classID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public List<Result> getResults() {
        return results;
    }
}
