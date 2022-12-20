package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.EventModell;
import hu.boga.musaic.midigateway.utils.MidiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShortMessageEventToModellConverterTest {

    private Collection<? extends EventModell> list;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 128);
        Track track = sequence.createTrack();

        MidiEvent event = MidiUtil.createMidiEventShortMessage(1, CommandEnum.PROGRAM_CHANGE.getIntValue(), 0,1,1);
        track.add(event);
        event = MidiUtil.createMidiEventShortMessage(1, CommandEnum.NOTE_ON.getIntValue(), 0,1,1);
        track.add(event);
        event = MidiUtil.createMidiEventShortMessage(1, CommandEnum.NOTE_OFF.getIntValue(), 0,1,0);
        track.add(event);
        list = new ShortMessageEventToModellConverter(track).convert();
    }

    @Test
    void convert() {
        assertEquals(1, list.size());
    }
}