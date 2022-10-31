package hu.boga.musaic.midigateway.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;

import static org.junit.jupiter.api.Assertions.*;

class TempoUtilTest extends MidiUtilBaseTest{

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        super.setUp();
        TempoUtil.addTempoEvents(sequence, 120);
    }

    @Test
    void addTempoEvents() {
        assertEquals(120, TempoUtil.getTempo(sequence));
    }

    @Test
    void removeTempoEvents() {
        TempoUtil.addTempoEvents(sequence, 120);
        TempoUtil.removeTempoEvents(sequence);
        assertEquals(0, TempoUtil.getTempo(sequence));
    }

    @Test
    void getTempo() {
        assertEquals(120, TempoUtil.getTempo(sequence));
    }
}