package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

import static org.junit.jupiter.api.Assertions.*;

class SequenceToModellConverterTest {

    public static final int TEMPO = 120;
    SequenceToModellConverter converter;
    private Sequence sequence;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        sequence = new Sequence(0.0f, 128);
        sequence.createTrack();
        TempoUtil.addTempoEvents(sequence, TEMPO);
        converter = new SequenceToModellConverter(sequence);
    }

    @Test
    void convert() {
        SequenceModell model = converter.convert();
        assertEquals(model.resolution, sequence.getResolution());
        assertEquals(model.division, sequence.getDivisionType());
        assertEquals(model.tempo, TEMPO);
    }


}