package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.all.Result;

public class ResultComponent extends HBox {

    private Result result;
    private WritableValue<Double> firstPaneWidth;

    public ResultComponent(Result result) {
        this.result = result;
        init();
    }

    private void init() {
        Text firstText = new Text(result.getResultName());
        firstText.getStyleClass().add("result-text");
        HBox firstPane = new HBox(firstText);
        if (result.getResultName().equals("Due Performance") || result.getResultName().equals("Final Mark")) {
            firstPane.getStyleClass().add("result-slide-pane");
        } else {
            firstPane.getStyleClass().add("result-first-slide-pane");
        }
        firstPane.setMaxSize(650, 30);
        firstPane.setMinSize(650, 30);
        firstPaneWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return firstPane.getWidth();
            }

            @Override
            public void setValue(Double value) {
                firstPane.setMaxWidth(value);
                firstPane.setMinWidth(value);
            }
        };
        Text secondText = new Text();
        secondText.getStyleClass().add("result-text");
        if (result.getResultName().equals("Result Name")) {
            secondText.setText("Result");
        } else if (result.getResult() == -1D || result.getResult() == -0D) {
            secondText.setText("N/A");
        } else {
            secondText.setText(String.format("%3.0f%s", (result.getResult() * 100) / result.getResultMax(), "%"));
        }
        HBox secondPane = new HBox(secondText);
        secondPane.getStyleClass().add("result-slide-pane");
        secondPane.setMaxSize(150, 30);
        secondPane.setMinSize(150, 30);
        HBox topPane = new HBox(firstPane, secondPane);
        topPane.getStyleClass().add("result-main-pain");

        Pane fillerPane = new Pane();
        fillerPane.setMinWidth(500);
        Text thirdText = new Text();
        thirdText.getStyleClass().add("result-text");
        if (result.getResultName().equals("Result Name")) {
            thirdText.setText("Due Performance");
        } else if (result.getDpWeight() != 0D) {
            if (result.getResult() == -1D) {
                thirdText.setText(String.format("N/A (%2d)", (int) result.getDpWeight()));
            } else {
                thirdText.setText(String.format("%3.0f (%2d)", result.getResult() * result.getDpWeight() / 100, (int) result.getDpWeight()));
            }
        }
        HBox thirdPane = new HBox(thirdText);
        thirdPane.getStyleClass().add("result-slide-pane");
        thirdPane.setMaxSize(150, 30);
        thirdPane.setMinSize(150, 30);
        Text fourthText = new Text();
        fourthText.getStyleClass().add("result-text");
        if (result.getResultName().equals("Result Name")) {
            fourthText.setText("Final Mark");
        } else if (result.getFinalWeight() != 0D) {
            if (result.getResult() == -1D) {
                fourthText.setText(String.format("N/A (%2d)", (int) result.getFinalWeight()));
            } else {
                fourthText.setText(String.format("%3.0f (%2d)", result.getResult() * result.getFinalWeight() / 100, (int) result.getFinalWeight()));
            }
        }
        HBox fourthPane = new HBox(fourthText);
        fourthPane.getStyleClass().add("result-slide-pane");
        fourthPane.setMaxSize(150, 30);
        fourthPane.setMinSize(150, 30);
        HBox backPane = new HBox(fillerPane, thirdPane, fourthPane);
        backPane.getStyleClass().add("result-main-pain");

        StackPane resultPane = new StackPane(backPane, topPane);
        getStyleClass().add("result-component");
        getChildren().addAll(resultPane);
    }

    public void setExtended(Boolean extended) {
        if (extended) {
            if (Display.enableAnimations.get()) {
                KeyValue keyValue = new KeyValue(firstPaneWidth, 650D);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValue);
                Timeline timeline = new Timeline(keyFrame);
                timeline.play();
            } else {
                firstPaneWidth.setValue(650D);
            }
        } else {
            if (Display.enableAnimations.get()) {
                KeyValue keyValue;
                switch (result.getResultName()) {
                    case "Due Performance":
                        keyValue = new KeyValue(firstPaneWidth, 500D);
                        break;
                    case "Final Mark":
                        keyValue = new KeyValue(firstPaneWidth, 650D);
                        break;
                    default:
                        keyValue = new KeyValue(firstPaneWidth, 350D);
                        break;
                }
                KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValue);
                Timeline timeline = new Timeline(keyFrame);
                timeline.play();
            } else {
                switch (result.getResultName()) {
                    case "Due Performance":
                        firstPaneWidth.setValue(500D);
                        break;
                    case "Final Mark":
                        firstPaneWidth.setValue(650D);
                        break;
                    default:
                        firstPaneWidth.setValue(350D);
                        break;
                }
            }
        }
    }

}
