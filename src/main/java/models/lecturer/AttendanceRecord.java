package models.lecturer;

import java.io.Serializable;
import java.util.List;

public class AttendanceRecord implements Serializable {

    private int classID;
    private List<String[]> attendance;

    public AttendanceRecord(int classID, List<String[]> attendance) {
        this.classID = classID;
        this.attendance = attendance;
    }

    public int getClassID() {
        return classID;
    }

    public List<String[]> getAttendance() {
        return attendance;
    }
}
