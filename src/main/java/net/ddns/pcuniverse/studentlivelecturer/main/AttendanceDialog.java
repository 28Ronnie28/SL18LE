package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import models.lecturer.AttendanceRecord;

import java.util.ArrayList;
import java.util.List;

public class AttendanceDialog extends CustomDialogSkin {

    public AttendanceDialog(Window parent, ConnectionHandler connectionHandler, List<String[]> students, int classID) {
        initOwner(parent);
        Text settingsText = new Text("Add Attendance");
        settingsText.getStyleClass().add("heading-text");
        List<AttendancePane> attendancePanes = new ArrayList<>();
        VBox attendanceInnerPane = new VBox();
        for (String[] s : students) {
            AttendancePane attendancePane = new AttendancePane(s[0], s[1]);
            attendancePanes.add(attendancePane);
            attendanceInnerPane.getChildren().add(attendancePane);
        }
        attendanceInnerPane.setSpacing(15);
        ScrollPane attendanceScrollPane = new ScrollPane(attendanceInnerPane);
        attendanceInnerPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 10;
            double width = attendanceScrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = attendanceScrollPane.getVvalue();
            attendanceScrollPane.setVvalue(vValue + -deltaY / width);
        });
        VBox.setVgrow(attendanceScrollPane, Priority.ALWAYS);
        attendanceInnerPane.prefHeightProperty().bind(attendanceScrollPane.heightProperty().subtract(46D));
        attendanceScrollPane.setFitToWidth(true);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            //TODO
            List<String[]> attendanceResults = new ArrayList<>();
            for (AttendancePane ap : attendancePanes) {
                attendanceResults.add(ap.getAttendance());
            }
            connectionHandler.sendData(new AttendanceRecord(classID, attendanceResults));
            closeAnimation();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> closeAnimation());
        HBox buttonPane = new HBox(submitButton, cancelButton);
        buttonPane.setSpacing(15);
        buttonPane.setAlignment(Pos.CENTER);
        VBox settingsInnerPane = new VBox(settingsText, attendanceScrollPane, buttonPane);
        settingsInnerPane.getChildren().addAll();
        settingsInnerPane.setSpacing(25);
        settingsInnerPane.setPadding(new javafx.geometry.Insets(20));
        settingsInnerPane.setAlignment(Pos.CENTER);
        settingsInnerPane.setStyle("-fx-background-color: #007FA3;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;");
        settingsInnerPane.setMaxHeight(800);
        settingsInnerPane.setMinHeight(800);
        VBox settingsPane = new VBox(settingsInnerPane);
        setWidth(1000);
        settingsPane.setAlignment(Pos.CENTER);
        getDialogPane().setContent(settingsPane);
    }

    public class AttendancePane extends HBox {

        private String studentNumber;
        private ToggleGroup toggleGroup = new ToggleGroup();
        private ToggleButton presentButton;
        private ToggleButton earlyButton;
        private ToggleButton lateButton;
        private ToggleButton absentButton;

        public AttendancePane(String studentNumber, String name) {
            this.studentNumber = studentNumber;
            Text nameText = new Text(name + " - " + studentNumber);
            presentButton = new ToggleButton("Present");
            presentButton.setToggleGroup(toggleGroup);
            presentButton.setUserData("P");
            presentButton.setMinWidth(100);
            earlyButton = new ToggleButton("Left Early");
            earlyButton.setToggleGroup(toggleGroup);
            earlyButton.setUserData("E");
            earlyButton.setMinWidth(100);
            lateButton = new ToggleButton("Late");
            lateButton.setToggleGroup(toggleGroup);
            lateButton.setUserData("L");
            lateButton.setMinWidth(100);
            absentButton = new ToggleButton("Absent");
            absentButton.setToggleGroup(toggleGroup);
            absentButton.setUserData("A");
            absentButton.setMinWidth(100);
            absentButton.setSelected(true);
            toggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
                if (newValue == null) {
                    oldValue.setSelected(true);
                }
            });
            Pane fillerPane = new Pane();
            HBox.setHgrow(fillerPane, Priority.ALWAYS);
            getChildren().addAll(nameText, fillerPane, presentButton, earlyButton, lateButton, absentButton);
            setSpacing(5);
            setAlignment(Pos.CENTER);
        }

        String[] getAttendance() {
            return new String[]{studentNumber, (String) toggleGroup.getSelectedToggle().getUserData()};
        }

    }

}
