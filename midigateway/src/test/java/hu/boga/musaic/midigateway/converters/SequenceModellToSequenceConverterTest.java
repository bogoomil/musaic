package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

import static org.junit.jupiter.api.Assertions.*;

class SequenceModellToSequenceConverterTest {

    private SequenceModell sequenceModell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        sequenceModell = new SequenceModell();
        sequenceModell.division = SequenceModell.DEFAULT_DIVISION;
        sequenceModell.resolution = SequenceModell.DEFAULT_RESOLUTION;
        trackModell = new TrackModell();
        trackModell.program = 12;
        trackModell.channel = 3;
        trackModell.name = "teszt";
        sequenceModell.tracks.add(trackModell);

        NoteModell noteModell = new NoteModell(12,1,512,100,3);
        trackModell.notes.add(noteModell);
    }

    @Test
    void convert() throws InvalidMidiDataException {
        Sequence sequence = new SequenceModellToSequenceConverter(sequenceModell).convert();
        assertEquals(1, sequence.getTracks().length);
        assertEquals(0, sequence.getDivisionType());
        assertEquals(128, sequence.getResolution());
        assertEquals(120, TempoUtil.getTempo(sequence));
        assertEquals("teszt", TrackUtil.getTrackName(sequence.getTracks()[0]).get());
        assertEquals(12, TrackUtil.getProgram(sequence.getTracks()[0]).get());
        assertEquals(3, TrackUtil.getChannel(sequence.getTracks()[0]).get());

        assertFalse(NoteUtil.getNoteOnEvents(sequence.getTracks()[0]).isEmpty());
    }
}