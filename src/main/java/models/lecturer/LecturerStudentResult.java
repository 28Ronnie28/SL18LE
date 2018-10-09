package models.lecturer;

import java.io.Serializable;
import java.util.List;

public class LecturerStudentResult implements Serializable {

    private String studentFirstName;
    private String studentLastName;
    private String studentNumber;
    private List<LecturerStudentResultClass> classes;

    public LecturerStudentResult(String studentFirstName, String studentLastName, String studentNumber, List<LecturerStudentResultClass> classes) {
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.studentNumber = studentNumber;
        this.classes = classes;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public List<LecturerStudentResultClass> getClasses() {
        return classes;
    }
}
