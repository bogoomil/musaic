package hu.boga.musaic.gui.track.panels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GridPanel extends ZoomablePanel {

    public GridPanel(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum, null);
        fourthInBar.addListener((observable, oldValue, newValue) -> {
            updateGui();
        });
    }

    @Override
    void updateGui() {
        paintGrid();
    }

    private void paintGrid() {
        getChildren().clear();
        setPrefWidth(measureWidth * measureNum * zoom.doubleValue());
        int x = 0;
        for(int i = 0; i < measureNum; i++){
            createMeasure(i);
        }
    }

    private void createMeasure(int i) {
        double measureEndX = 0;
        double barW = measureWidth * zoom.doubleValue();
        double rectW = barW / fourthInBar.intValue();
        for(int j = 0; j < fourthInBar.intValue(); j++){
            double rectX = i * barW + j * rectW;
            measureEndX = rectW + rectX;
            Rectangle rectangle = new Rectangle(rectX , 0, rectW, height);
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            getChildren().add(rectangle);
        }

        Line line = new Line(measureEndX, 0, measureEndX, height);
        line.setStroke(Color.RED);
        line.setStrokeWidth(1);
        getChildren().add(line);
    }

}
