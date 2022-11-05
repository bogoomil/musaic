package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.trackeditor.events.NoteMovedEvent;
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
        this.xProperty().addListener((observable, oldValue, newValue) -> xPropertyChanged());
    }

    private void xPropertyChanged() {
        if(getX() < 0){
            setX(0);
        }
        LOG.debug("Posting NoteMovedEvent {}", noteDto.id );
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
}
