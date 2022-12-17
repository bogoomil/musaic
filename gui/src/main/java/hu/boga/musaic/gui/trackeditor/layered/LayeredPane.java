package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.panels.ZoomablePanel;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.trackeditor.TrackEditorPresenterImpl;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class LayeredPane extends ZoomablePanel implements NoteChangedListener {
    public final IntegerProperty octaveNum;
    private final List<Layer> layers = new ArrayList<>();
    private final NotesLayer notesLayer;
    private final GridLayer gridLayer;
    private CursorLayer cursor;
    private final TrackEditorPresenterImpl presenter;

    public LayeredPane(TrackEditorPresenterImpl presenter,
                       DoubleProperty zoom,
                       IntegerProperty resolution,
                       IntegerProperty fourthInBar,
                       IntegerProperty measureNumProperty,
                       IntegerProperty octaveNum,
                       Observable<NoteLength> noteLengthObservable,
                       Observable<ChordType> chordTypeObservable,
                       Observable<TrackModell> trackModellObservable) {
        super(zoom, resolution, fourthInBar, measureNumProperty);
        this.octaveNum = octaveNum;
        this.presenter = presenter;

        gridLayer = new GridLayer(this);
        cursor = new CursorLayer(this, noteLengthObservable, chordTypeObservable, zoom);
        notesLayer = new NotesLayer(this, trackModellObservable, zoom);

        initLayers();
        updateGui();
    }

    private void initCursor() {
        layers.add(cursor);
        this.getChildren().add(cursor);
        this.addEventHandler(MouseEvent.ANY, cursor);
    }

    private void initLayers() {
        initGridLayer();
        initCursor();
        initNotesLayer();
    }

    private void initNotesLayer() {
        layers.add(notesLayer);
        this.getChildren().add(notesLayer);
    }

    private void initGridLayer() {
        layers.add(gridLayer);
        this.getChildren().add(gridLayer);
    }

    @Override
    protected void updateGui() {
        this.layers.forEach(layer -> layer.updateGui());
    }

    protected double getFullHeight() {
        return GuiConstants.NOTE_LINE_HEIGHT * octaveNum.intValue() * 12;
    }

    protected Pitch getPitchByY(final double y) {
        final int pitch = (int) ((getFullHeight() / GuiConstants.NOTE_LINE_HEIGHT) - (y / GuiConstants.NOTE_LINE_HEIGHT));
        return new Pitch(pitch);
    }

    protected double get32ndsWidth() {
        return measureWidth * this.zoom.doubleValue() / get32ndsCountInBar();
    }

    protected int get32ndsCountInBar() {
        return fourthInBar.intValue() * 8;
    }

    protected int getYByPitch(final int midiCode) {
        return (octaveNum.intValue() * 12 - 1 - midiCode) * GuiConstants.NOTE_LINE_HEIGHT;
    }

    protected double getTickWidth() {
        return measureWidth * zoom.doubleValue() / (this.resolution.intValue() * fourthInBar.intValue());
    }

    public void addNotesToTrack(int tick, int pitch){
        presenter.addNotesToTrack(tick, pitch);
    }

    @Override
    public void volumeChanged(String noteId, double newVolume) {
        presenter.noteVolumeChanged(noteId, newVolume);
    }

    @Override
    public void noteDeleted(String noteId) {
        presenter.deleteNote(noteId);

    }
}
