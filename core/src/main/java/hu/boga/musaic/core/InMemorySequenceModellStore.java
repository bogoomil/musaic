package hu.boga.musaic.core;

import hu.boga.musaic.core.modell.SequenceModell;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class InMemorySequenceModellStore {
    public static final Map<String, SequenceModell> SEQUENCE_MODELS = new HashMap<>();

    public static void clear(){
        SEQUENCE_MODELS.clear();
    }

    public static SequenceModell getSequenceById(String sequenceId){
        return SEQUENCE_MODELS.get(sequenceId);
    }

    public static String getSequenceIdByTrackId(String trackId){
        AtomicReference<String> retVal = new AtomicReference<>();
        SEQUENCE_MODELS.entrySet().forEach((entry) -> {
            entry.getValue().tracks.forEach(trackModell -> {
                if(trackId.equals(trackModell.getId())){
                    retVal.set(entry.getKey());
                }
            });
        });
        return retVal.get();
    }
}
