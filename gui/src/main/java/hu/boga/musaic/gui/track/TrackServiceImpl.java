package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

import javax.inject.Inject;

public class TrackServiceImpl implements TrackService, TrackPropertiesBoundaryOut {

    TrackPropertiesBoundaryIn boundaryIn;
    TrackDto dto;

    @Inject
    public TrackServiceImpl(TrackPropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displayTrack(TrackDto trackDto) {
        this.dto = trackDto;
    }

    @Override
    public TrackModell getModell() {
        return new TrackModell(dto);
    }
}
