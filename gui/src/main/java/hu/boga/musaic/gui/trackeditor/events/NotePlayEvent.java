package hu.boga.musaic.gui.trackeditor.events;

public class NotePlayEvent {
    public int midiCode;
    public int lengthInTicks;

    public NotePlayEvent(int midiCode, int lengthInTicks) {
        this.midiCode = midiCode;
        this.lengthInTicks = lengthInTicks;
    }
}
