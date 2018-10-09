package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class NoticeboardCard extends StackPane {

    public NoticeboardCard(Window parent, String heading, String body, Boolean notification) {
        ImageView pushpinImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Pushpin.png")));
        pushpinImageView.setFitHeight(64);
        pushpinImageView.setFitWidth(64);
        HBox pushpinPane = new HBox(pushpinImageView);
        pushpinPane.setAlignment(Pos.CENTER);
        pushpinPane.setPadding(new Insets(-15, 0, -40, 0));
        Text headingLabel = new Text(heading);
        headingLabel.setWrappingWidth(400);
        headingLabel.getStyleClass().add("noteHeading");
        Text descriptionLabel = new Text(body);
        VBox descriptionPane = new VBox(descriptionLabel);
        descriptionLabel.setWrappingWidth(400);
        descriptionLabel.getStyleClass().add("noteDescription");
        VBox noticePane = new VBox(pushpinPane, headingLabel, descriptionPane);
        JFXButton dismissButton = new JFXButton("Dismiss");
        dismissButton.setFocusTraversable(false);
        dismissButton.setStyle("-fx-text-fill: black;" +
                "-fx-effect: null;");
        dismissButton.setOnAction(e -> {
            if (UserNotification.confirmationDialog(parent, "Dismiss Notification", "Are you sure you want to dismiss notification? Dismissed notifications is erased permanently!")) {

            }
        });
        VBox dismissPane = new VBox(dismissButton);
        if (notification) {
            noticePane.getChildren().add(dismissPane);
        }
        VBox.setVgrow(descriptionPane, Priority.ALWAYS);
        noticePane.setAlignment(Pos.TOP_LEFT);
        noticePane.setPadding(new Insets(0, 15, 15, 15));
        noticePane.setSpacing(15);
        int randomInt = (int) (Math.random() * 4);
        if (randomInt == 3) {
            noticePane.setStyle("-fx-background-color: #8ae1e3");
        } else if (randomInt == 2) {
            noticePane.setStyle("-fx-background-color: #cf90e3");
        } else if (randomInt == 1) {
            noticePane.setStyle("-fx-background-color: #82e367");
        } else {
            noticePane.setStyle("-fx-background-color: #dce334");
        }
        noticePane.setMinWidth(450);
        noticePane.setMaxWidth(450);
        //setPrefHeight(100);
        getChildren().add(noticePane);
        getStyleClass().add("noteShadow");
        setRotate((Math.random() * 4.0) - 2);
    }

}
