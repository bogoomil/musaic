package hu.boga.musaic.gui.trackeditor.layered;

import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.panels.ZoomablePanel;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.trackeditor.NoteModell;
import hu.boga.musaic.gui.trackeditor.TrackEditorPresenterImpl;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LayeredPane extends ZoomablePanel implements NoteChangedListener {

    private static final Logger LOG = LoggerFactory.getLogger(LayeredPane.class);

    public final IntegerProperty octaveNum;
    private final List<Layer> layers = new ArrayList<>();
    private final NotesLayer notesLayer;
    private final GridLayer gridLayer;
    private final TrackEditorPresenterImpl presenter;

    private CursorLayer cursorLayer;
    private MaskLayer maskLayer;
    private SelectionLayer selectionLayer;

    public LayeredPane(TrackEditorPresenterImpl presenter,
                       DoubleProperty zoom,
                       IntegerProperty resolution,
                       IntegerProperty fourthInBar,
                       IntegerProperty measureNumProperty,
                       IntegerProperty octaveNum,
                       Observable<NoteLength> noteLengthObservable,
                       Observable<ChordType> chordTypeObservable,
                       Observable<TrackModell> trackModellObservable,
                       Observable<NoteName> rootObservable,
                       Observable<Tone> modeObservable) {
        super(zoom, resolution, fourthInBar, measureNumProperty);
        this.octaveNum = octaveNum;
        this.presenter = presenter;

        gridLayer = new GridLayer(this);
        cursorLayer = new CursorLayer(this, noteLengthObservable, chordTypeObservable, zoom);
        notesLayer = new NotesLayer(this, trackModellObservable, zoom);
        maskLayer = new MaskLayer(this, rootObservable, modeObservable);
        selectionLayer = new SelectionLayer(this);

        this.setOnMouseMoved(event -> this.requestFocus());
        this.setOnKeyPressed(this::keyPressed);

        initLayers();
        updateGui();
    }

    private void mouseMoved(MouseEvent event) {
        this.requestFocus();
    }

    private void keyPressed(KeyEvent event) {
        if(event.isControlDown()){
            if(event.getCode() == KeyCode.A){
                Point2D p1 = new Point2D(0,0);
                Point2D p2 = new Point2D(this.getWidth(), this.getHeight());
                notesLayer.selectNotes(p1, p2);
            }else if(event.getCode() == KeyCode.UP){
                presenter.moveUpSelected(12);
                event.consume();
            } else if(event.getCode() == KeyCode.DOWN){
                presenter.moveDownSelected(-12);
                event.consume();
            } else if(event.getCode() == KeyCode.LEFT){
                LOG.debug("32nds in bar {}", get32ndsCountInBar());
                presenter.moveLeftSelected(get32ndsCountInBar());
                event.consume();
            } else if(event.getCode() == KeyCode.RIGHT){
                LOG.debug("32nds in bar {}", get32ndsCountInBar());
                presenter.moveRightSelected(get32ndsCountInBar());
                event.consume();
            }

        } else {
            if(event.getCode() == KeyCode.DELETE){
                notesLayer.getSelectedNoteModells().forEach(noteModell -> presenter.deleteNote(noteModell.id));
            } else if(event.getCode() == KeyCode.UP){
                presenter.moveUpSelected(1);
                event.consume();
            } else if(event.getCode() == KeyCode.DOWN){
                presenter.moveDownSelected(-1);
                event.consume();
            } else if(event.getCode() == KeyCode.LEFT){
                presenter.moveLeftSelected(1);
                event.consume();
            } else if(event.getCode() == KeyCode.RIGHT){
                presenter.moveRightSelected(1);
                event.consume();
            }
        }
        LOG.debug("key: {}, {}, {}, {}", event, event.getText(), event.getCharacter(), event.getCode());
    }

    private void initLayers() {
        initGridLayer();
        initSelectioLayer();
        initNotesLayer();
        initCursorLayer();
        initMaskLayer();
    }

    private void initGridLayer() {
        layers.add(gridLayer);
        this.getChildren().add(gridLayer);
    }

    private void initNotesLayer() {
        layers.add(notesLayer);
        this.getChildren().add(notesLayer);
    }

    private void initCursorLayer() {
        layers.add(cursorLayer);
        this.getChildren().add(cursorLayer);
        this.addEventHandler(MouseEvent.ANY, cursorLayer);
    }

    private void initMaskLayer() {
        layers.add(maskLayer);
        this.getChildren().add(maskLayer);
    }

    private void initSelectioLayer(){
        this.getChildren().add(selectionLayer);
        this.addEventHandler(MouseEvent.ANY, selectionLayer);
    }

    @Override
    protected void updateGui() {
        this.layers.forEach(Layer::updateGui);
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
        int value = fourthInBar.intValue() * 8;
        return value;
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

    public void selectNotes(Point2D startPoint, Point2D endPoint) {
        notesLayer.selectNotes(startPoint, endPoint);
    }

    public List<NoteModell> getSelectedNoteIds(){
        return notesLayer.getSelectedNoteModells();
    }

    public void playChord(int midiCode, int ertek, ChordType currentChordType) {
        presenter.playChord(midiCode, ertek, currentChordType);
    }
}
