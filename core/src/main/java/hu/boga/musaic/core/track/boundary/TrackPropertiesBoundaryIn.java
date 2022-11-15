package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

public interface TrackPropertiesBoundaryIn {
    void updateTrackChannel(String trackId, int channel);
    void updateTrackName(TrackDto trackDto);
    void setMuted(String trackId, boolean muted);
    void removeTrack(String trackId);
}
