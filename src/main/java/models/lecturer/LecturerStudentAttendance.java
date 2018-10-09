package models.lecturer;

import java.io.Serializable;
import java.util.List;

public class LecturerStudentAttendance implements Serializable {

    private String studentFirstName;
    private String studentLastName;
    private String studentNumber;
    private List<LecturerStudentAttendanceClass> classes;

    public LecturerStudentAttendance(String studentFirstName, String studentLastName, String studentNumber, List<LecturerStudentAttendanceClass> classes) {
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

    public List<LecturerStudentAttendanceClass> getClasses() {
        return classes;
    }
}
