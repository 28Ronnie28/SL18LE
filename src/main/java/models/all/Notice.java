package models.all;

import java.io.Serializable;

public class Notice implements Serializable {

    private int id;
    private String heading;
    private String description;
    private String tag;
    private String expiryDate;

    public Notice(int id, String heading, String description, String tag, String expiryDate) {
        this.id = id;
        this.heading = heading;
        this.description = description;
        this.tag = tag;
        this.expiryDate = expiryDate;
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

    public String getExpiryDate() {
        return expiryDate;
    }
}
