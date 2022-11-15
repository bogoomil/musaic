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
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            LOG.debug("updating track: {}, channel: {}", trackId, channel);
            trackModell.channel = channel;
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    @Override
    public void updateTrackName(TrackDto trackDto) {
        InMemorySequenceModellStore.getTrackById(trackDto.id).ifPresent(trackModell -> {
            LOG.debug("updating track name: {}", trackDto.name);
            trackModell.setName(trackDto.name);
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    @Override
    public void removeTrack(String trackId) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.tracks.removeIf(trackModell -> trackModell.getId().equals(trackId));
        });
    }

    @Override
    public void updateVolume(String trackId, double velocityPercent) {
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            trackModell.getNotes().forEach(noteModell -> noteModell.velocity = calcNewVelocity(noteModell.velocity, velocityPercent));
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    private double calcNewVelocity(double current, double percent){
        double newValue = current + percent;
        if(newValue > 1) newValue = 1;
        if (newValue < 0) newValue = 0;

        return newValue;
    }
}
