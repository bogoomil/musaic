package hu.boga.musaic.gui.trackeditor.events;

import java.util.UUID;

public class NoteDraggingEvent {
    UUID noteId;
    double delta;

    public NoteDraggingEvent(final UUID noteId, final double delta) {
        this.noteId = noteId;
        this.delta = delta;
    }

    public UUID getNoteId() {
        return this.noteId;
    }

    public double getDelta() {
        return this.delta;
    }
}
