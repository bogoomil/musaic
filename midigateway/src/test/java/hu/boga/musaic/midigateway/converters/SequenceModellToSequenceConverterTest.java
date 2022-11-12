package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.MetaMessageEventModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.ShortMessageEventModell;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

import java.nio.charset.StandardCharsets;

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
        trackModell.channel = 3;
        trackModell.setName("teszt");
        sequenceModell.tracks.add(trackModell);

        NoteModell noteModell = new NoteModell(12,1,512,100,3);
        trackModell.eventModells.add(noteModell);

        MetaMessageEventModell mm = new MetaMessageEventModell(0, "fing".getBytes(StandardCharsets.UTF_8), CommandEnum.TRACK_NAME);
        trackModell.eventModells.add(mm);
    }

    @Test
    void convert() throws InvalidMidiDataException {
        Sequence sequence = new SequenceModellToSequenceConverter(sequenceModell).convert();
        assertEquals(2, sequence.getTracks().length);
        assertEquals(0, sequence.getDivisionType());
        assertEquals(128, sequence.getResolution());
        assertEquals(120, TempoUtil.getTempo(sequence));
        assertEquals(2, sequence.getTracks()[0].size());
//        assertFalse(NoteUtil.getNoteOnEvents(sequence.getTracks()[0]).isEmpty());
    }
}