package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.midigateway.converters.ConverterBaseTest;
import hu.boga.musaic.midigateway.converters.NoteToModellConverter;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteToModellConverterTest extends ConverterBaseTest {

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        super.setUp();
        NoteUtil.addNote(track, 10, 12, 32, 127, 0);
    }

    @Test
    void convert() {
        List<NoteModell> notes = new NoteToModellConverter(track).convert();
        assertEquals(1, notes.size());
        assertEquals(10, notes.get(0).tick);
        assertEquals(12, notes.get(0).midiCode);
        assertEquals(32, notes.get(0).length);
        assertEquals(1, notes.get(0).velocity);
    }
}