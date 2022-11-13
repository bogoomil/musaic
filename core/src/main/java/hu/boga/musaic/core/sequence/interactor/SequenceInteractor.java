package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.sequence.interactor.converters.SequenceModellToDtoConverter;
import hu.boga.musaic.core.track.interactor.converters.TrackModelltoDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

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
        gateway.save(InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId), path);
    }

    @Override
    public void setTempo(String sequenceId, int tempo) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        modell.tempo = tempo;
    }

    @Override
    public void addTrack(String sequenceId) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        TrackModell trackModell = new TrackModell();
        modell.tracks.add(trackModell);
        boundaryOut.displayNewTrack(new TrackModelltoDtoConverter(trackModell).convert());
    }

    @Override
    public void stop() {
        gateway.stop();
    }

    @Override
    public void reloadSequence(String sequenceId) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    @Override
    public void updateChannelColorMapping(String sequenceId, int channel, String color) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        modell.channelToColorMapping[channel] = color;
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    @Override
    public void updateChannelToProgramMappings(String id, int i, int selectedProgram) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(id);
        modell.updateChannelToProgramMapping(i, selectedProgram);
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    private SequenceModell createNewSequence(){
        SequenceModell modell = new SequenceModell();
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        return modell;
    }

}
