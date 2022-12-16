package hu.boga.musaic.gui.track.panels;

import hu.boga.musaic.gui.trackeditor.NoteModell;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.logic.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.List;

public final class NotesPanel extends NotesPanelBase {
    private static final Logger LOG = LoggerFactory.getLogger(NotesPanel.class);
    private final Observable<TrackModell> trackModellObservable;
    private TrackModell trackModell;

    public NotesPanel(int height, DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum, Observable<TrackModell> trackModellObservable) {
        super(height, zoom, scroll, resolution, fourthInBar, measureNum);
        this.trackModellObservable = trackModellObservable;
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LOG.debug("event x: {}, tick: {}", event.getX(), getTickAtX(event.getX()));
        });
        this.trackModellObservable.addPropertyChangeListener(propertyChangeEvent -> trackModellChanged(propertyChangeEvent));
    }

    private void trackModellChanged(PropertyChangeEvent propertyChangeEvent) {
        trackModell = (TrackModell) propertyChangeEvent.getNewValue();
        updateGui();
    }

    @Override
    protected void updateGui() {
        getChildren().clear();
        createBorder();
        if(trackModell != null){
            List<NoteModell> notes = trackModell.getNotesNormalized();
            if (!notes.isEmpty()) {
                int high = notes.get(notes.size() - 1).midiCode;
                if (high == 0) {
                    high = 1;
                }
                double pitchHeight = (height - 20) / high;
                double tickWidth = getTickWith();
                notes.forEach(noteModell -> {
                    double length = noteModell.length * tickWidth;
                    double y = height - (pitchHeight * noteModell.midiCode) - 10;
                    double x = getXByTick((int) noteModell.tick);
                    Rectangle r = new Rectangle(x, y, length, 3);
                    getChildren().add(r);
                });
            }
        }
    }

}
