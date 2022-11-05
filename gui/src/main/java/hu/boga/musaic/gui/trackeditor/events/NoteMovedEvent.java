package hu.boga.musaic.gui.trackeditor.events;

public class NoteMovedEvent {
    String id;
    int newTick;

    public NoteMovedEvent(String id, int newTick) {
        this.id = id;
        this.newTick = newTick;
    }

    public String getId() {
        return id;
    }

    public int getNewTick() {
        return newTick;
    }
}
