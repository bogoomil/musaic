package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.core.exceptions.MusaicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
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

        NoteUtil.addNote(track, 340, 23, 12, 100, 0);
        assertEquals(352, NoteUtil.getNoteOnOffPair(track, 340, 23).noteOff.getTick());
    }

    @Test
    void deleteNote() {
        assertEquals(1, NoteUtil.getNoteOffEvents(track).size());
        NoteUtil.deleteNote(track, 10, 12);
        assertEquals(0, NoteUtil.getNoteOnEvents(track).size());
        assertEquals(0, NoteUtil.getNoteOffEvents(track).size());
    }

//    @Test
//    void findMatchingNoteOff(){
//        MidiUtil.removeEvents(track, MidiUtil.getMidiEventsByShortMessageCommand(track, ShortMessage.NOTE_OFF));
//        assertThrows(MusaicException.class, () -> NoteUtil.getNoteOnOffPair(track, 10,12));
//    }
}