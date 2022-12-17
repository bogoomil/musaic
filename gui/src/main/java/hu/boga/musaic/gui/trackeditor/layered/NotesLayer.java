package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.trackeditor.NoteRectangle;
import hu.boga.musaic.musictheory.Chord;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Comparator;

public class NotesLayer extends Group implements Layer, EventHandler<MouseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(NotesLayer.class);

    LayeredPane parent;
    final Observable<TrackModell> trackModellObservable;
    TrackModell trackModell;

    public NotesLayer(LayeredPane parent, Observable<TrackModell> trackModellObservable, DoubleProperty zoom) {
        this.parent = parent;
        this.trackModellObservable = trackModellObservable;
        this.trackModellObservable.addPropertyChangeListener(propertyChangeEvent -> trackModellChanged(propertyChangeEvent));
    }

    private void trackModellChanged(PropertyChangeEvent propertyChangeEvent) {
        this.trackModell = (TrackModell) propertyChangeEvent.getNewValue();
        updateGui();
    }

    @Override
    public void updateGui() {
        getChildren().clear();
        if(trackModell != null){
            trackModell.notes.forEach(noteModell -> {
                NoteRectangle noteRectangle = new NoteRectangle(noteModell, parent);
                noteRectangle.setWidth(parent.getTickWidth() * noteModell.length);
                noteRectangle.setHeight(GuiConstants.NOTE_LINE_HEIGHT);
                noteRectangle.setLayoutX(parent.getXByTick((int) noteModell.tick));
                noteRectangle.setLayoutY(parent.getYByPitch(noteModell.midiCode));
                getChildren().add(noteRectangle);
            });
        }
    }

    private double calculateWidth(Long noteLength) {
        return noteLength * parent.get32ndsWidth();
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
            this.setVisible(false);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)){
            moveCursor(event);
        }
    }

    private void moveCursor(MouseEvent event) {
        this.setVisible(true);
        setLayoutX(getCaculatedX(event.getX()));
        setLayoutY(parent.getYByPitch(parent.getPitchByY((int) event.getY()).getMidiCode()));
    }

    private double getCaculatedX(double x) {
        return (x - (x % parent.get32ndsWidth()));
    }

}
