package hu.boga.musaic.gui.trackeditor;

import hu.boga.musaic.gui.GuiConstants;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.Scale;
import hu.boga.musaic.musictheory.enums.NoteLength;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public abstract class TrackEditorBasePanel extends Pane {

    public static final Color DISABLED_COLOR = Color.GAINSBORO;
    private int measureNum = 100;
    protected int resolution;
    protected Tone currentTone = null;
    protected NoteName currentRoot = NoteName.C;
    protected NoteLength currentNoteLength = NoteLength.HARMICKETTED;
    protected CursorRectangle cursor = new CursorRectangle();

    private static final int KEYBOARD_OFFSET = 0;
    private float zoomFactor = 1f;

    public TrackEditorBasePanel(){
        this.setOnMouseEntered(event -> showCursor(event));
        this.setOnMouseMoved(event -> moveCursor(event));
        this.setOnMouseExited(event -> hideCursor());
    }

    public abstract void paintNotes();

    public void setCurrentRoot(NoteName currentRoot) {
        this.currentRoot = currentRoot;
        paintNotes();
    }

    public void setCurrentTone(Tone currentTone) {
        this.currentTone = currentTone;
        paintNotes();
    }

    public void setResolution(final int resolution) {
        this.resolution = resolution;
    }

    public void setZoomFactor(final float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    protected void initializeCanvas() {
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        setPrefWidth(this.getWorkingWidth());
        setPrefHeight(this.getWorkingHeight());
        getChildren().removeAll();
        this.paintVerticalLines();
        this.paintHorizontalLines();
        this.paintKeyboard();
        this.paintDisabled();
    }

    protected int getPitchHeight() {
        return (int) (getPrefHeight() / (GuiConstants.OCTAVES * 12));
    }

    protected int get32ndsWidth() {
        return (int) (((this.resolution * 4) / 32) * this.zoomFactor);
    }

    protected double getMeasureWidth() {
        return this.get32ndsWidth() * 32;
    }

    protected Pitch getPitchByY(final int y) {
        final int pitch = (int) ((getPrefHeight() / GuiConstants.LINE_HEIGHT - 1) - (y / GuiConstants.LINE_HEIGHT));
        return new Pitch(pitch);
    }

    protected int getYByPitch(final int midiCode) {
        return (GuiConstants.OCTAVES * 12 - 1 - midiCode) * GuiConstants.LINE_HEIGHT;
    }

    protected int getTickByX(final int x) {
        final double tickWidth = this.getTickWidth();
        final int tick = (int) ((x - TrackEditorBasePanel.KEYBOARD_OFFSET) / tickWidth);
        return tick;
    }

    protected double getTickWidth() {
        return this.getMeasureWidth() / (this.resolution * 4);
    }

    private int getWorkingWidth() {
        return (int) (this.getMeasureWidth() * this.measureNum);
    }

    private int getWorkingHeight() {
        return GuiConstants.OCTAVES * GuiConstants.LINE_HEIGHT * 12;
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


}
