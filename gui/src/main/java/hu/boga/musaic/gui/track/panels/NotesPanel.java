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

public class NotesPanel extends NotesPanelBase {
    private static final Logger LOG = LoggerFactory.getLogger(NotesPanel.class);

    public NotesPanel(DoubleProperty zoom, DoubleProperty scroll, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNum, TrackModell trackModell) {
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

    void paintNotes() {
        createBorder();
        List<NoteModell> notes = trackModell.getNotesNormalized();
        if (!notes.isEmpty()){
            int high = notes.get(notes.size() - 1).midiCode;
            if(high == 0){
                high = 1;
            }
            double pitchHeight = (GridPanel.HEIGHT - 20) / high;
            double tickWidth = getTickWith();
            notes.forEach(noteModell -> {
                double length = noteModell.length * tickWidth;
                double y = GridPanel.HEIGHT - (pitchHeight * noteModell.midiCode) - 10;
                double x = getXByTick((int) noteModell.tick);
                Rectangle r = new Rectangle(x, y, length, 3);
                getChildren().add(r);
            });
        }
    }

}
