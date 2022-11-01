package hu.boga.musaic.gui.trackeditor.events;

public class ProgramChangedEvent {
    String trackId;
    int program;
    int channel;

    public ProgramChangedEvent(final String trackId, final int program, final int channel) {
        this.trackId = trackId;
        this.program = program;
        this.channel = channel;
    }

    public String getTrackId() {
        return trackId;
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
                "trackId=" + trackId +
                ", program=" + program +
                ", channel=" + channel +
                '}';
    }
}
