package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;
import models.all.ClassFile;

import java.util.Optional;

public class UserNotification {


    public static void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showConfirmationMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showMessage(Window parent, String heading, String message) {
        new CustomDialog(parent, heading, message, new JFXButton("Ok"));
    }

    public static String getText(String title, String message) {
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static void showFileDetails(Window parent, ClassFile classFile) {
        CustomDialog customDialog = new CustomDialog(parent, Display.getFileNameWithoutExtension(classFile.getFileName()), "Extension: " + Display.getFileExtension(classFile.getFileName()) + "\nSize          : " + (classFile.getFileLength() / 1024) + "kB", new JFXButton("Ok"));
        customDialog.showDialog();
    }

    public static Boolean confirmationDialog(Window parent, String heading, String body) {
        CustomDialog customDialog = new CustomDialog(parent, heading, body, new JFXButton("Yes"), new JFXButton("Cancel"));
        return customDialog.showDialog() == 1;
    }

    public static int showLecturerContactMethod(Window parent) {
        CustomDialog customDialog = new CustomDialog(parent, "Contact Lecturer", "Do you want to contact lecturer by email or directly?", new JFXButton("Email"), new JFXButton("Direct Message"), new JFXButton("Cancel"));
        return customDialog.showDialog();
    }

}
