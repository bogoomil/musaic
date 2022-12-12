package hu.boga.musaic.gui.track.events;

public class TrackEditingFinishedEvent {
    private String trackId;

    public TrackEditingFinishedEvent(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackId() {
        return trackId;
    }
}
