package hu.boga.musaic.gui.trackeditor.events;

public class ProgramChangedEvent {
    int trackIndex;
    int program;
    int channel;

    public ProgramChangedEvent(final int trackIndex, final int program, final int channel) {
        this.trackIndex = trackIndex;
        this.program = program;
        this.channel = channel;
    }

    public int getTrackIndex() {
        return this.trackIndex;
    }

    public int getProgram() {
        return this.program;
    }

    public int getChannel() {
        return this.channel;
    }

    @Override
    public String toString() {
        return "ProgramChangedEvent{" +
                "trackIndex=" + trackIndex +
                ", program=" + program +
                ", channel=" + channel +
                '}';
    }
}
