package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.all.Attendance;

import java.util.List;

public class AttendanceCard extends HBox{

    public AttendanceCard(String className, List<Attendance> attendance) {
        Text classHeading = new Text(className);
        classHeading.setStyle("-fx-font-size: 22");
        int classesHeld = 0;
        int classesAttended = 0;
        int classesEarlyLeft = 0;
        for (Attendance a : attendance) {
            classesHeld++;
            if (a.getAttendance().equals("P")) {
                classesAttended++;
            } else if (a.getAttendance().equals("E") || a.getAttendance().equals("L")) {
                classesEarlyLeft++;
            }
        }
        Text classesHeldText = new Text("Classes held: " + classesHeld);
        Text classesAttendedText = new Text("Classes attended: " + classesAttended);
        Text classesEarlyLeftText = new Text("Classes late/left early: " + classesEarlyLeft);
        VBox statPane = new VBox(classesHeldText, classesAttendedText, classesEarlyLeftText);
        statPane.setAlignment(Pos.CENTER_LEFT);
        Text percentageText = new Text();
        if (classesHeld == 0) {
             percentageText.setText("N/A");
        } else {
            percentageText.setText(String.format("%3.0f%s", 100D * (classesAttended + (classesEarlyLeft / 2D)) / classesHeld, "%"));
        }
        percentageText.setStyle("-fx-font-size: 42");
        HBox innerPane = new HBox(statPane, percentageText);
        HBox.setHgrow(statPane, Priority.ALWAYS);
        innerPane.setPadding(new Insets(15));
        innerPane.setSpacing(15);
        StackPane colorGrading = new StackPane();
        colorGrading.setMinWidth(25);
        colorGrading.setMaxWidth(25);
        VBox.setVgrow(colorGrading, Priority.ALWAYS);
        if (classesHeld == 0) {
            colorGrading.setStyle("-fx-background-color: #005A70");
        } else {
            if ((classesAttended + (classesEarlyLeft / 2.0)) / classesHeld < 0.5) {
                colorGrading.setStyle("-fx-background-color: #DB0020");
            } else if ((classesAttended + (classesEarlyLeft / 2.0)) / classesHeld < 0.75) {
                colorGrading.setStyle("-fx-background-color: #EA7600");
            } else {
                colorGrading.setStyle("-fx-background-color: #008638");
            }
        }
        VBox mainPane = new VBox(classHeading, innerPane);
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setStyle("-fx-background-color: #D4EAE4");
        mainPane.setSpacing(15);
        mainPane.setPadding(new Insets(10));
        HBox.setHgrow(mainPane, Priority.ALWAYS);
        getChildren().addAll(mainPane, colorGrading);
        setMinWidth(700);
        setMaxWidth(700);
    }

}
