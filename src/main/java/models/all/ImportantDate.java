package models.all;

import java.io.Serializable;

public class ImportantDate implements Serializable {

    private int id;
    private String date;
    private String description;

    public ImportantDate(int id, String date, String description) {
        this.id = id;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
