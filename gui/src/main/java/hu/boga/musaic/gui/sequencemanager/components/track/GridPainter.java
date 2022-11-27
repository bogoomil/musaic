package hu.boga.musaic.gui.sequencemanager.components.track;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GridPainter {
    private int resolution;
    private int fourthsInMeasure;
    private int measureNum;

    private DoubleProperty zoomFactor;
    private DoubleProperty scrollFactor;

    private Pane pane;
    private double origHeight;

    private double measureWidth = 20;

    public GridPainter(int resolution, int fourthsInMeasure, int measureNum, DoubleProperty zoomFactor, DoubleProperty scrollFactor) {
        this.resolution = resolution;
        this.fourthsInMeasure = fourthsInMeasure;
        this.measureNum = measureNum;
        this.zoomFactor = zoomFactor;
        zoomFactor.addListener((observable, oldValue, newValue) -> paintGrid());
        this.scrollFactor = scrollFactor;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
        origHeight = pane.getHeight();
        paintGrid();
    }

    public void paintGrid() {
        pane.getChildren().clear();
        System.out.println("painting grid, zoom: " + zoomFactor.intValue());
        int x = 0;
        for(int i = 0; i < measureNum; i++){
            createMeasure(i);
        }
    }

    private void createMeasure(int i) {
        double measureEndX = 0;
        for(int j = 0; j < fourthsInMeasure; j++){
            double barW = measureWidth * zoomFactor.doubleValue();
            double rectW = barW / fourthsInMeasure;
            double rectX = i * barW + j * rectW;
            measureEndX = rectW + rectX;

            System.out.println("painting rects: " + rectX + ", " + rectW);

            Rectangle rectangle = new Rectangle(rectX , 0, rectW, pane.getHeight() - 2  );
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            pane.getChildren().add(rectangle);
        }
        Line line = new Line(measureEndX, 0, measureEndX, pane.getHeight() - 2);
        line.setStroke(Color.RED);
        line.setStrokeWidth(1);

        pane.getChildren().add(line);
    }
}
