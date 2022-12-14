package hu.boga.musaic.gui.track.panels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NotesPanelBase extends ScrollablePanel {
    private static final Logger LOG = LoggerFactory.getLogger(NotesPanelBase.class);

    public NotesPanelBase(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum);
    }

    @Override
    protected abstract void updateGui();

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
