package hu.boga.musaic.gui.sequencemanager.components.track;

public class DuplicateTrackEvent {
    String trackId;

    public DuplicateTrackEvent(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackId() {
        return trackId;
    }

}
