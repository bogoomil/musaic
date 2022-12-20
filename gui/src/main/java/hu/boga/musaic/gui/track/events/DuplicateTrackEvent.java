package hu.boga.musaic.gui.track.events;

public class DuplicateTrackEvent {
    private String id;

    public DuplicateTrackEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
