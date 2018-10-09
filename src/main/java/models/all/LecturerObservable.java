package models.all;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class LecturerObservable {

    private Lecturer lecturer;
    public volatile BooleanProperty update;

    public LecturerObservable(Lecturer lecturer) {
        this.lecturer = lecturer;
        update = new SimpleBooleanProperty(false);
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public void update() {
        update.setValue(false);
        update.setValue(true);
    }
}
