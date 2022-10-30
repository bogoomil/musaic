package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.junit.jupiter.api.BeforeEach;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class MidiUtilBaseTest {

    Sequence sequence;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        sequence = new Sequence(0.0f, 128);
        sequence.createTrack();
        TempoUtil.addTempoEvents(sequence, 120);
    }

}
