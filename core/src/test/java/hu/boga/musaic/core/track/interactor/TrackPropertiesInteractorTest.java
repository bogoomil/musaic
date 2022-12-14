package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class TrackPropertiesInteractorTest {
    public static final String NEW_NAME = "NEW_NAME";
    public static final String TRACK_ID = "TRACK_ID";
    TrackInteractor trackPropertiesInteractor;
    private SequenceModell modell;
    private TrackModell trackModell;
    private TrackBoundaryOut boundaryOut;
    private NoteModell noteModell;


    @BeforeEach
    void setUp() {

        InMemorySequenceModellStore.clear();

        boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        TrackBoundaryIn boundaryIn = Mockito.mock(TrackBoundaryIn.class);

        trackPropertiesInteractor = new TrackInteractor(boundaryOut);

        modell = new SequenceModell();
        trackModell = new TrackModell();
        modell.tracks.add(trackModell);

        noteModell = new NoteModell(12, 0, 32, 1, 0);
        trackModell.eventModells.add(noteModell);

        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void setMuted(){
        trackPropertiesInteractor.setMuted(trackModell.getId(), true);
        assertTrue(trackModell.muted);
    }

    @Test
    void updateTrackChannel() {
        trackPropertiesInteractor.updateTrackChannel(trackModell.getId(), 4);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
        assertEquals(4, trackModell.channel);
    }


    @Test
    void updateTrackName() {
        TrackDto dto = new TrackDto();
        dto.id = trackModell.getId();
        dto.name = NEW_NAME;
        trackPropertiesInteractor.updateTrackName(dto);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());

        assertEquals(NEW_NAME, trackModell.getName());

    }

    @Test
    void updateVolume(){
        trackPropertiesInteractor.updateVolume(trackModell.getId(), -0.5);
        assertEquals(0.5, noteModell.velocity);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());

        trackPropertiesInteractor.updateVolume(trackModell.getId(), 0.5);
        assertEquals(1, noteModell.velocity);

        trackPropertiesInteractor.updateVolume(trackModell.getId(), -0.5);
        assertEquals(0.5, noteModell.velocity);

        trackPropertiesInteractor.updateVolume(trackModell.getId(), 0.5);
        assertEquals(1, noteModell.velocity);

        trackPropertiesInteractor.updateVolume(trackModell.getId(), -1);
        assertEquals(0, noteModell.velocity);

        trackPropertiesInteractor.updateVolume(trackModell.getId(), 0.5);
        assertEquals(0.5, noteModell.velocity);

        trackPropertiesInteractor.updateVolume(trackModell.getId(), 0.5);
        assertEquals(1, noteModell.velocity);
    }

    @Test
    void load(){
        trackPropertiesInteractor.load(trackModell.getId());
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
    }
}