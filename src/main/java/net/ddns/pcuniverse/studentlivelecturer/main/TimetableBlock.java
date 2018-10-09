package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class TimetableBlock extends HBox{

    private Color[] accentColour = {Color.rgb(255, 184, 28), Color.rgb(234, 118, 00), Color.rgb(219, 00, 32), Color.rgb(18, 178, 166), Color.rgb(158, 00, 126), Color.rgb(234, 06, 126), Color.rgb(132, 189, 00)};

    public TimetableBlock(String text, int number) {
        Label label = new Label(text);
        label.getStyleClass().add("timetable-text");
        label.setAlignment(Pos.TOP_CENTER);
        getChildren().add(label);
        setBorder(new Border(new BorderStroke(accentColour[number], BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 8))));
    }

    public void setSelected(Boolean selected) {
        if (selected) {
            setStyle("-fx-background-color: #007FA3");
        } else {
            setStyle("-fx-background-color: transparent");
        }
    }

}
