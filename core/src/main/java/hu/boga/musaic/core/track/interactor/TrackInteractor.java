package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;

import javax.inject.Inject;

public class TrackInteractor implements TrackBoundaryIn {

    TrackGateway gateway;
    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackGateway gateway, TrackBoundaryOut boundaryOut) {
        this.gateway = gateway;
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void updateTrackName(TrackDto trackDto) {
        gateway.updateTrackName(trackDto.id, trackDto.name);
    }
}
