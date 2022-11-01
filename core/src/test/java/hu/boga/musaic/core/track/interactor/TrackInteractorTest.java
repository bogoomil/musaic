package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

class TrackInteractorTest {

    public static final String NEW_NAME = "NEW_NAME";
    public static final String TRACK_ID = "TRACK_ID";
    TrackInteractor trackInteractor;
    private TrackGateway gateway;


    @BeforeEach
    void setUp() {
        TrackBoundaryOut boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        TrackBoundaryIn boundaryIn = Mockito.mock(TrackBoundaryIn.class);
        gateway = Mockito.mock(TrackGateway.class);

        trackInteractor = new TrackInteractor(gateway, boundaryOut);
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

        InMemorySequenceModellStore.clear();

        SequenceModell modell = new SequenceModell();
        TrackModell trackModell = new TrackModell();
        modell.tracks.add(trackModell);
        modell.tracks.add(new TrackModell());

        try (MockedStatic<InMemorySequenceModellStore> mockedStatic = Mockito.mockStatic(InMemorySequenceModellStore.class)) {
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceIdByTrackId(trackModell.getId())).thenReturn(modell.getId());
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceById(modell.getId())).thenReturn(modell);

            trackInteractor.removeTrack(trackModell.getId());
            Mockito.verify(gateway).removeTrack(modell.getId(), trackModell.getId());

            assertEquals(1, modell.tracks.size());
        }

    }
}