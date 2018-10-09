package models.all;

import java.io.Serializable;

public class Attendance implements Serializable {

    private int attendanceID;
    private String attendanceDate;
    private String attendance;

    public Attendance(int attendanceID, String attendanceDate, String attendance) {
        this.attendanceID = attendanceID;
        this.attendanceDate = attendanceDate;
        this.attendance = attendance;
    }

    public int getAttendanceID() {
        return attendanceID;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
