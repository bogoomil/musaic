package hu.boga.musaic.gui.noteeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.note.NoteBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.trackeditor.events.DeleteNoteEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NoteRectangle extends Rectangle {
    private static final Logger LOG = LoggerFactory.getLogger(NoteRectangle.class);
    public static final Color SELECTED_COLOR = Color.color(Color.LAWNGREEN.getRed(), Color.LAWNGREEN.getGreen(), Color.LAWNGREEN.getBlue(), 0.8);
    private Color fill;

    private int length;
    private boolean selected;

    private EventBus eventBus;
    private double offset;
    private NoteDto noteDto;
    private NoteBoundaryIn noteBoundaryIn;

    private static final List<String> selectedIds = new ArrayList<>();

    public NoteRectangle(NoteDto noteDto, EventBus eventBus, Color color, NoteBoundaryIn noteBoundaryIn) {
        this.noteDto = noteDto;
        this.eventBus = eventBus;

        this.selected = selectedIds.contains(noteDto.id);
        fill = selected ? SELECTED_COLOR :Color.color(color.getRed(), color.getGreen(), color.getBlue(), noteDto.velocity);

        this.noteBoundaryIn = noteBoundaryIn;
        eventBus.register(this);
        this.setStroke(color);
        this.setStrokeWidth(3);
        this.setFill(fill);
        this.setArcHeight(15);
        this.setArcWidth(15);
        setUpEventHandlers();
    }

    private void setUpEventHandlers() {
        setOnMousePressed(this::handleMousePressed);
        setOnMouseReleased(this::handleMouseReleased);
        setOnMouseDragged(this::handleMouseDragged);
        this.xProperty().addListener((observable, oldValue, newValue) -> xPropertyChanged());
        setOnMouseClicked(this::handleMouseClicked);
    }

    private void handleMouseReleased(MouseEvent event) {
        eventBus.post(new MouseReleasedEvent());
        event.consume();
    }

    private void handleMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            eventBus.unregister(this);
            eventBus.post(new DeleteNoteEvent(noteDto.id));
        } else if (event.getClickCount() == 1) {
            toggleSlection();
        }
        event.consume();
    }

    private void handleMousePressed(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY){
            showContextMenu(event);
        }
        offset = event.getX() - getX();
        event.consume();
    }

    private void showContextMenu(MouseEvent event) {
        getContextMenu().show(this, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private ContextMenu getContextMenu(){
        ContextMenu menu = new ContextMenu();

        int currVelocity = (int) (noteDto.velocity * 100);

        Label label = new Label("" + currVelocity);

        Slider volumeSlider = new Slider();
        volumeSlider.orientationProperty().setValue(Orientation.VERTICAL);
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(noteDto.velocity);
        volumeSlider.setMajorTickUnit(0.1);
        volumeSlider.setSnapToTicks(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.blockIncrementProperty().setValue(0.1);
        volumeSlider.setMinorTickCount(10);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            noteDto.velocity = newValue.doubleValue();
            noteBoundaryIn.setNoteVolume(noteDto.id, noteDto.velocity);
            fill = Color.color(fill.getRed(), fill.getGreen(), fill.getBlue(), noteDto.velocity);
            label.setText(((int) (noteDto.velocity * 100)) + "%");
            setFill(fill);
        });

        VBox hBox = new VBox();
        hBox.getChildren().add(volumeSlider);
        hBox.getChildren().add(label);

        MenuItem volumeMenuItem = new MenuItem("", hBox);
        menu.getItems().add(volumeMenuItem);
        return menu;
    }

    private void xPropertyChanged() {
        if(getX() < 0){
            setX(0);
        }
        eventBus.post(new NoteMovedEvent(noteDto.id));
    }

    private void handleMouseDragged(MouseEvent e) {
        if (contains(e.getX(), e.getY())) {
            double delta = -1 * (getX() - (e.getX() - offset));
            setX(e.getX() - offset);
            eventBus.post(new NoteRectangleMovedEvent(noteDto.id, delta));
        }
        e.consume();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public int getTick() {
        return (int) this.noteDto.tick;
    }

    public int getPitch() {
        return this.noteDto.midiCode;
    }

    public void toggleSlection() {
        this.selected = !selected;
        if(selected){
            selectedIds.add(noteDto.id);
            setFill(SELECTED_COLOR);
        }else{
            selectedIds.remove(noteDto.id);
            Color origColor = (Color) getStroke();
            setFill(Color.color(origColor.getRed(), origColor.getGreen(), origColor.getBlue(), noteDto.velocity));
        }
    }

    public String getNoteId(){
        return noteDto.id;
    }

    @Subscribe
    private void handleNoteMovedEvent(NoteRectangleMovedEvent event){
        if(this.selected && !this.noteDto.id.equals(event.noteId)){
            this.setX(getX() + event.delta);
        }
    }

    public static class NoteMovedEvent{
        String id;
        public NoteMovedEvent(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

    private static class NoteRectangleMovedEvent {
        String noteId;
        double delta;
        public NoteRectangleMovedEvent(String noteId, double delta) {
            this.noteId = noteId;
            this.delta = delta;
        }
    }

    public static  class MouseReleasedEvent{}
}
