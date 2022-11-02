package hu.boga.musaic.core;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InMemorySequenceModellStore {
    public static final Map<String, SequenceModell> SEQUENCE_MODELS = new HashMap<>();

    public static void clear(){
        SEQUENCE_MODELS.clear();
    }

    public static SequenceModell getSequenceById(String sequenceId){
        return SEQUENCE_MODELS.get(sequenceId);
    }

    public static Optional<SequenceModell> getSequenceByTrackId(String trackId){
        return SEQUENCE_MODELS.values().stream().filter(sequenceModell -> sequenceModell.getTrackById(trackId).isPresent()).findFirst();
    }

    public static Optional<TrackModell> getTrackById(String trackId){
        return SEQUENCE_MODELS
                .values()
                .stream()
                .map(sequenceModell -> sequenceModell.getTrackById(trackId)).findFirst().get();
    }
}
