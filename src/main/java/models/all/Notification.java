package models.all;

import java.io.Serializable;

public class Notification implements Serializable {

    private int id;
    private String heading;
    private String description;
    private String tag;

    public Notification(int id, String heading, String description, String tag) {
        this.id = id;
        this.heading = heading;
        this.description = description;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getTag() {
        return tag;
    }
}
