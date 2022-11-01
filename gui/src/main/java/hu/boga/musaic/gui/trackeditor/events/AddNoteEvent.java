package hu.boga.musaic.gui.trackeditor.events;

public class AddNoteEvent {
    int tick;
    int pitch;
    int length;

    public AddNoteEvent(final int tick, final int pitch, int length) {
        this.tick = tick;
        this.pitch = pitch;
        this.length = length;
    }

    public int getTick() {
        return this.tick;
    }

    public int getPitch() {
        return this.pitch;
    }

    public int getLength() {
        return length;
    }
}
