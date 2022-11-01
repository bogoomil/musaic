package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

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

    @Override
    public void removeTrack(String trackId) {
        String seqId = InMemorySequenceModellStore.getSequenceIdByTrackId(trackId);
        SequenceModell modell = InMemorySequenceModellStore.getSequenceById(seqId);
        modell.tracks.removeIf(trackModell -> trackModell.getId().equals(trackId));
        gateway.removeTrack(seqId, trackId);
    }
}
