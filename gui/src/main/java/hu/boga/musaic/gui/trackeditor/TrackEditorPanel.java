package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.trackeditor.events.*;
import hu.boga.musaic.musictheory.enums.ChordType;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class TrackEditorPanel extends TrackEditorBasePanel {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPanel.class);
    public static final Color DEFAULT_VERTICAL_LINE_COLOR = Color.LIME;
    public static final Color DEFAULT_HORIZONTAL_LINE_COLOR = Color.LIME;
    public static final Paint TEXT_COLOR = Color.WHITE;
    private List<NoteDto> notes;
    private ContextMenu contextMenu;
    private ChordType currentChordType = null;
    private List<String> selectedNoteIds = new ArrayList<>(0);
    private NoteChangeListener noteChangeListener;
    private EventBus eventBus = new EventBus("TRACK_EDITOR_PANEL_EVENT_BUS");

    private List<String> movedNoteIds = new ArrayList<>();

    public TrackEditorPanel() {
        super();
        this.setOnMouseClicked(event -> this.handleMouseClick(event));
        eventBus.register(this);
        contextMenu = new SettingsContextMenu(eventBus);
    }

    @Override
    public void paintNotes() {
        getChildren().clear();
        this.initializeCanvas();
        this.getChildren().add(cursor);
        this.notes.forEach(noteDto -> {
            this.paintNote(noteDto);
        });
    }

    public void setNotes(final List<NoteDto> notes) {
        this.notes = notes;
    }

    public void setNoteChangeListener(NoteChangeListener noteChangeListener) {
        this.noteChangeListener = noteChangeListener;
    }

    @Subscribe
    private void handleDeleteAllNotesEvent(SettingsContextMenu.DeleteAllNotesEvent event) {
        List<DeleteNoteEvent> events = getAllNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getNoteId()))
                .collect(Collectors.toList());
        LOG.debug("deleting notes " + events);
        this.noteChangeListener.onDeleteNoteEvent(events.toArray(DeleteNoteEvent[]::new));
        selectedNoteIds.clear();
    }

    @Subscribe
    private void handleDeleteSelectedNoteEvent(SettingsContextMenu.DeleteSelectedNoteEvent event) {
        List<DeleteNoteEvent> events = getSelectedNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getNoteId()))
                .collect(Collectors.toList());
        events.forEach(ev -> {
            LOG.debug("deleting notes " + ev.getNoteId());
        });
        this.noteChangeListener.onDeleteNoteEvent(events.toArray(DeleteNoteEvent[]::new));
        selectedNoteIds.clear();
    }

    @Subscribe
    private void handleInvertSelectionEvent(SettingsContextMenu.InvertSelectionEvent event) {
        selectedNoteIds.clear();
        getAllNoteRectangles().forEach(noteRectangle -> {
            noteRectangle.toggleSlection();
            if (noteRectangle.isSelected()) {
                selectedNoteIds.add(noteRectangle.getNoteId());
            }
        });
    }

    @Subscribe
    private void handleDeSeletAllEventEvent(SettingsContextMenu.DeSelectAllEvent event) {
        this.selectedNoteIds.clear();
        paintNotes();
    }

    @Subscribe
    private void handleSelectAllEvent(SettingsContextMenu.SelectAllEvent event) {
        List<String> allPoints = getAllNoteRectangles().stream().map(noteRectangle -> noteRectangle.getNoteId()).collect(Collectors.toList());
        this.selectedNoteIds.addAll(allPoints);
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
    private void handleNoteMovedEvent(NoteMovedEvent even){
        if(!movedNoteIds.contains(even.getId())){
            movedNoteIds.add(even.getId());
        }
    }

    private List<NoteRectangle> getAllNoteRectangles() {
        List<NoteRectangle> l = getChildren().stream().filter(node -> node instanceof NoteRectangle).map(node -> (NoteRectangle) node).collect(Collectors.toList());
        return l;
    }

    private List<NoteRectangle> getSelectedNoteRectangles() {
        return getAllNoteRectangles().stream().filter(noteRectangle -> noteRectangle.isSelected()).collect(Collectors.toList());
    }

    private Optional<NoteRectangle> getNoteRectangleByNoteId(String noteId){
        return getAllNoteRectangles().stream().filter(noteRectangle -> noteRectangle.getNoteId().equals(noteId)).findFirst();
    }


    private void handleMouseClick(final MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            final AddChordEvent addChordEvent = new AddChordEvent(this.getTickByX((int) event.getX()), this.getPitchByY((int) event.getY()).getMidiCode(), currentNoteLength.getErtek(), currentChordType);
            this.noteChangeListener.onAddChordEvent(addChordEvent);
        }
    }

    private Optional<NoteRectangle> getChildAtPoint(final Point2D point) {
        return getAllNoteRectangles().stream().filter(node -> node.contains(point)).findFirst();
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
        final NoteRectangle noteRectangle = new NoteRectangle(noteDto, eventBus);
        noteRectangle.setX(x);
        noteRectangle.setY(this.getYByPitch((int) noteDto.midiCode));
        noteRectangle.setWidth(getTickWidth() * noteDto.length);
        noteRectangle.setHeight(this.getPitchHeight());

        noteRectangle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                this.noteChangeListener.onDeleteNoteEvent(new DeleteNoteEvent(noteDto.id));
            } else if (event.getClickCount() == 1) {
                noteRectangle.setSelected(!noteRectangle.isSelected());
                if (noteRectangle.isSelected()) {
                    selectedNoteIds.add(noteRectangle.getNoteId());
                } else {
                    selectedNoteIds.remove(noteRectangle.getNoteId());
                }
                event.consume();
            }
        });

        noteRectangle.setOnMouseReleased(event -> handleMouseReleasedOnNoteEvent());

        if (selectedNoteIds.contains(noteRectangle.getNoteId())) {
            noteRectangle.setSelected(true);
        }

        movedNoteIds.clear();

        return noteRectangle;
    }

    private void handleMouseReleasedOnNoteEvent() {
        LOG.debug("------------------------------------------------------");
        LOG.debug("Notes moved: " + movedNoteIds);
        movedNoteIds.forEach(id -> {
            getNoteRectangleByNoteId(id).ifPresent(noteRectangle -> {
                int newTick = getTickByX((int) noteRectangle.getX());
                LOG.debug("MOVING NOTE: {} FROM: {} TO: {}",id, noteRectangle.getTick(), newTick );
                noteChangeListener.onNoteMoved(noteRectangle.getNoteId(), newTick);
            });
        });
        movedNoteIds.clear();
        LOG.debug("Notes moved: " + movedNoteIds);
        LOG.debug("------------------------------------------------------");
    }

}
