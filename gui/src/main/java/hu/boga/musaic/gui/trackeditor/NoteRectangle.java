package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoteRectangle extends Rectangle {
    private static final Logger LOG = LoggerFactory.getLogger(NoteRectangle.class);
    public static final Color SELECTED_COLOR = Color.color(Color.LAWNGREEN.getRed(), Color.LAWNGREEN.getGreen(), Color.LAWNGREEN.getBlue(), 0.7);
    ;
    public static final Color DEFAULT_COLOR = Color.color(Color.DEEPSKYBLUE.getRed(), Color.DEEPSKYBLUE.getGreen(), Color.DEEPSKYBLUE.getBlue(), 0.7);
    private int length;
    private boolean selected;

    private EventBus eventBus;
    private double offset;
    private NoteDto noteDto;

    public NoteRectangle(NoteDto noteDto, EventBus eventBus) {
        this.noteDto = noteDto;
        this.eventBus = eventBus;
        eventBus.register(this);
        this.setFill(DEFAULT_COLOR);
        setUpEventHandlers();
    }

    private void setUpEventHandlers() {

        setOnMousePressed(event -> offset = event.getX() - getX());
        setOnMouseDragged(event -> handleMouseDragged(event));

        this.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(getX() < 0){
                    setX(0);
                }
            }
        });
    }

    private void handleMouseDragged(MouseEvent e) {
        if (contains(e.getX(), e.getY())) {
            double delta = -1 * (getX() - (e.getX() - offset));
            setX(e.getX() - offset);
            eventBus.post(new NoteRectangleMovedEvent(noteDto.id, delta));
        }
        e.consume();
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
        setFill(isSelected() ? SELECTED_COLOR : DEFAULT_COLOR);
    }

    public int getTick() {
        return (int) this.noteDto.tick;
    }

    public int getPitch() {
        return this.noteDto.midiCode;
    }

    public void toggleSlection() {
        setSelected(!isSelected());
    }

    public String getNoteId(){
        return noteDto.id;
    }

    private static class NoteRectangleMovedEvent {
        String noteId;
        double delta;
        public NoteRectangleMovedEvent(String noteId, double delta) {
            this.noteId = noteId;
            this.delta = delta;
        }
    }

    @Subscribe
    private void handleNoteMovedEvent(NoteRectangleMovedEvent event){
        if(this.selected && !this.noteDto.id.equals(event.noteId)){
            this.setX(getX() + event.delta);
        }
    }
}
