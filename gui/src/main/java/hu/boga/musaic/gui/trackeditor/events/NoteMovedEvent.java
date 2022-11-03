package hu.boga.musaic.gui.trackeditor.events;

public class NoteMovedEvent {

    String id;

    public NoteMovedEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
