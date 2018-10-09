package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.svg.SVGGlyph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class SideTab extends Group {

    private StackPane textPane;
    private Rectangle rectClip;
    private HBox iconPane;
    private SVGGlyph graphic;
    private String text;
    private Pane content;

    public SideTab(String svg, int width, int height, String text, Pane content) {
        this.graphic = new SVGGlyph(0, text, svg, Color.WHITE);
        this.text = text;
        this.content = content;
        graphic.setSize(width, height);
        graphic.setScaleY(-1);
        iconPane = new HBox(graphic);
        iconPane.setAlignment(Pos.CENTER);
        int padding = (50 - width) / 2;
        iconPane.setPadding(new Insets(9, padding, 9, padding));
        iconPane.setPickOnBounds(true);
        Text tabHeaderText = new Text("  " + text);
        tabHeaderText.getStyleClass().add("side-tab-text");
        rectClip = new Rectangle();
        HBox textInnerPane = new HBox(tabHeaderText);
        textInnerPane.setAlignment(Pos.CENTER_LEFT);
        textPane = new StackPane(textInnerPane);
        textPane.setClip(rectClip);
        textPane.setPrefSize(160, 50);
        textPane.setPickOnBounds(false);
        rectClip.setHeight(50);
        getChildren().addAll(iconPane, textPane);
        iconPane.getStyleClass().add("side-tab");
        textPane.getStyleClass().add("side-tab");
        setOnMouseEntered(e -> {
            iconPane.getStyleClass().add("side-tab-hovered");
            textPane.getStyleClass().add("side-tab-hovered");
        });
        setOnMouseExited(e -> {
            iconPane.getStyleClass().removeAll("side-tab-hovered");
            textPane.getStyleClass().removeAll("side-tab-hovered");
        });
        rectClip.setWidth(5);
        rectClip.setTranslateX(155);
        textPane.setTranslateX(-160);
    }

    public SVGGlyph getGraphic() {
        return graphic;
    }

    public String getText() {
        return text;
    }

    public Pane getContent() {
        return content;
    }

    public void setExtended(Boolean extended) {
        if (extended) {

            rectClip.setWidth(160);
            rectClip.setTranslateX(0);
            textPane.setTranslateX(50);
        } else {
            rectClip.setWidth(5);
            rectClip.setTranslateX(155);
            textPane.setTranslateX(-155);
        }
    }

    public void setSelected(Boolean selected) {
        if (selected) {
            textPane.setStyle("-fx-border-color: #D2DB0E");
        } else {
            textPane.setStyle("-fx-border-color: transparent;");
        }
    }

}
