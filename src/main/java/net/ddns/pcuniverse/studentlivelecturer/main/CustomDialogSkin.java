package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CustomDialogSkin extends Dialog<Integer> {

    private int result;
    private int width;

    public CustomDialogSkin() {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
        getDialogPane().setStyle("-fx-background-color: transparent");
        getDialogPane().getScene().setFill(Color.TRANSPARENT);
        ((Stage)getDialogPane().getScene().getWindow()).initStyle(StageStyle.TRANSPARENT);
    }

    public void setCustomResult(int result) {
        this.result = result;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int showDialog() {
        setX((getOwner().getWidth() / 2) - (width / 2));
        setY((getOwner().getHeight() / 2) - (getHeight() / 2));
        if (Display.enableAnimations.get()) {
            openAnimation();
        }
        showAndWait();
        return result;
    }

    public void openAnimation() {
        getDialogPane().setScaleX(0);
        getDialogPane().setScaleY(0);
        getDialogPane().setOpacity(0);
        KeyValue scaleXValue = new KeyValue(getDialogPane().scaleXProperty(), 1);
        KeyValue scaleYValue = new KeyValue(getDialogPane().scaleYProperty(), 1);
        KeyValue opacityValue = new KeyValue(getDialogPane().opacityProperty(), 1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), scaleXValue, scaleYValue, opacityValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    public void closeAnimation() {
        KeyValue scaleXValue = new KeyValue(getDialogPane().scaleXProperty(), 0);
        KeyValue scaleYValue = new KeyValue(getDialogPane().scaleYProperty(), 0);
        KeyValue opacityValue = new KeyValue(getDialogPane().opacityProperty(), 0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), scaleXValue, scaleYValue, opacityValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
        timeline.setOnFinished((ActionEvent e) -> setResult(0));
    }

}
