package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.Chord;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Comparator;

public class CursorLayer extends Pane implements Layer, EventHandler<MouseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(CursorLayer.class);
    public static final Color FILL_COLOR = Color.color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), 0.3);
    public static final Color STROKE_COLOR = Color.RED;

    LayeredPane parent;
    NoteLength currentNoteLength;
    ChordType currentChordType;

    public CursorLayer(LayeredPane parent, Observable<NoteLength> noteLengthObservable, Observable<ChordType> chordTypeObservable, DoubleProperty zoom) {
        this.parent = parent;
        noteLengthObservable.addPropertyChangeListener(this::noteLengthPropertyChange);
        chordTypeObservable.addPropertyChangeListener(this::chordTypePropertyChange);

        currentNoteLength = NoteLength.HARMICKETTED;
        currentChordType = ChordType.NONE;
        createRectangle(0);
        setVisible(false);
    }

    @Override
    public void updateGui() {
        getChildren().clear();
        if(currentChordType != ChordType.NONE){
            Chord chord = Chord.getChord(new Pitch(0), currentChordType);
            Arrays.stream(chord.getPitches()).sorted(Comparator.comparingInt(p -> p.getMidiCode())).forEach(pitch -> {
                createRectangle(pitch.getMidiCode());
            });
        } else {
            createRectangle(0);
        }
    }

    private void createRectangle(int midiCode) {
        Rectangle rectangle = new Rectangle();
        getChildren().add(rectangle);
        rectangle.setFill(FILL_COLOR);
        rectangle.setStroke(STROKE_COLOR);

        double width = calculateWidth(currentNoteLength);
        LOG.debug("cursor width: {}", width);
        rectangle.setWidth(width);
        rectangle.setHeight(GuiConstants.NOTE_LINE_HEIGHT);
        rectangle.setY(-midiCode * GuiConstants.NOTE_LINE_HEIGHT);
    }

    private void chordTypePropertyChange(PropertyChangeEvent event) {
        this.currentChordType = (ChordType) event.getNewValue();
        updateGui();
    }

    private void noteLengthPropertyChange(PropertyChangeEvent event) {
        this.currentNoteLength = (NoteLength) event.getNewValue();
        updateGui();
    }

    private double calculateWidth(NoteLength noteLength) {
        return noteLength.getErtek() * parent.get32ndsWidth();
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
            this.setVisible(false);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)){
            moveCursor(event);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)){
            parent.addNotesToTrack(parent.getTickAtX(getCaculatedX(event.getX())), parent.getPitchByY(event.getY()).getMidiCode());
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
