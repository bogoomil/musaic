package hu.boga.musaic.midigateway.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import static org.junit.jupiter.api.Assertions.*;

class NoteUtilTest extends MidiUtilBaseTest {

    Track track;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        super.setUp();
        track = sequence.getTracks()[0];
        NoteUtil.addNote(track, 10, 12, 100, 100, 0);
    }

    @Test
    void addNote() {
        assertEquals(1, NoteUtil.getNoteOnEvents(track).size());
    }

    @Test
    void moveNote() {
        NoteUtil.moveNote(track, 10, 12, 100);
        assertEquals(1, NoteUtil.getNoteOnEvents(track).size());
        assertEquals(100, NoteUtil.getNoteOnEvents(track).get(0).getTick());
        assertNotNull(NoteUtil.getNoteOnOffPair(track, 100, 12).noteOff);
        assertEquals(200, NoteUtil.getNoteOnOffPair(track, 100, 12).noteOff.getTick());
    }

    @Test
    void deleteNote() {
        NoteUtil.deleteNote(track, 10, 12);
        assertEquals(0, NoteUtil.getNoteOnEvents(track).size());
    }
}