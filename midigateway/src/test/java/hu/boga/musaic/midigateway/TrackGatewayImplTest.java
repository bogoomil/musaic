package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class TrackGatewayImplTest {

    public static final String NEW_NAME = "newName";
    public static final String TRACK_ID = "trackId";
    private TrackGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new TrackGatewayImpl();
    }

    @Test
    void updateTrackName() {
        try (MockedStatic<TrackUtil> mockedStatic = Mockito.mockStatic(TrackUtil.class)) {
            gateway.updateTrackName(TRACK_ID, NEW_NAME);
            mockedStatic.verify(() -> TrackUtil.updateTrackName(Mockito.any(), eq(NEW_NAME)), times(1));
        }
    }
}