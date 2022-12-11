package hu.boga.musaic.gui.trackeditor.panels;

import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GridPanel extends EditorBasePanel {

    private static final Logger LOG = LoggerFactory.getLogger(GridPanel.class);

    Group shapes = new Group();

    public GridPanel(DoubleProperty zoom, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty, TrackModell trackModell, IntegerProperty octaveNum) {
        super(zoom, resolution, fourthInBar, measureNumProperty, trackModell, octaveNum);
        getChildren().add(shapes);
        updateGui();
    }

    @Override
    protected void updateGui() {
        super.updateGui();
        shapes.getChildren().clear();
        createMeasures();
        createVerticalLines();
        createHorizontalLines();
        createTexts();
    }

    private void createTexts() {
    }

    private void createHorizontalLines() {
    }

    private void createVerticalLines() {
        int countOf32ndsInBar = this.get32ndsCountInBar();
        int countOf32nds = countOf32ndsInBar * measureNum.intValue();
        final double w32nds = this.get32ndsWidth();
        double x = 0;
        for (int i = 0; i < countOf32nds; i++) {
            final Line line = new Line();
            line.setStartX(x);
            line.setStartY(0);
            line.setEndX(x);
            line.setEndY(getFullHeight());
            if (i % countOf32ndsInBar == 0) {
                line.setStrokeWidth(2);
                line.setStroke(Color.BLACK);
            } else {
                line.setStroke(Color.RED);
            }
            shapes.getChildren().addAll(line);
            x += w32nds;
        }
    }

    private void createMeasures() {
        for (int i = 0; i < measureNum.intValue(); i++) {
            createMeasure(i);
        }
    }

    private void createMeasure(int i) {
//        double measureEndX = 0;
        double height = getFullHeight();
        double barW = measureWidth * zoom.doubleValue();
        double rectW = barW / fourthInBar.intValue();
        for (int j = 0; j < fourthInBar.intValue(); j++) {
            double rectX = i * barW + j * rectW;
//            measureEndX = rectW + rectX;
            Rectangle rectangle = new Rectangle(rectX, 0, rectW, height);
            rectangle.setFill(j % 2 == 0 ? Color.LIGHTSKYBLUE : Color.DEEPSKYBLUE);
            shapes.getChildren().add(rectangle);
        }
//        Line line = new Line(measureEndX, 0, measureEndX, height);
//        line.setStroke(Color.RED);
//        line.setStrokeWidth(1);
//        shapes.getChildren().add(line);
    }


}
