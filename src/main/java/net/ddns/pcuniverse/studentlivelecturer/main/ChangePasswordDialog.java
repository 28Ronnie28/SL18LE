package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class ChangePasswordDialog extends CustomDialogSkin {

    public ChangePasswordDialog(Window parent, Boolean defaultPassword, ConnectionHandler connectionHandler) {
        initOwner(parent);
        VBox innerPane = new VBox();
        Text changePasswordHeading = new Text("Change Password");
        changePasswordHeading.getStyleClass().add("heading-text");
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);");
        JFXButton changeButton = new JFXButton("Change");
        changeButton.getStyleClass().add("dialog-button");
        changeButton.setOnAction(e -> {
            if (!defaultPassword) {
                if (!currentPasswordField.getText().isEmpty() && !newPasswordField.getText().isEmpty()) {
                    if (!currentPasswordField.getText().equals(newPasswordField.getText())) {
                        if (currentPasswordField.getText().length() >= 5 && newPasswordField.getText().length() >= 5) {
                            if (currentPasswordField.getText().matches("[a-zA-Z0-9]*") && newPasswordField.getText().matches("[a-zA-Z0-9]*")) {
                                if (connectionHandler.changePassword(currentPasswordField.getText(), newPasswordField.getText())) {
                                    UserNotification.showConfirmationMessage("Change Password", "Successfully changed your password");
                                    closeAnimation();
                                    new SettingsDialog(parent, connectionHandler).showDialog();
                                } else {
                                    UserNotification.showErrorMessage("Change Password", "Incorrect current password entered");
                                }
                            } else {
                                UserNotification.showErrorMessage("Change Password", "Only alphanumeric passwords are accepted (letters and numbers)");
                            }
                        } else {
                            UserNotification.showErrorMessage("Change Password", "Passwords must be at least 5 characters long");
                        }
                    } else {
                        UserNotification.showErrorMessage("Change Password", "New password can't match your previous password");
                    }
                } else {
                    UserNotification.showErrorMessage("Change Password", "Passwords can't be empty");
                }
            } else {
                if (!newPasswordField.getText().isEmpty()) {
                    if (newPasswordField.getText().length() >= 5) {
                        if (newPasswordField.getText().matches("[a-zA-Z0-9]*")) {
                            if (connectionHandler.changeDefaultPassword(newPasswordField.getText())) {
                                UserNotification.showConfirmationMessage("Change Password", "Successfully changed password");
                                closeAnimation();
                            } else {
                                UserNotification.showErrorMessage("Change Password", "Could not change password");
                            }
                        } else {
                            UserNotification.showErrorMessage("Change Password", "Only alphanumeric passwords are accepted (letters and numbers)");
                        }
                    } else {
                        UserNotification.showErrorMessage("Change Password", "Password must be at least 5 characters long");
                    }
                } else {
                    UserNotification.showErrorMessage("Change Password", "Password can't be empty");
                }
            }
        });
        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.getStyleClass().add("dialog-button");
        cancelButton.setOnAction(e -> {
            closeAnimation();
            new SettingsDialog(parent, connectionHandler).showDialog();
        });
        HBox buttonPane = new HBox(changeButton);
        if (!defaultPassword) {
            buttonPane.getChildren().add(cancelButton);
            innerPane.getChildren().addAll(changePasswordHeading, currentPasswordField, newPasswordField, buttonPane);
        } else {
            innerPane.getChildren().addAll(changePasswordHeading, newPasswordField, buttonPane);
        }
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(25);
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
