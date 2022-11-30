package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

public interface TrackPropertiesBoundaryIn {
    void load(String trackId);
    void updateTrackChannel(String trackId, int channel);
    void updateTrackName(TrackDto trackDto);
    void setMuted(String trackId, boolean muted);
    void updateVolume(String trackId, double velocityPercent);
}
