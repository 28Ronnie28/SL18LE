package models.lecturer;

import models.all.Attendance;

import java.io.Serializable;
import java.util.List;

public class LecturerStudentAttendanceClass implements Serializable {

    private int classID;
    private String moduleName;
    private List<Attendance> attendance;

    public LecturerStudentAttendanceClass(int classID, String moduleName, List<Attendance> attendance) {
        this.classID = classID;
        this.moduleName = moduleName;
        this.attendance = attendance;
    }

    public int getClassID() {
        return classID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public List<Attendance> getAttendance() {
        return attendance;
    }
}
