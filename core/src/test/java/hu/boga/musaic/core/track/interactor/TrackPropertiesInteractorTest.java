package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class TrackPropertiesInteractorTest {
    public static final String NEW_NAME = "NEW_NAME";
    public static final String TRACK_ID = "TRACK_ID";
    TrackPropertiesInteractor trackInteractor;
    private SequenceModell modell;
    private TrackModell trackModell;
    private TrackPropertiesBoundaryOut boundaryOut;
    private NoteModell noteModell;


    @BeforeEach
    void setUp() {

        InMemorySequenceModellStore.clear();

        boundaryOut = Mockito.mock(TrackPropertiesBoundaryOut.class);
        TrackBoundaryIn boundaryIn = Mockito.mock(TrackBoundaryIn.class);

        trackInteractor = new TrackPropertiesInteractor(boundaryOut);

        modell = new SequenceModell();
        trackModell = new TrackModell();
        modell.tracks.add(trackModell);

        noteModell = new NoteModell(12, 0, 32, 100, 0);
        trackModell.eventModells.add(noteModell);

        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void setMuted(){
        trackInteractor.setMuted(trackModell.getId(), true);
        assertTrue(trackModell.muted);
    }

    @Test
    void updateTrackChannel() {
        trackInteractor.updateTrackChannel(trackModell.getId(), 4);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
        assertEquals(4, trackModell.channel);
    }


    @Test
    void updateTrackName() {
        TrackDto dto = new TrackDto();
        dto.id = trackModell.getId();
        dto.name = NEW_NAME;
        trackInteractor.updateTrackName(dto);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());

        assertEquals(NEW_NAME, trackModell.getName());

    }

    @Test
    void removeTrack() {
        try (MockedStatic<InMemorySequenceModellStore> mockedStatic = Mockito.mockStatic(InMemorySequenceModellStore.class)) {
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceByTrackId(trackModell.getId())).thenReturn(Optional.of(modell));
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceById(modell.getId())).thenReturn(modell);
            trackInteractor.removeTrack(trackModell.getId());

            assertEquals(1, modell.tracks.size());
        }
    }
}