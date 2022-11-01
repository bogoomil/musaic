package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.TrackToModellConverter;
import hu.boga.musaic.midigateway.utils.MidiUtilBaseTest;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import static org.junit.jupiter.api.Assertions.*;

class TrackToModellConverterTest extends ConverterBaseTest {

    public static final String TRACK_NAME = "zergegenny";

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        super.setUp();
        TrackUtil.updateTrackName(track, TRACK_NAME);
        TrackUtil.addProgramChangeEvent(track, 1, 2, 0);
    }

    @Test
    void convert() {
        TrackModell modell = new TrackToModellConverter(track).convert();
        assertEquals(TRACK_NAME, modell.name);
        assertEquals(1, modell.channel);
        assertEquals(2, modell.program);
    }

//    @Test
//    void convertNegativePath(){
//        TrackUtil.addProgramChangeEvent(track, 2,3,1);
//        TrackToModellConverter converter = new TrackToModellConverter(track);
//        assertThrows(MusaicException.class, () -> converter.convert());
//    }
}