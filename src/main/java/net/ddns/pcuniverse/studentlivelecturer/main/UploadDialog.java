package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class UploadDialog extends CustomDialogSkin {

    public UploadDialog(Window parent) {
        initOwner(parent);
        Text settingsText = new Text("Uploading file..");
        settingsText.getStyleClass().add("heading-text");
        Text text = new Text("Please be patient..");
        ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setMinSize(200, 200);
        progressIndicator.setMaxSize(200, 200);
        progressIndicator.setStyle("-fx-progress-color: #ffb81c");
        VBox settingsInnerPane = new VBox(settingsText, text, progressIndicator);
        settingsInnerPane.getChildren().addAll();
        settingsInnerPane.setSpacing(25);
        settingsInnerPane.setPadding(new Insets(20));
        settingsInnerPane.setAlignment(Pos.TOP_CENTER);
        settingsInnerPane.setStyle("-fx-background-color: #007FA3;" +
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
