package hu.boga.musaic.core;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class InMemorySequenceModellStore {
    public static final Map<String, SequenceModell> SEQUENCE_MODELS = new HashMap<>();

    public static void clear(){
        SEQUENCE_MODELS.clear();
    }

    public static SequenceModell getSequenceById(String sequenceId){
        return SEQUENCE_MODELS.get(sequenceId);
    }

    public static Optional<SequenceModell> getSequenceByTrackId(String trackId){
        for(SequenceModell sequenceModell : SEQUENCE_MODELS.values()){
            for(TrackModell trackModell : sequenceModell.tracks){
                if(trackId.equals(trackModell.getId())){
                    return Optional.of(sequenceModell);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<TrackModell> getTrackById(String trackId){
        for(SequenceModell sequenceModell : SEQUENCE_MODELS.values()){
            for(TrackModell trackModell : sequenceModell.tracks){
                if(trackId.equals(trackModell.getId())){
                    return Optional.of(trackModell);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<TrackModell> getTrackByNoteId(String noteId){
        for (SequenceModell sm : SEQUENCE_MODELS.values()) {
            for (TrackModell trackModell : sm.tracks) {
                if (trackModell.gtNoteModellById(noteId).isPresent()) {
                    return Optional.of(trackModell);
                }
            }
        }
        return Optional.empty();
    }
}
