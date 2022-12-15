package hu.boga.musaic.gui.trackeditor.panels;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.TrackService;
import hu.boga.musaic.gui.trackeditor.NoteModell;
import hu.boga.musaic.gui.trackeditor.NoteRectangle;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;

public final class NotesLayer extends CursorLayer {

    private static final Logger LOG = LoggerFactory.getLogger(NotesLayer.class);

    final Group notesGroup = new Group();
    final EventBus eventBus;
    final Observable<TrackModell> trackModellObservable;
    private TrackModell trackModell;
    private final TrackService trackService;
    private NoteLength currentLength = NoteLength.HARMICKETTED;
    private ChordType currentChordType = ChordType.NONE;

    public NotesLayer(DoubleProperty zoom,
                      IntegerProperty resolution,
                      IntegerProperty fourthInBar,
                      IntegerProperty measureNumProperty,
                      Observable<TrackModell> trackModellObservable,
                      IntegerProperty octaveNum,
                      Observable<NoteLength> noteLengthObservable,
                      Observable<ChordType> chordTypeObservable,
                      EventBus eventBus, TrackService service) {
        super(zoom, resolution, fourthInBar, measureNumProperty, octaveNum, noteLengthObservable, chordTypeObservable);
        this.getChildren().add(notesGroup);
        this.eventBus = eventBus;
        this.trackModellObservable = trackModellObservable;
        this.trackModellObservable.addPropertyChangeListener(propertyChangeEvent -> trackModellChanged(propertyChangeEvent));
        this.trackService = service;
        noteLengthObservable.addPropertyChangeListener(propertyChangeEvent -> noteLengthChanged(propertyChangeEvent));
        chordTypeObservable.addPropertyChangeListener(propertyChangeEvent -> chordTypeChanged(propertyChangeEvent));
        this.setOnMouseClicked(event -> mouseClicked(event));
    }

    private void chordTypeChanged(PropertyChangeEvent propertyChangeEvent) {
        this.currentChordType = (ChordType) propertyChangeEvent.getNewValue();
    }

    private void noteLengthChanged(PropertyChangeEvent propertyChangeEvent) {
        this.currentLength = (NoteLength) propertyChangeEvent.getNewValue();
    }

    private void mouseClicked(MouseEvent event) {
        trackService.addChord(trackModell.id, getTickAtX(getCaculatedX(event.getX())), getPitchByY(event.getY()).getMidiCode(), currentLength.getErtek(), currentChordType);
    }

    @Override
    protected void updateGui() {
        super.updateGui();
        notesGroup.getChildren().clear();
        if (trackModell != null) {
            trackModell.notes.forEach(noteModell -> {
                createNoteRectangle(noteModell);
            });
        }
    }

    private void createNoteRectangle(NoteModell noteModell) {
        NoteRectangle noteRectangle = new NoteRectangle(noteModell, eventBus, Color.BLACK);
        noteRectangle.setX(getXByTick((int) noteModell.tick));
        noteRectangle.setY(getYByPitch(noteModell.midiCode));
        noteRectangle.setHeight(GuiConstants.NOTE_LINE_HEIGHT);
        noteRectangle.setWidth(getTickWidth() * noteModell.length);
        notesGroup.getChildren().add(noteRectangle);
    }

    private void trackModellChanged(PropertyChangeEvent propertyChangeEvent) {
        trackModell = (TrackModell) propertyChangeEvent.getNewValue();
        updateGui();
    }

}
