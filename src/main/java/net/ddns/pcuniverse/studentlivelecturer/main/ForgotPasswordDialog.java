package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class ForgotPasswordDialog extends CustomDialogSkin {

    public ForgotPasswordDialog(Window parent, ConnectionHandler connectionHandler) {
        initOwner(parent);
        Text forgotPasswordText = new Text("Forgot Password");
        forgotPasswordText.getStyleClass().add("heading-text");
        TextField studentNumberTextField = new TextField();
        studentNumberTextField.setPromptText("Student number");
        studentNumberTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);");
        JFXButton sendButton = new JFXButton("Send Password");
        sendButton.getStyleClass().add("dialog-button");
        sendButton.setOnAction(e -> {
            if (!studentNumberTextField.getText().isEmpty() && studentNumberTextField.getText().matches("[A-Z]{2}[0-9]{4}-[0-9]{4}")) {
                connectionHandler.forgotPassword(studentNumberTextField.getText());
                closeAnimation();
            } else {
                UserNotification.showErrorMessage("Forgot Password", "Student number is invalid");
            }
        });
        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.getStyleClass().add("dialog-button");
        cancelButton.setOnAction(e -> closeAnimation());
        HBox buttonPane = new HBox(sendButton, cancelButton);
        buttonPane.setSpacing(15);
        buttonPane.setAlignment(Pos.CENTER);
        VBox innerPane = new VBox(forgotPasswordText, studentNumberTextField, buttonPane);
        innerPane.setPadding(new Insets(20, 50, 20, 50));
        innerPane.setSpacing(20);
        innerPane.setMinWidth(600);
        innerPane.setMaxWidth(600);
        innerPane.setAlignment(Pos.CENTER);
        innerPane.setStyle("-fx-background-color: #007FA3;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;");
        VBox contentPane = new VBox(innerPane);
        contentPane.setAlignment(Pos.CENTER);
        setWidth(600);
        getDialogPane().setContent(contentPane);
    }

}
