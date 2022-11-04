package hu.boga.musaic.core;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySequenceModellStoreTest {

    public static final String NON_EXISTING_TRACK_ID = "nonExistingTrackId";
    private SequenceModell modell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        InMemorySequenceModellStore.SEQUENCE_MODELS.clear();

        modell = new SequenceModell();
        trackModell = new TrackModell();
        modell.tracks.add(trackModell);
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void getSequenceByTrackId() {
        assertEquals(modell, InMemorySequenceModellStore.getSequenceByTrackId(trackModell.getId()).get());
        assertTrue(InMemorySequenceModellStore.getSequenceByTrackId(NON_EXISTING_TRACK_ID).isEmpty());
    }

    @Test
    void getSequenceById(){
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        assertEquals(modell, InMemorySequenceModellStore.getSequenceById(modell.getId()));
    }

    @Test
    void clear(){
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
        InMemorySequenceModellStore.clear();
        assertTrue(InMemorySequenceModellStore.SEQUENCE_MODELS.isEmpty());
    }

    @Test
    void getTrackById(){
        assertEquals(trackModell, InMemorySequenceModellStore.getTrackById(trackModell.getId()).get());
    }

    @Test
    void getTrackByNoteId(){
        NoteModell note = new NoteModell(12, 1,512,100,0);
        trackModell.notes.add(note);

        assertEquals(trackModell, InMemorySequenceModellStore.getTrackByNoteId(note.getId()).get());
        assertTrue(InMemorySequenceModellStore.getTrackByNoteId("Zerge").isEmpty());
    }
}