package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.trackeditor.events.*;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private List<String> selectedPoints = new ArrayList<>(0);
    private NoteChangeListener noteChangeListener;
    private EventBus eventBus = new EventBus("TRACK_EDITOR_PANEL_EVENET_BUS");

    public TrackEditorPanel() {
        super();
        this.createContextMenu();
        this.setOnMouseClicked(event -> this.handleMouseClick(event));

        contextMenu = createContextMenu();
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

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        Menu creationMenu = new Menu("Creation", null,
                new Menu("Length", null, createNoteLengthMenuItem()),
                new Menu("Chords", null, createChordMenuItems())
        );


        MenuItem selectAllMenu = new MenuItem("Select all");
        selectAllMenu.addEventHandler(ActionEvent.ACTION, event -> selectAllNotes());
        MenuItem deSelectAllMenu = new MenuItem("Deselect all");
        deSelectAllMenu.addEventHandler(ActionEvent.ACTION, event -> deSelectAllNotes());
        MenuItem invertSelectionMenu = new MenuItem("Invert selection");
        invertSelectionMenu.addEventHandler(ActionEvent.ACTION, event -> invertSelection());
        Menu selectionMenu = new Menu("Selection", null,
                selectAllMenu,
                deSelectAllMenu,
                invertSelectionMenu
        );

        MenuItem deleteSelectedNotesMenu = new MenuItem("selected");
        deleteSelectedNotesMenu.addEventHandler(ActionEvent.ACTION, event -> deleteSelectedNotes());
        MenuItem deleteAllNotesMenu = new MenuItem("all");
        deleteAllNotesMenu.addEventHandler(ActionEvent.ACTION, event -> deleteAllNotes());
        Menu deletionMenu = new Menu("Delete", null,
                deleteSelectedNotesMenu,
                deleteAllNotesMenu
        );
        contextMenu.getItems().add(creationMenu);
        contextMenu.getItems().add(selectionMenu);
        contextMenu.getItems().add(deletionMenu);

        return contextMenu;
    }

    private void invertSelection() {
        selectedPoints.clear();
        getAllNoteRectangles().forEach(noteRectangle -> {
            noteRectangle.toggleSlection();
            if (noteRectangle.isSelected()) {
                selectedPoints.add(noteRectangle.getNoteId());
            }
        });
    }

    private void deleteAllNotes() {
        List<DeleteNoteEvent> events = getAllNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getTick(), noteRectangle.getPitch()))
                .collect(Collectors.toList());
        LOG.debug("deleting notes " + events);
        this.noteChangeListener.onDeleteNoteEvent(events.toArray(DeleteNoteEvent[]::new));
        selectedPoints.clear();
    }

    private void deleteSelectedNotes() {
        List<DeleteNoteEvent> events = getSelectedNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getTick(), noteRectangle.getPitch()))
                .collect(Collectors.toList());
        LOG.debug("deleting notes " + events);
        this.noteChangeListener.onDeleteNoteEvent(events.toArray(DeleteNoteEvent[]::new));
        selectedPoints.clear();
    }

    private void selectAllNotes() {
        List<String> allPoints = getAllNoteRectangles().stream().map(noteRectangle -> noteRectangle.getNoteId()).collect(Collectors.toList());
        this.selectedPoints.addAll(allPoints);
        paintNotes();
    }

    private void deSelectAllNotes() {
        this.selectedPoints.clear();
        paintNotes();
    }

    private List<NoteRectangle> getAllNoteRectangles() {
        List<NoteRectangle> l = getChildren().stream().filter(node -> node instanceof NoteRectangle).map(node -> (NoteRectangle) node).collect(Collectors.toList());
        LOG.debug("notes " + l);
        return l;
    }

    private List<NoteRectangle> getSelectedNoteRectangles() {
        return getAllNoteRectangles().stream().filter(noteRectangle -> noteRectangle.isSelected()).collect(Collectors.toList());
    }

    private RadioMenuItem[] createChordMenuItems() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[ChordType.values().length + 1];
        items[0] = new RadioMenuItem("single note");
        items[0].setToggleGroup(toggleGroup);
        items[0].setSelected(true);
        items[0].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentChordType = null;
            }
        });
        for (int i = 0; i < ChordType.values().length; i++) {
            ChordType currChordType = ChordType.values()[i];
            RadioMenuItem menuItem = new RadioMenuItem(currChordType.name());
            menuItem.setToggleGroup(toggleGroup);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentChordType = currChordType;
                    cursor.setChordType(currChordType);
                }
            });
            items[i + 1] = menuItem;
        }
        return items;

    }

    private RadioMenuItem[] createNoteLengthMenuItem() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[NoteLength.values().length];
        for (int i = 0; i < NoteLength.values().length; i++) {
            NoteLength currLength = NoteLength.values()[i];
            RadioMenuItem menuItem = new RadioMenuItem(currLength.name());
            menuItem.setToggleGroup(toggleGroup);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                currentNoteLength = currLength;
                cursor.setPrefWidth(currentNoteLength.getErtek() * get32ndsWidth());
            });
            items[i] = menuItem;
        }
        items[0].setSelected(true);
        return items;
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
            final Rectangle rect = this.createNoteRectangle(noteDto);
            getChildren().add(rect);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private NoteRectangle createNoteRectangle(final NoteDto noteDto) {

        final int x = (int) (noteDto.tick * getTickWidth());
        final NoteRectangle noteRectangle = new NoteRectangle(noteDto, eventBus);
        noteRectangle.setX(x);
        noteRectangle.setY(this.getYByPitch((int) noteDto.midiCode));
        noteRectangle.setWidth(getTickWidth() * noteDto.length);
        noteRectangle.setHeight(this.getPitchHeight());

        noteRectangle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                this.noteChangeListener.onDeleteNoteEvent(new DeleteNoteEvent(noteDto.tick, noteDto.midiCode));
            } else if (event.getClickCount() == 1) {
                noteRectangle.setSelected(!noteRectangle.isSelected());
                if (noteRectangle.isSelected()) {
                    selectedPoints.add(noteRectangle.getNoteId());
                } else {
                    selectedPoints.remove(noteRectangle.getNoteId());
                }
                event.consume();
            }
        });

        if (selectedPoints.contains(noteRectangle.getNoteId())) {
            noteRectangle.setSelected(true);
        }

        return noteRectangle;
    }

}
