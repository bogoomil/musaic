package hu.boga.musaic.gui.sequencemanager.components.track;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GridPainter {
    private int fourthsInMeasure;
    private int measureNum;

    private DoubleProperty zoomFactor;
    private DoubleProperty scrollFactor;

    private Pane pane;

    private double measureWidth = 10;

    public GridPainter(int fourthsInMeasure, int measureNum, DoubleProperty zoomFactor, DoubleProperty scrollFactor) {
        this.fourthsInMeasure = fourthsInMeasure;
        this.measureNum = measureNum;
        this.zoomFactor = zoomFactor;
        this.scrollFactor = scrollFactor;
        zoomFactor.addListener((observable, oldValue, newValue) -> paintGrid());
        scrollFactor.addListener((observable, oldValue, newValue) -> scrollGrid(newValue));
    }

    private void scrollGrid(Number newValue) {
        Group group = (Group) pane.getParent();
        double v = pane.getPrefWidth() * (newValue.doubleValue() / 100);
        group.toBack();
        group.setLayoutX(-1 * v);
    }

    public void setPane(Pane pane) {
        this.pane = pane;
        paintGrid();
    }

    public void paintGrid() {
        pane.getChildren().clear();
        pane.setPrefWidth(measureWidth * measureNum * zoomFactor.doubleValue());
        int x = 0;
        for(int i = 0; i < measureNum; i++){
            createMeasure(i);
        }
        scrollGrid(scrollFactor.doubleValue());
    }

    private void createMeasure(int i) {
        double measureEndX = 0;
        for(int j = 0; j < fourthsInMeasure; j++){
            double barW = measureWidth * zoomFactor.doubleValue();
            double rectW = barW / fourthsInMeasure;
            double rectX = i * barW + j * rectW;
            measureEndX = rectW + rectX;
            Rectangle rectangle = new Rectangle(rectX , 0, rectW, 80  );
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            pane.getChildren().add(rectangle);
        }
        Line line = new Line(measureEndX, 0, measureEndX, 80);
        line.setStroke(Color.RED);
        line.setStrokeWidth(1);

        pane.getChildren().add(line);
    }
}
