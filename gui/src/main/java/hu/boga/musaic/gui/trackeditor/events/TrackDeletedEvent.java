package hu.boga.musaic.gui.trackeditor.events;

public class TrackDeletedEvent {
    private final String trackId;
    public TrackDeletedEvent(String id) {
        this.trackId = id;
    }

    public String getTrackId() {
        return trackId;
    }
}
