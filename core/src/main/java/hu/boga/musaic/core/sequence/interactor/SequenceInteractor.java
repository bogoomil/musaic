package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.gateway.MidiGateway;
import hu.boga.musaic.core.sequence.interactor.converters.SequenceModellToDtoConverter;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SequenceInteractor implements SequenceBoundaryIn {

    private static final Map<String, SequenceModell> SEQUENCE_MODELS = new HashMap<>();

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
        SequenceDto dto = new SequenceModellToDtoConverter(gateway.open(path)).convert();
        boundaryOut.displaySequence(dto);
    }

    @Override
    public void play(String sequenceId) {
        gateway.play(sequenceId);
    }

    private SequenceModell createNewSequence(){
        SequenceModell modell = new SequenceModell();
        SEQUENCE_MODELS.put(modell.getId(), modell);
        gateway.initMidiSequence(modell);
        return modell;
    }

    private Optional<SequenceModell> getModell(String id){
        if(SEQUENCE_MODELS.containsKey(id)) {
            return Optional.of(SEQUENCE_MODELS.get(id));
        }
        return Optional.empty();
    }
}
