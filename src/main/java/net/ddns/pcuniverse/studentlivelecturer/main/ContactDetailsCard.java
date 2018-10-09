package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Window;
import models.all.ContactDetails;

public class ContactDetailsCard extends HBox {

    public ContactDetailsCard(Window parent, ContactDetails contactDetails, String studentName, String studentEmail) {
        Circle contactImage = new Circle(30);
        if (contactDetails.getImage() == null) {
            contactImage.setFill(new ImagePattern(new Image(getClass().getClassLoader().getResourceAsStream("DefaultProfile.jpg"))));
        } else {
            contactImage.setFill(new ImagePattern(contactDetails.getImage()));
        }
        contactImage.setStroke(Color.BLACK);
        contactImage.setStrokeWidth(2);
        Text contactName = new Text(contactDetails.getName());
        contactName.setStyle("-fx-font-size: 22");
        Text contactPosition = new Text(contactDetails.getPosition());
        VBox contactMainPane = new VBox(contactName, contactPosition);
        contactMainPane.setAlignment(Pos.CENTER_LEFT);
        contactMainPane.setMinWidth(500);
        contactMainPane.setMaxWidth(500);
        Text contactNumber = new Text(contactDetails.getContactNumber());
        Hyperlink contactEmail = new Hyperlink(contactDetails.getEmail());
        contactEmail.setOnAction(e -> {
            new EmailDialog(parent, contactDetails.getName(), contactDetails.getEmail(), studentName, studentEmail).showDialog();
            contactEmail.setVisited(false);
        });
        contactEmail.setFocusTraversable(false);
        contactEmail.setStyle("-fx-effect: null;" +
                "-fx-text-fill: #8B0000;");
        setStyle("-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2;" +
                "-fx-min-width: 1000;" +
                "-fx-max-width: 1000;" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;");
        setSpacing(25);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(5));
        getChildren().addAll(contactImage, contactMainPane, contactNumber, contactEmail);
    }

}
