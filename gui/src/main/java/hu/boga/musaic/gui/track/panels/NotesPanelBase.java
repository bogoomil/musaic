package hu.boga.musaic.gui.track.panels;

import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NotesPanelBase extends ZoomablePanel {
    private static final Logger LOG = LoggerFactory.getLogger(NotesPanelBase.class);

    public NotesPanelBase(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum, TrackModell trackModell) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum, trackModell);
    }

    @Override
    void updateGui() {
        paint();
    }

    abstract void paint();

    void createBorder() {
        getChildren().clear();
        double w = getFullWidth();
        setPrefWidth(w);
        createBorderRectangle(w);
    }

    private void createBorderRectangle(double width) {
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        rectangle.setFill(null);
        rectangle.setStroke(Color.DARKORCHID);
        rectangle.setStrokeWidth(2);
        getChildren().add(rectangle);
    }
}
