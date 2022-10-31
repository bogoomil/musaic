package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.gateway.MidiGateway;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MidiGatewayImplTest {

    private static final String PATH = "/home/kunb/git/musaic/midigateway/src/main/resources/phrygian.mid";

    public static final float INVALID_DIVISION_TYPE = 0.123445f;
    private EasyRandom easyRandom;
    private MidiGateway midiGateway;
    private SequenceModell sequenceModell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        easyRandom = new EasyRandom();
        sequenceModell = easyRandom.nextObject(SequenceModell.class);
        sequenceModell.division = SequenceModell.DEFAULT_DIVISION;
        sequenceModell.resolution = SequenceModell.DEFAULT_RESOLUTION;
        trackModell = easyRandom.nextObject(TrackModell.class);
        sequenceModell.tracks.add(trackModell);
        midiGateway = new MidiGatewayImpl();
    }

    @Test
    void initMidiSequence() {
        midiGateway.initMidiSequence(sequenceModell);
        assertTrue(MidiGatewayImpl.SEQUENCE_MAP.containsKey(sequenceModell.getId()));
        assertTrue(MidiGatewayImpl.TRACK_MAP.containsKey(trackModell.getId()));
    }

    @Test
    void initMidiSequenceNegativePath() {
        sequenceModell.division = INVALID_DIVISION_TYPE;
        Assertions.assertThrows(MusaicException.class, () -> midiGateway.initMidiSequence(sequenceModell));
    }

    @Test
    void open(){
        SequenceModell model = midiGateway.open(PATH);
        assertNotNull(model);
        assertEquals(1, model.tracks.size());
        assertEquals(3, model.tracks.get(0).notes.size());
    }
    @Test
    void openNegativePath(){
        assertThrows(MusaicException.class, () ->  midiGateway.open(""));
    }
}