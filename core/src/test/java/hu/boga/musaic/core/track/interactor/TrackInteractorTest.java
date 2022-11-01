package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
}