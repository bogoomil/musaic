package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.gateway.MidiGateway;
import hu.boga.musaic.core.sequence.interactor.converters.SequenceModellToDtoConverter;
import hu.boga.musaic.core.track.interactor.converters.TrackModelltoDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SequenceInteractor implements SequenceBoundaryIn {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceInteractor.class);

    private final SequenceBoundaryOut boundaryOut;
    private final MidiGateway gateway;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut, MidiGateway gateway) {
        this.boundaryOut = boundaryOut;
        this.gateway = gateway;
    }

    @Override
    public void create() {
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(createNewSequence()).convert());
    }

    @Override
    public void load(String id) {
        SequenceDto dto = getModell(id).map(modell -> new SequenceModellToDtoConverter(modell).convert()).orElseThrow();
        boundaryOut.displaySequence(dto);
    }

    @Override
    public void open(String path){
        SequenceModell sequenceModell = gateway.open(path);
        LOG.debug("opening sequence modell: {}", sequenceModell);
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(sequenceModell.getId(), sequenceModell);
        SequenceDto dto = new SequenceModellToDtoConverter(sequenceModell).convert();
        boundaryOut.displaySequence(dto);
    }

    @Override
    public void play(String sequenceId) {
        gateway.play(sequenceId);
    }

    @Override
    public void save(String sequenceId, String path) {
        gateway.save(sequenceId, path);
    }

    @Override
    public void setTempo(String sequenceId, int tempo) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        modell.tempo = tempo;
        gateway.updateTempo(modell);
    }

    @Override
    public void addTrack(String sequenceId) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceId);
        TrackModell trackModell = new TrackModell();
        modell.tracks.add(trackModell);
        gateway.addTrack(modell);
        boundaryOut.displayNewTrack(new TrackModelltoDtoConverter(trackModell).convert());
    }

    @Override
    public void stop() {
        gateway.stop();

    }

    @Override
    public void reloadSequence(SequenceDto sequenceDto) {
        SequenceModell modell = InMemorySequenceModellStore.SEQUENCE_MODELS.get(sequenceDto.id);
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }

    @Override
    public void updateTrackProgram(String trackId, int program, int channel) {
        LOG.debug("updating track: {} program: {} channel: {}", trackId, program, channel);
    }

    private SequenceModell createNewSequence(){
        SequenceModell modell = new SequenceModell();
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        gateway.initMidiSequence(modell);
        return modell;
    }

    private Optional<SequenceModell> getModell(String id){
        if(InMemorySequenceModellStore.SEQUENCE_MODELS.containsKey(id)) {
            return Optional.of(InMemorySequenceModellStore.SEQUENCE_MODELS.get(id));
        }
        return Optional.empty();
    }
}
