package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.sequence.interactor.converters.SequenceModellToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SequenceInteractor implements SequenceBoundaryIn {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceInteractor.class);

    private final SequenceBoundaryOut boundaryOut;
    private final SequenceGateway gateway;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut, SequenceGateway gateway) {
        this.boundaryOut = boundaryOut;
        this.gateway = gateway;
    }

    @Override
    public void create() {
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(createNewSequence()).convert());
    }

    @Override
    public void load(String id) {
        SequenceDto dto = new SequenceModellToDtoConverter(InMemorySequenceModellStore.getSequenceById(id)).convert();
        boundaryOut.displaySequence(dto);
    }

    @Override
    public void open(String path){
        SequenceModell sequenceModell = gateway.open(path);
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(sequenceModell.getId(), sequenceModell);
        SequenceDto dto = new SequenceModellToDtoConverter(sequenceModell).convert();
        boundaryOut.displaySequence(dto);
    }

    @Override
    public void play(String sequenceId, long fromTick, long toTick) {
        gateway.play(InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId), fromTick, toTick);
    }

    @Override
    public void save(String sequenceId, String path) {
        LOG.debug("id: {}, path: {}", sequenceId, path);
        SequenceModell modell = InMemorySequenceModellStore.getSequenceById(sequenceId);
        gateway.save(modell, path);
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    @Override
    public void setTempo(String sequenceId, int tempo) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        modell.tempo = tempo;
    }

    @Override
    public void addTrack(String sequenceId) {
        LOG.debug("id: {}", sequenceId);
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        TrackModell trackModell = new TrackModell();
        modell.tracks.add(trackModell);
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    @Override
    public void stop() {
        gateway.stop();
    }

    @Override
    public void updateChannelToProgramMappings(String id, int channel, int selectedProgram) {
        LOG.debug("seq: {}, channel: {}, program: {}", id, channel, selectedProgram);
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(id);
        modell.updateChannelToProgramMapping(channel, selectedProgram);
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    @Override
    public void duplicateTrack(String trackId) {
        LOG.debug("track: {}", trackId);
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                TrackModell clone = trackModell.clone();
                sequenceModell.tracks.add(clone);
            });
            boundaryOut.displaySequence(new SequenceModellToDtoConverter(sequenceModell).convert());
        });
    }

    @Override
    public void removeTrack(String trackId) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.tracks.removeIf(trackModell -> trackModell.getId().equals(trackId));
            boundaryOut.displaySequence(new SequenceModellToDtoConverter(sequenceModell).convert());
        });
    }


    private SequenceModell createNewSequence(){
        SequenceModell modell = new SequenceModell();
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        return modell;
    }

}
