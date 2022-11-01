package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.GuiConstants;
import hu.boga.musaic.gui.trackeditor.events.*;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.Scale;
import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteLength;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class TrackEditorPanel extends Pane {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPanel.class);

    private static final int KEYBOARD_OFFSET = 0;
    public static final Color DEFAULT_VERTICAL_LINE_COLOR = Color.LIME;
    public static final Color DEFAULT_HORIZONTAL_LINE_COLOR = Color.LIME;
    public static final Paint TEXT_COLOR = Color.WHITE;
    public static final Color DISABLED_COLOR = Color.GAINSBORO;
    int measureNum = 100;
    private int resolution;
    private float zoomFactor = 1f;
    private List<NoteDto> notes;
    private ContextMenu contextMenu;

    private NoteLength currentNoteLength = NoteLength.HARMICKETTED;
    private ChordType currentChordType = null;
    private Tone currentTone = null;
    private NoteName currentRoot = NoteName.C;

    private CursorRectangle cursor = new CursorRectangle();
    private List<Point2D> selectedPoints = new ArrayList<>(0);

    private EventBus eventBus;

    public TrackEditorPanel() {
        this.createContextMenu();
        this.setOnMouseEntered(event -> showCursor(event));
        this.setOnMouseMoved(event -> moveCursor(event));
        this.setOnMouseExited(event -> hideCursor());
        this.setOnMouseClicked(event -> this.handleMouseClick(event));

        contextMenu = createContextMenu();
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    private void hideCursor() {
        cursor.setVisible(false);
    }

    private void moveCursor(MouseEvent event) {
        cursor.setLayoutX(event.getX());
        int y = getYByPitch(getPitchByY((int) event.getY()).getMidiCode());
        cursor.setLayoutY(y);
    }

    private void showCursor(MouseEvent event) {
        cursor.setLayoutX(event.getX());
        cursor.setLayoutY(event.getY() - cursor.getHeight());
        cursor.setWidth(currentNoteLength.getErtek() * get32ndsWidth());
        cursor.setVisible(true);
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
                selectedPoints.add(new Point2D(noteRectangle.getX(), noteRectangle.getY()));
            }
        });
    }

    private void deleteAllNotes() {
        List<DeleteNoteEvent> events = getAllNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getTick(), noteRectangle.getPitch()))
                .collect(Collectors.toList());
        LOG.debug("deleting notes " + events);
        eventBus.post(events.toArray(DeleteNoteEvent[]::new));
        selectedPoints.clear();
    }

    private void deleteSelectedNotes() {
        List<DeleteNoteEvent> events = getSelectedNoteRectangles().stream()
                .map(noteRectangle -> new DeleteNoteEvent(noteRectangle.getTick(), noteRectangle.getPitch()))
                .collect(Collectors.toList());
        LOG.debug("deleting notes " + events);
        eventBus.post(events.toArray(DeleteNoteEvent[]::new));
        selectedPoints.clear();
    }

    private void selectAllNotes() {
        List<Point2D> allPoints = getAllNoteRectangles().stream().map(noteRectangle -> new Point2D(noteRectangle.getX(), noteRectangle.getY())).collect(Collectors.toList());
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
            if(currentChordType == null){
                final AddNoteEvent addNoteEvent = new AddNoteEvent(this.getTickByX((int) event.getX()), this.getPitchByY((int) event.getY()).getMidiCode(), currentNoteLength.getErtek());
                eventBus.post(addNoteEvent);
//                trackEventListeners.forEach(trackEventListener -> trackEventListener.onAddNoteEvent(addNoteEvent));
            } else{
                final AddChordEvent addChordEvent = new AddChordEvent(this.getTickByX((int) event.getX()), this.getPitchByY((int) event.getY()).getMidiCode(), currentNoteLength.getErtek(), currentChordType);
                eventBus.post(addChordEvent);
//                trackEventListeners.forEach(trackEventListener -> trackEventListener.onAddChordEvent(addChordEvent));
            }
        }
    }

    private Optional<NoteRectangle> getChildAtPoint(final Point2D point) {
        return getAllNoteRectangles().stream().filter(node -> node.contains(point)).findFirst();
    }

    public void paintNotes() {
        getChildren().clear();
        this.initializeCanvas();
        this.paintVerticalLines();
        this.paintHorizontalLines();
        this.paintKeyboard();
        this.paintDisabled();

        this.getChildren().add(cursor);

        this.notes.forEach(noteDto -> {
            this.paintNote(noteDto);
        });
    }

    private void paintDisabled() {
        if (currentTone != null && currentRoot != null) {
            List<NoteName> scale = Scale.getScale(currentRoot, currentTone);
            for (int y = 0; y < getPrefHeight(); y += this.getPitchHeight()) {
                paintDisabledLines(scale, y);
            }
        }
    }

    private void paintDisabledLines(List<NoteName> scale, int y) {
        NoteName currentNoteName = NoteName.byCode(getPitchByY(y + 5).getMidiCode());
        if (!scale.contains(currentNoteName)) {
            paintDisabledRectangle(y);
        }
        if (currentNoteName == currentRoot) {
            paintRootMarker(y);
        }
    }

    private void paintRootMarker(int y) {
        Line line = new Line(0, y + getPitchHeight() / 2, getPrefWidth(), y + getPitchHeight() / 2);
        line.setStroke(Color.RED);
        getChildren().add(line);
    }

    private void paintDisabledRectangle(int y) {
        final Rectangle rectangle = new Rectangle();
        rectangle.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        });
        rectangle.setFill(DISABLED_COLOR);
        rectangle.setX(0);
        rectangle.setY(y);
        rectangle.setWidth(getPrefWidth());
        rectangle.setHeight(getPitchHeight());
        getChildren().add(rectangle);
    }

    private void paintKeyboard() {
        final List<String> noteNames = Arrays.stream(NoteName.values())
                .sorted(Comparator.comparingInt(NoteName::ordinal).reversed())
                .map(noteName -> noteName.name()).collect(Collectors.toList());
        final int increment = this.getPitchHeight();
        int y = increment;
        for (int i = 0; i <= GuiConstants.OCTAVES; i++) {
            for (int j = 0; j < 12; j++) {
                final Text text = new Text(noteNames.get(j) + " " + (GuiConstants.OCTAVES - i));
                text.setX(5);
                text.setY(y - 5);
                text.setStroke(TrackEditorPanel.TEXT_COLOR);
                y += increment;
                getChildren().add(text);
            }
        }
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

        final int x = (int) (noteDto.tick * this.getTickWidth());
        final NoteRectangle noteRectangle = new NoteRectangle(x, (int) noteDto.midiCode, eventBus);
        noteRectangle.setX(x);
        noteRectangle.setY(this.getYByPitch((int) noteDto.midiCode));
        noteRectangle.setWidth(getTickWidth() * noteDto.length);
        noteRectangle.setHeight(this.getPitchHeight());

        noteRectangle.setOnMouseReleased(event -> {
            if (noteRectangle.isDragging()) {
                eventBus.post(new MoveNoteEvent((int) noteDto.tick, (int) noteDto.midiCode, TrackEditorPanel.this.getTickByX((int) noteRectangle.getX())));
//                trackEventListener.onMoveNoteEvent();
            }
            noteRectangle.setDragging(false);
        });

        noteRectangle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                eventBus.post(new DeleteNoteEvent(noteDto.tick, noteDto.midiCode));
            } else if (event.getClickCount() == 1) {
                noteRectangle.setSelected(!noteRectangle.isSelected());
                if (noteRectangle.isSelected()) {
                    selectedPoints.add(new Point2D(noteRectangle.getX(), noteRectangle.getY()));
                } else {
                    selectedPoints.remove(new Point2D(noteRectangle.getX(), noteRectangle.getY()));
                }
                event.consume();
            }
        });

        if (selectedPoints.contains(new Point2D(noteRectangle.getX(), noteRectangle.getY()))) {
            noteRectangle.setSelected(true);
        }

        return noteRectangle;
    }

    private void initializeCanvas() {
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        setPrefWidth(this.getWorkingWidth());
        setPrefHeight(this.getWorkingHeight());
        getChildren().removeAll();
    }

    private void paintVerticalLines() {
        final int w32nds = this.get32ndsWidth();
        int counter = 0;
        for (int x = 0; x < this.getWorkingWidth(); x += w32nds) {
            final Line line = new Line();
            line.setStartX(x);
            line.setStartY(0);
            line.setEndX(x);
            line.setEndY(getPrefHeight());
            if (counter % 32 == 0) {

                line.setStrokeWidth(3);
                line.setStroke(TrackEditorPanel.DEFAULT_VERTICAL_LINE_COLOR);
            } else {
                line.setStroke(Color.LIME);
            }
            counter++;
            getChildren().addAll(line);
        }
    }

    private void paintHorizontalLines() {
        for (int y = 0; y < getPrefHeight(); y += this.getPitchHeight()) {
            final Line line = new Line();
            line.setStroke(TrackEditorPanel.DEFAULT_HORIZONTAL_LINE_COLOR);
            line.setStartX(0);
            line.setStartY(y);
            line.setEndX(getPrefWidth());
            line.setEndY(y);
            getChildren().add(line);
        }
    }


    private int getPitchHeight() {
        return (int) (getPrefHeight() / (GuiConstants.OCTAVES * 12));
    }

    private int get32ndsWidth() {
        return (int) (((this.resolution * 4) / 32) * this.zoomFactor);
    }

    private int getWorkingWidth() {
        return (int) (this.getMeasureWidth() * this.measureNum);
    }

    private double getMeasureWidth() {
        return this.get32ndsWidth() * 32;
    }

    private int getWorkingHeight() {
        return GuiConstants.OCTAVES * GuiConstants.LINE_HEIGHT * 12;
    }

    private double getTickWidth() {
        return this.getMeasureWidth() / (this.resolution * 4);
    }

    public void setZoomFactor(final float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void setNotes(final List<NoteDto> notes) {
        this.notes = notes;
    }

    public void setResolution(final int resolution) {
        this.resolution = resolution;
    }

    private Pitch getPitchByY(final int y) {
        final int pitch = (int) ((getPrefHeight() / GuiConstants.LINE_HEIGHT - 1) - (y / GuiConstants.LINE_HEIGHT));
        return new Pitch(pitch);
    }

    private int getTickByX(final int x) {
        final double tickWidth = this.getTickWidth();
        final int tick = (int) ((x - TrackEditorPanel.KEYBOARD_OFFSET) / tickWidth);
        return tick;
    }

    private int getYByPitch(final int midiCode) {
        return (GuiConstants.OCTAVES * 12 - 1 - midiCode) * GuiConstants.LINE_HEIGHT;
    }

    public void setCurrentRoot(NoteName currentRoot) {
        this.currentRoot = currentRoot;
        paintNotes();

    }

    public void setCurrentTone(Tone currentTone) {
        this.currentTone = currentTone;
        paintNotes();
    }
}
