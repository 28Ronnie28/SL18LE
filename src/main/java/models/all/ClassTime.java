package models.all;

import java.io.Serializable;

public class ClassTime implements Serializable {

    private int id;
    private int classID;
    private String roomNumber;
    private int dayOfWeek;
    private int startSlot;
    private int endSlot;

    public ClassTime(int id, int classID, String roomNumber, int dayOfWeek, int startSlot, int endSlot) {
        this.id = id;
        this.classID = classID;
        this.roomNumber = roomNumber;
        this.dayOfWeek = dayOfWeek;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    public int getId() {
        return id;
    }

    public int getClassID() {
        return classID;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getEndSlot() {
        return endSlot;
    }
}
