package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.core.track.interactor.converters.TrackModelltoDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class TrackPropertiesInteractor implements TrackPropertiesBoundaryIn {
    private static final Logger LOG = LoggerFactory.getLogger(TrackPropertiesInteractor.class);

    private TrackPropertiesBoundaryOut boundaryOut;

    @Inject
    public TrackPropertiesInteractor(TrackPropertiesBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void setMuted(String id, boolean muted) {
        InMemorySequenceModellStore.getSequenceByTrackId(id).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(id).ifPresent(trackModell -> {
                trackModell.muted = muted;
            });
            LOG.debug("sequence to show: {}", sequenceModell);
        });
    }

    @Override
    public void updateTrackChannel(String trackId, int channel) {
        LOG.debug("updating track: {}, channel: {}", trackId, channel);
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            trackModell.channel = channel;
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });

//        showTrack(trackId);
    }

    @Override
    public void updateTrackName(TrackDto trackDto) {
        LOG.debug("updating track name: {}", trackDto.name);
        InMemorySequenceModellStore.getTrackById(trackDto.id).ifPresent(trackModell -> {
            trackModell.setName(trackDto.name);
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
//        showTrack(trackDto.id);
    }

    @Override
    public void removeTrack(String trackId) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.tracks.removeIf(trackModell -> trackModell.getId().equals(trackId));
        });
    }
}