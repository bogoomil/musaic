package hu.boga.musaic.core.events;

public class TickEvent {
    String sequenceId;
    int tick;

    public TickEvent(String sequenceId, int tick) {
        this.sequenceId = sequenceId;
        this.tick = tick;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public int getTick() {
        return tick;
    }
}
