package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.shortThat;
import static org.mockito.Mockito.times;

class TrackGatewayImplTest {

    public static final String NEW_NAME = "newName";
    public static final String TRACK_ID = "trackId";
    public static final String SEQ_ID = "SEQID";
    private TrackGateway gateway;
    private Sequence modell;
    private Track track;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        InMemorySequenceStore.TRACK_MAP.clear();
        InMemorySequenceStore.SEQUENCE_MAP.clear();
        modell = new Sequence(Sequence.PPQ, 128);
        track = modell.createTrack();

        InMemorySequenceStore.SEQUENCE_MAP.put(SEQ_ID, modell);
        InMemorySequenceStore.TRACK_MAP.put(TRACK_ID, track);

        gateway = new TrackGatewayImpl();
    }

    @Test
    void updateTrackName() {
        try (MockedStatic<TrackUtil> mockedStatic = Mockito.mockStatic(TrackUtil.class)) {
            gateway.updateTrackName(TRACK_ID, NEW_NAME);
            mockedStatic.verify(() -> TrackUtil.updateTrackName(Mockito.any(), eq(NEW_NAME)), times(1));
        }
    }

    @Test
    void removeTrack() {
        gateway.removeTrack(SEQ_ID, TRACK_ID);
        assertEquals(0, modell.getTracks().length);
    }
}