package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import models.all.Result;

import java.util.List;

public class ResultPane extends VBox {

    private List<Result> results;
    private String moduleName;
    private Boolean extended;

    public ResultPane(String moduleName, List<Result> results) {
        this.moduleName = moduleName;
        this.results = results;
        extended = true;
        init();
    }

    private void init() {
        Text headingText = new Text(moduleName);
        headingText.getStyleClass().add("result-heading-text");
        HBox.setHgrow(headingText, Priority.ALWAYS);
        HBox headingTextPane = new HBox(headingText);
        headingTextPane.setAlignment(Pos.CENTER);
        JFXButton actionButton = new JFXButton("More");
        actionButton.setRipplerFill(Color.WHITE);
        actionButton.setStyle("-fx-text-fill: white;");
        actionButton.setFocusTraversable(false);
        actionButton.setOnAction(e -> {
            if (actionButton.getText().equals("More")) {
                actionButton.setText("Less");
                setExtended(false);
            } else {
                actionButton.setText("More");
                setExtended(true);
            }
        });
        Pane fillerPane = new Pane();
        HBox.setHgrow(fillerPane, Priority.ALWAYS);
        HBox actionButtonPane = new HBox(fillerPane, actionButton);
        StackPane headingPane = new StackPane(headingTextPane, actionButtonPane);
        headingPane.getStyleClass().add("result-slide-pane");
        headingPane.getStyleClass().add("result-heading-slide-pane");
        headingPane.setAlignment(Pos.CENTER);
        getChildren().add(headingPane);

        getChildren().add(new ResultComponent(new Result(0, "", "Result Name", 0D, 0D, 0D, -2D)));

        Double dp = 0D;
        Double fm = 0D;
        for (Result result : results) {
            if (result.getDpWeight() != 0D) {
                dp += (result.getResult() * result.getDpWeight() / 100D);
                fm += (result.getResult() * result.getFinalWeight() / 100D);
                getChildren().add(new ResultComponent(result));
            }
        }

        if (dp != 0D && dp != -1D) {
            Result dpResult = new Result(0, "", "Due Performance", dp, 100D, 0D, 0D);
            getChildren().add(new ResultComponent(dpResult));
        }

        //Check supplementary results
        //Check which is higher

        double examMark = 0;
        double suppExamMark = 0;

        for (Result result : results) {
            if (result.getResultName().equals("Initial Exam")) {
                examMark = result.getResult();
            } else if (result.getResultName().equals("Supplementary Exam")) {
                suppExamMark = result.getResult();
            }
        }

        //TODO currently only works if result is out of 100

        for (Result result : results) {
            if (result.getDpWeight() == 0D) {
                if (result.getResultName().equals("Initial Exam")) {
                    if (examMark >= suppExamMark) {
                        fm += (result.getResult() * result.getFinalWeight() / 100D);
                    }
                } else if (result.getResultName().equals("Supplementary Exam")) {
                    if (suppExamMark > examMark) {
                        fm += (result.getResult() * result.getFinalWeight() / 100D);
                    }
                } else {
                    fm += (result.getResult() * result.getFinalWeight() / 100D);
                }

                getChildren().add(new ResultComponent(result));
            }
        }

        Result fmResult;
        if (results.isEmpty()) {
            fmResult = new Result(0, "", "Final Mark", -1D, 100D, 0D, 0D);
        } else {
            fmResult = new Result(0, "", "Final Mark", fm, 100D, 0D, 0D);
        }
        getChildren().add(new ResultComponent(fmResult));
        setMaxWidth(800);
        setMinWidth(800);
        setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-insets: 0 -4 0 0");
    }

    public void setExtended(Boolean extended) {
        if ((this.extended && !extended) || (!this.extended && extended)) {
            for (Node node : getChildren()) {
                if (node instanceof ResultComponent) {
                    ((ResultComponent) node).setExtended(extended);
                }
            }
        }
        this.extended = extended;
    }

    public Boolean getExtended() {
        return extended;
    }

}
