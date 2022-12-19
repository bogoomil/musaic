package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.Chord;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Comparator;

public class SelectionLayer extends Group implements EventHandler<MouseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(SelectionLayer.class);
    public static final Color STROKE_COLOR = Color.RED;

    LayeredPane parent;
    Rectangle selection = new Rectangle();

    Point2D startPoint;
    Point2D endPoint;

    public SelectionLayer(LayeredPane parent) {
        this.parent = parent;
        selection = new Rectangle();
        this.getChildren().add(selection);
        selection.setStroke(STROKE_COLOR);
        selection.setFill(null);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
            startPoint = new Point2D(event.getX(), event.getY());
            endPoint = new Point2D(event.getX(), event.getY());
            initSelection(event);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)){
            resetSelection();
            parent.selectNotes(startPoint, endPoint);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)){
            endPoint = new Point2D(event.getX(), event.getY());
            updateSelectionRectangel();
        }
    }

    private void initSelection(MouseEvent event) {
        selection.setVisible(true);
        selection.setX(event.getX());
        selection.setY(event.getY());
    }

    private void resetSelection() {
        selection.setVisible(false);
        selection.setWidth(0);
        selection.setHeight(0);
    }

    private void updateSelectionRectangel() {
        double width = endPoint.getX() - startPoint.getX();
        double height = endPoint.getY() - startPoint.getY();
        selection.setWidth(width);
        selection.setHeight(height);
    }

}
