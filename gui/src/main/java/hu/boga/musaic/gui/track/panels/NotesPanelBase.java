package hu.boga.musaic.gui.track.panels;

import hu.boga.musaic.gui.note.NoteModell;
import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class NotesPanelBase extends ZoomablePanel {
    private static final Logger LOG = LoggerFactory.getLogger(NotesPanelBase.class);

    public NotesPanelBase(DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum, TrackModell trackModell) {
        super(zoom, scroll, resolution, fourthInBar, measureNum, trackModell);
        LOG.debug("track modell: {}", trackModell.id);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LOG.debug("event x: {}, tick: {}", event.getX(), getTickAtX(event.getX()));
        });
    }

    @Override
    void updateGui() {
        paintNotes();
    }

    abstract void paintNotes();

    void createBorder() {
        getChildren().clear();
        double w = getFullWidth();
        setPrefWidth(w);
        createBorderRectangle(w);
    }

    private void createBorderRectangle(double width) {
        Rectangle rectangle = new Rectangle(0, 0, width, GridPanel.HEIGHT);
        rectangle.setFill(null);
        rectangle.setStroke(Color.DARKORCHID);
        rectangle.setStrokeWidth(2);
        getChildren().add(rectangle);
    }
}
