package hu.boga.musaic.gui.trackeditor.events;

public class DeleteNoteEvent {
    double tick;
    double pitch;

    public DeleteNoteEvent(final double tick, final double pitch) {
        this.tick = tick;
        this.pitch = pitch;
    }

    public double getTick() {
        return this.tick;
    }

    public double getPitch() {
        return this.pitch;
    }
}
