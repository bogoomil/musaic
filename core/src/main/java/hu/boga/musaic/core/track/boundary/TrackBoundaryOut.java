package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

public interface TrackBoundaryOut {
    void setTrackDto(TrackDto trackDto, int resolution);
}
