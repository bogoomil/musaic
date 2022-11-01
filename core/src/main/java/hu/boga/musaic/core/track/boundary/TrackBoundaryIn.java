package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

public interface TrackBoundaryIn {
    void updateTrackName(TrackDto trackDto);

    void removeTrack(String id);
}
