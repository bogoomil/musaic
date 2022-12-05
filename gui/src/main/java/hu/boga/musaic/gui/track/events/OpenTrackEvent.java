package hu.boga.musaic.gui.track.events;

public class OpenTrackEvent {
    String trackId;

    public OpenTrackEvent(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackId() {
        return trackId;
    }
}
