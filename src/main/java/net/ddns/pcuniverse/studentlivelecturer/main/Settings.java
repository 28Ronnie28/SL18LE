package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Settings extends Application {

    private Stage parent;

    public Settings(Stage parent) {
        this.parent = parent;
        try {
            start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Settings");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        stage.setResizable(true);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);

        //Setup content pane
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent;" +
                " -fx-font-size: 20;");
        closeButton.setOnAction(e -> stage.close());
        HBox closePane = new HBox(closeButton);
        closePane.setAlignment(Pos.CENTER_RIGHT);
        closePane.setPadding(new Insets(0, 0, -25, 0));
        Text headingText = new Text("     Settings     ");
        headingText.setStyle("-fx-font-size: 32pt;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setMinSize(250, 50);
        changePasswordButton.getStyleClass().add("settingsButton");
        Button aboutButton = new Button("About");
        aboutButton.setMinSize(250, 50);
        aboutButton.getStyleClass().add("settingsButton");
        Button logoutButton = new Button("Log Out");
        logoutButton.setMinSize(250, 50);
        logoutButton.getStyleClass().add("settingsButton");
        VBox settingsPane = new VBox(changePasswordButton, aboutButton, logoutButton);
        settingsPane.setAlignment(Pos.TOP_CENTER);
        settingsPane.setSpacing(5);
        VBox contentPane = new VBox(closePane, headingText, settingsPane);
        contentPane.getChildren().addAll();
        contentPane.setSpacing(15);
        contentPane.setPadding(new Insets(0, 0, 15, 0));
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setStyle("-fx-background-color: rgba(0,135,167,0.8);" +
                " -fx-background-radius: 15");

        //Setup scene
        Scene scene = new Scene(contentPane, Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("StudentLiveStyle.css").toExternalForm());

        //Select and display scene
        stage.setScene(scene);
        stage.showAndWait();
    }

}
