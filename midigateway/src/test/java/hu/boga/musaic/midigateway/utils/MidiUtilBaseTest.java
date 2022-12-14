package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MidiUtilBaseTest {

    Sequence sequence;
    Track track;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        sequence = new Sequence(0.0f, 128);
        track = sequence.createTrack();
    }

    @Test
    void setUpTests(){
        assertEquals(1, sequence.getTracks().length);
    }
}
