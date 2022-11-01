package hu.boga.musaic.core;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySequenceModellStoreTest {

    private SequenceModell modell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        modell = new SequenceModell();
        trackModell = new TrackModell();

        modell.tracks.add(trackModell);
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void getSequenceIdByTrackId() {
        assertEquals(modell.getId(), InMemorySequenceModellStore.getSequenceIdByTrackId(trackModell.getId()));
    }

    @Test
    void getSequenceById(){
        SequenceModell modell = new SequenceModell();
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        assertEquals(modell, InMemorySequenceModellStore.getSequenceById(modell.getId()));
    }

    @Test
    void clear(){
        SequenceModell modell = new SequenceModell();
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        InMemorySequenceModellStore.clear();

        assertTrue(InMemorySequenceModellStore.SEQUENCE_MODELS.isEmpty());
    }
}