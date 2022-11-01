package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.musictheory.enums.ChordType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

class TrackInteractorTest {

    public static final String NEW_NAME = "NEW_NAME";
    public static final String TRACK_ID = "TRACK_ID";
    TrackInteractor trackInteractor;
    private TrackGateway gateway;
    private SequenceModell modell;
    private TrackModell trackModell;
    private TrackBoundaryOut boundaryOut;


    @BeforeEach
    void setUp() {

        InMemorySequenceModellStore.clear();

        boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        TrackBoundaryIn boundaryIn = Mockito.mock(TrackBoundaryIn.class);
        gateway = Mockito.mock(TrackGateway.class);

        trackInteractor = new TrackInteractor(gateway, boundaryOut);

        modell = new SequenceModell();
        trackModell = new TrackModell();
        modell.tracks.add(trackModell);

        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void updateTrackName() {
        TrackDto dto = new TrackDto();
        dto.id = TRACK_ID;
        dto.name = NEW_NAME;
        trackInteractor.updateTrackName(dto);

        Mockito.verify(gateway).updateTrackName(dto.id, dto.name);

    }

    @Test
    void removeTrack(){
        modell.tracks.add(new TrackModell());

        try (MockedStatic<InMemorySequenceModellStore> mockedStatic = Mockito.mockStatic(InMemorySequenceModellStore.class)) {
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceIdByTrackId(trackModell.getId())).thenReturn(modell.getId());
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceById(modell.getId())).thenReturn(modell);
            trackInteractor.removeTrack(trackModell.getId());
            Mockito.verify(gateway).removeTrack(modell.getId(), trackModell.getId());

            assertEquals(1, modell.tracks.size());
        }
    }

    @Test
    void updateTrackProgram(){
        trackInteractor.updateTrackProgram(TRACK_ID, 0,0);
        Mockito.verify(gateway).updateTrackProgram(TRACK_ID, 0, 0);
    }

    @Test
    void addSingleNote(){
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, 0, null);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).setTrackDto(captor.capture(), eq(modell.resolution));
        assertEquals(1, captor.getValue().notes.size());
        assertEquals(512, captor.getValue().notes.get(0).length);
    }
    @Test
    void addChord(){
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, 0, ChordType.MAJ);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).setTrackDto(captor.capture(), eq(modell.resolution));
        Mockito.verify(gateway).addNotesToTrack(eq(trackModell.getId()), Mockito.any());
        assertEquals(3, captor.getValue().notes.size());

    }
}