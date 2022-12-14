package hu.boga.musaic.gui.trackeditor.panels;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.trackeditor.NoteRectangle;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NotesLayer extends CursorLayer{

    private static final Logger LOG = LoggerFactory.getLogger(NotesLayer.class);

    final Group notesGroup = new Group();
    final EventBus eventBus;

    public NotesLayer(DoubleProperty zoom, IntegerProperty resolution, IntegerProperty fourthInBar, IntegerProperty measureNumProperty, TrackModell trackModell, IntegerProperty octaveNum, Observable<NoteLength> noteLengthObservable, Observable<ChordType> chordTypeObservable, EventBus eventBus) {
        super(zoom, resolution, fourthInBar, measureNumProperty, trackModell, octaveNum, noteLengthObservable, chordTypeObservable);
         getChildren().add(notesGroup);
        this.eventBus = eventBus;
        updateGui();
   }

    @Override
    protected void updateGui() {
        super.updateGui();
        notesGroup.getChildren().clear();
        trackModell.notes.forEach(noteModell -> {
            NoteRectangle noteRectangle = new NoteRectangle(noteModell, eventBus, Color.BLACK);
            noteRectangle.setX(getXByTick((int) noteModell.tick));
            noteRectangle.setY(getYByPitch(noteModell.midiCode));
            noteRectangle.setHeight(GuiConstants.NOTE_LINE_HEIGHT);
            noteRectangle.setWidth(getTickWidth() * noteModell.length);
            notesGroup.getChildren().add(noteRectangle);
        });
    }
}
