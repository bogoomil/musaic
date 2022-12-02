package hu.boga.musaic.gui.track.panels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotesPanel extends ZoomablePanel {
    private static final Logger LOG = LoggerFactory.getLogger(NotesPanel.class);

    public NotesPanel(DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar) {
        super(zoom, scroll, resolution, fourthInBar);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LOG.debug("event x: {}, tick: {}", event.getX(), getTickAtX(event.getX()));
        });
    }

    @Override
    void updateGui() {
        paintNotes();
    }

    private void paintNotes() {
        getChildren().clear();
        double width = getFullWidth();
        setPrefWidth(width);
        Rectangle rectangle = new Rectangle(0, 0, width, 70);
        rectangle.setFill(null);
        rectangle.setStroke(Color.DARKORCHID);
        rectangle.setStrokeWidth(2);
        getChildren().add(rectangle);
    }

}
