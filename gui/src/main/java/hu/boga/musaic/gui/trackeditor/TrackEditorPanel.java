package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.note.NoteBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.noteeditor.NoteRectangle;
import hu.boga.musaic.gui.trackeditor.events.AddChordEvent;
import hu.boga.musaic.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.musaic.gui.trackeditor.events.NoteMovedEvent;
import hu.boga.musaic.gui.trackeditor.events.NotePlayEvent;
import hu.boga.musaic.midigateway.Player;
import hu.boga.musaic.musictheory.enums.ChordType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MetaMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrackEditorPanel extends TrackEditorBasePanel {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPanel.class);
    public static final Color DEFAULT_VERTICAL_LINE_COLOR = Color.LIME;
    public static final Color DEFAULT_HORIZONTAL_LINE_COLOR = Color.LIME;
    public static final Paint TEXT_COLOR = Color.WHITE;
    private List<NoteDto> notes;
    private SettingsContextMenu contextMenu;
    private ChordType currentChordType = null;
    private NoteChangeListener noteChangeListener;
    private EventBus eventBus = new EventBus("TRACK_EDITOR_PANEL_EVENT_BUS");

    private List<String> movedNoteIds = new ArrayList<>();

    private Rectangle selectionRect = new Rectangle();
    private Rectangle loopRectangle = new Rectangle();
    private Line cursorLine = new Line();

    private int loopStartTick;
    private int loopEndTick;
    private Color noteColor;
    private NoteBoundaryIn noteBoundaryIn;


    public TrackEditorPanel() {
        super();
        this.setOnMouseClicked(event -> this.handleMouseClick(event));

        this.setOnMousePressed(event -> {
            selectionRect.setVisible(true);
            selectionRect.setX(event.getX());
            selectionRect.setY(event.getY());
        });

        this.setOnMouseDragged(event -> {
            double width = event.getX() - selectionRect.getX();
            double height = event.getY() - selectionRect.getY();
            selectionRect.setWidth(width);
            selectionRect.setHeight(height);

        });
        this.setOnMouseReleased(this::handleMouseReleased);

        eventBus.register(this);
        contextMenu = new SettingsContextMenu(eventBus);

        selectionRect.setStroke(Color.RED);
        selectionRect.setFill(Color.color(
                Color.WHITE.getRed(),
                Color.WHITE.getGreen(),
                Color.WHITE.getBlue(), 0.3));

        loopRectangle.setStroke(Color.WHITE);
        loopRectangle.setFill(Color.color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHEAT.getBlue(), 0.3));

        initMetaEventListener();
    }

    private void initMetaEventListener() {
        Player.sequencer.addMetaEventListener(metaMessage -> {
            if(metaMessage.getType() == CommandEnum.CUE_MARKER.getIntValue()){
                setCursorLinePosition(metaMessage);
            }
        });
    }

    private void setCursorLinePosition(MetaMessage metaMessage) {
        int tick = Integer.parseInt(new String(metaMessage.getData(), StandardCharsets.UTF_8));
        final int x = (int) (tick * getTickWidth());
        cursorLine.setStartX(x);
        cursorLine.setEndX(x);
    }

    private void initCursorLine() {
        cursorLine.setStroke(Color.RED);
        cursorLine.setStrokeWidth(3);
        cursorLine.setStartY(0);
        cursorLine.setEndY(this.getHeight());
        cursorLine.setStartX(0);
        cursorLine.setEndX(0);
        this.getChildren().add(cursorLine);

    }

    private void handleMouseReleased(MouseEvent event) {
        selectNotesDraggedOver();
        resetSelectionRect();
    }

    private void resetSelectionRect() {
        selectionRect.setVisible(false);
        selectionRect.setWidth(0);
        selectionRect.setHeight(0);
    }

    private void selectNotesDraggedOver() {
        this.getChildren().forEach(node -> {
            if(node instanceof NoteRectangle){
                NoteRectangle nr = (NoteRectangle) node;
                if(nr.intersects(selectionRect.getBoundsInLocal())){
                    nr.toggleSlection();
                }
            }
        });
    }

    @Override
    public void paintNotes() {
        getChildren().clear();
        this.initializeCanvas();

        getChildren().add(selectionRect);
        selectionRect.setVisible(false);

        getChildren().add(loopRectangle);
        loopRectangle.setVisible(loopEndTick != 0);
        loopRectangle.setHeight(getHeight());
        loopRectangle.setY(0);
        showLoopRect();

        this.notes.forEach(noteDto -> {
            this.paintNote(noteDto);
        });

        this.getChildren().add(cursor);
        initCursorLine();
    }

    public void setNotes(final List<NoteDto> notes) {
        this.notes = notes;
    }

    public void setNoteChangeListener(NoteChangeListener noteChangeListener) {
        this.noteChangeListener = noteChangeListener;
    }

    @Subscribe
    private void handleDeleteNoteEvent(DeleteNoteEvent event){
        this.noteChangeListener.onDeleteNoteEvent(event);
    }

    @Subscribe
    private void handleDeleteAllNotesEvent(SettingsContextMenu.DeleteAllNotesEvent event) {
        List<DeleteNoteEvent> events = getAllNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getNoteId()))
                .collect(Collectors.toList());
        LOG.debug("deleting notes {}", events);
        this.noteChangeListener.onDeleteNoteEvent(events.toArray(DeleteNoteEvent[]::new));
    }

    @Subscribe
    private void handleDeleteSelectedNoteEvent(SettingsContextMenu.DeleteSelectedNoteEvent event) {
        List<DeleteNoteEvent> events = getSelectedNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getNoteId()))
                .collect(Collectors.toList());
        events.forEach(ev -> {
            LOG.debug("deleting notes {}", ev.getNoteId());
        });
        this.noteChangeListener.onDeleteNoteEvent(events.toArray(DeleteNoteEvent[]::new));
    }

    @Subscribe
    private void handleInvertSelectionEvent(SettingsContextMenu.InvertSelectionEvent event) {
        getAllNoteRectangles().forEach(noteRectangle -> {
            noteRectangle.toggleSlection();
        });
    }

    @Subscribe
    private void handleDeSeletAllEventEvent(SettingsContextMenu.DeSelectAllEvent event) {
        getSelectedNoteRectangles().forEach(noteRectangle -> noteRectangle.toggleSlection());
        paintNotes();
    }

    @Subscribe
    private void handleSelectAllEvent(SettingsContextMenu.SelectAllEvent event) {
        getAllNoteRectangles().stream()
                .filter(noteRectangle -> !noteRectangle.isSelected())
                .forEach(noteRectangle -> noteRectangle.toggleSlection());
        paintNotes();
    }

    @Subscribe
    private void handleChordChangeEvent(SettingsContextMenu.ChordTypeChangedEvent event) {
        currentChordType = event.getChordType();
        cursor.setChordType(currentChordType);
    }
    @Subscribe
    private void handleNoteLengthChangedEvent(SettingsContextMenu.NoteLengthChangedEvent event) {
        currentNoteLength = event.getNoteLength();
        cursor.setPrefWidth(currentNoteLength.getErtek() * get32ndsWidth());
    }

    @Subscribe
    private void handleNoteMovedEvent(NoteRectangle.NoteMovedEvent even){
        if(!movedNoteIds.contains(even.getId())){
            movedNoteIds.add(even.getId());
        }
    }

    @Subscribe
    private void handleLoopStartEvent(SettingsContextMenu.LoopStartEvent event){
        loopStartTick = (int) event.tick;
        if(loopEndTick == 0){
            loopEndTick = loopStartTick + 1;
        }
        showLoopRect();

    }

    private void showLoopRect() {
        loopRectangle.setX(getTickWidth() * loopStartTick);
        int width = (int) ((loopEndTick - loopStartTick) * getTickWidth());
        loopRectangle.setWidth(width);
        loopRectangle.setVisible(true);
    }

    @Subscribe
    private void handleLoopEndEvent(SettingsContextMenu.LoopEndEvent event){
        loopEndTick = (int) event.tick;
        showLoopRect();
    }

    @Subscribe
    private void handleClearLoopEvent(SettingsContextMenu.ClearLoopEvent event){
        LOG.debug("clearing loop");
        loopRectangle.setVisible(false);
        loopStartTick = 0;
        loopEndTick = 0;
    }

    private List<NoteRectangle> getAllNoteRectangles() {
        List<NoteRectangle> l = getChildren().stream().filter(node -> node instanceof NoteRectangle).map(node -> (NoteRectangle) node).collect(Collectors.toList());
        return l;
    }

    protected List<NoteRectangle> getSelectedNoteRectangles() {
        List<NoteRectangle> l = getAllNoteRectangles().stream().filter(noteRectangle -> noteRectangle.isSelected()).collect(Collectors.toList());
        return l;
    }

    private Optional<NoteRectangle> getNoteRectangleByNoteId(String noteId){
        return getAllNoteRectangles().stream().filter(noteRectangle -> noteRectangle.getNoteId().equals(noteId)).findFirst();
    }


    private void handleMouseClick(final MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
            this.contextMenu.currentTick = this.getTickByX((int) getCaculatedX(event.getX()));
        } else if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1){
            NotePlayEvent notePlayEvent = new NotePlayEvent(getPitchByY((int) event.getY()).getMidiCode(), currentNoteLength.getErtek());
            this.noteChangeListener.onNotePlay(notePlayEvent);

        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            final AddChordEvent addChordEvent = new AddChordEvent(this.getTickByX((int) getCaculatedX(event.getX())), this.getPitchByY((int) event.getY()).getMidiCode(), currentNoteLength.getErtek(), currentChordType);
            this.noteChangeListener.onAddChordEvent(addChordEvent);
        }
    }

    private void paintNote(final NoteDto noteDto) {
        try {
            final Rectangle rect = this.addNoteRectangle(noteDto);
            getChildren().add(rect);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private NoteRectangle addNoteRectangle(final NoteDto noteDto) {

        final int x = (int) (noteDto.tick * getTickWidth());
        final NoteRectangle noteRectangle = new NoteRectangle(noteDto, eventBus, noteColor, noteBoundaryIn);
        noteRectangle.setX(x);
        noteRectangle.setY(this.getYByPitch((int) noteDto.midiCode));
        noteRectangle.setWidth(getTickWidth() * noteDto.length);
        noteRectangle.setHeight(this.getPitchHeight());
        movedNoteIds.clear();
        return noteRectangle;
    }

    @Subscribe
    private void handleMouseReleasedOnNoteEvent(NoteRectangle.MouseReleasedEvent event) {
        if(movedNoteIds.size() > 0){
            performNotesMove();
        }
    }

    private void performNotesMove() {
        List<NoteMovedEvent> events = new ArrayList<>();
        movedNoteIds.forEach(id -> {
            getNoteRectangleByNoteId(id).ifPresent(noteRectangle -> {
                int newTick = getTickByX((int) getCaculatedX(noteRectangle.getX()));
                events.add(new NoteMovedEvent(id, newTick));
            });
        });
        noteChangeListener.onNoteMoved(events.toArray(new NoteMovedEvent[0]));
        movedNoteIds.clear();
    }

    public int getLoopStartTick() {
        return loopStartTick;
    }

    public int getLoopEndTick() {
        return loopEndTick;
    }

    public void setNoteColor(Color color) {
        this.noteColor = color;
    }

    public void setNoteBoundaryIn(NoteBoundaryIn noteBoundaryIn) {
        this.noteBoundaryIn = noteBoundaryIn;
    }

    public void setLoopEnd(int loopEnd) {
        this.loopEndTick = loopEnd;
    }
}
