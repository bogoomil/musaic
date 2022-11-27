package hu.boga.musaic.gui.sequencemanager.components.track;

import javafx.beans.property.DoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GridPainter {
    private int resolution;
    private int forthInMeasure;
    private int measureNum;

    private DoubleProperty zoomFactor;
    private DoubleProperty scrollFactor;

    private Canvas canvas;

    public GridPainter(int resolution, int forthInMeasure, int measureNum, DoubleProperty zoomFactor, DoubleProperty scrollFactor) {
        this.resolution = resolution;
        this.forthInMeasure = forthInMeasure;
        this.measureNum = measureNum;
        this.zoomFactor = zoomFactor;

        zoomFactor.addListener((observable, oldValue, newValue) -> paintGrid());

        this.scrollFactor = scrollFactor;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void paintGrid() {
        System.out.println("painting grid, zoom: " + zoomFactor.intValue());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

        canvas.setWidth(zoomFactor.doubleValue() * measureNum * 32);
        canvas.setHeight(100);
        graphicsContext.setStroke(Color.GREEN);
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.setLineWidth(0.5);
        for(int x = 0; x < get32ndsCount() * zoomFactor.intValue(); x+= zoomFactor.intValue()){
            System.out.println("height: " + canvas.getHeight());
            graphicsContext.strokeLine(x, 0, x, canvas.getHeight());
        }
    }

    private int get32ndsCount(){
        return measureNum * 32;
    }

}
