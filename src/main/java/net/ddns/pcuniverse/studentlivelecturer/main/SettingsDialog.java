package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXToggleButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.awt.*;
import java.net.URI;

public class SettingsDialog extends CustomDialogSkin{

    public SettingsDialog(Window parent, ConnectionHandler connectionHandler) {
        initOwner(parent);
        Text settingsText = new Text("Settings");
        settingsText.getStyleClass().add("heading-text");
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.getStyleClass().add("settingsButton");
        changePasswordButton.setOnAction(e -> {
            closeAnimation();
            new ChangePasswordDialog(parent, false, connectionHandler).showDialog();
        });
        Button aboutButton = new Button("About");
        aboutButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://pcuniverse.ddns.net"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        aboutButton.getStyleClass().add("settingsButton");
        Button helpButton = new Button("Help");
        helpButton.setOnAction(e -> {
            //TODO add user manual
        });
        helpButton.getStyleClass().add("settingsButton");
        JFXToggleButton toggleButton = new JFXToggleButton();
        toggleButton.setText("Enable Animations");
        toggleButton.setToggleColor(Color.GREEN);
        toggleButton.setUnToggleColor(Color.RED);
        toggleButton.setSelected(true);
        toggleButton.setStyle("-fx-text-fill: white;");
        toggleButton.setPadding(new Insets(10));
        Display.enableAnimations.bind(toggleButton.selectedProperty());
        Button animationsButton = new Button();
        animationsButton.setDisable(true);
        animationsButton.setGraphic(toggleButton);
        animationsButton.getStyleClass().add("settingsButton");
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("settingsButton");
        closeButton.setOnAction(e -> closeAnimation());
        VBox settingsButtonsPane = new VBox(changePasswordButton, aboutButton, helpButton, toggleButton, closeButton);
        settingsButtonsPane.setAlignment(Pos.TOP_CENTER);
        settingsButtonsPane.setSpacing(25);
        VBox settingsInnerPane = new VBox(settingsText, settingsButtonsPane);
        settingsInnerPane.getChildren().addAll();
        settingsInnerPane.setSpacing(25);
        settingsInnerPane.setPadding(new Insets(20));
        settingsInnerPane.setAlignment(Pos.CENTER);
        settingsInnerPane.setStyle("-fx-background-color: #660000;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;");
        settingsInnerPane.setMaxSize(450, 500);
        settingsInnerPane.setMinSize(450, 500);
        VBox settingsPane = new VBox(settingsInnerPane);
        setWidth(450);
        settingsPane.setAlignment(Pos.CENTER);
        getDialogPane().setContent(settingsPane);
    }

}
