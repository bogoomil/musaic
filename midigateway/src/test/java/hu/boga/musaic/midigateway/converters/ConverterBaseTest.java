package hu.boga.musaic.midigateway.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterBaseTest {
    public static final float DIVISION_TYPE = 0.0f;
    public static final int RESOLUTION = 128;
    Sequence sequence;
    Track track;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        sequence = new Sequence(DIVISION_TYPE, RESOLUTION);
        track = sequence.createTrack();
    }

    @Test
    void setUpTest(){
        assertEquals(1, sequence.getTracks().length);
    }

}
