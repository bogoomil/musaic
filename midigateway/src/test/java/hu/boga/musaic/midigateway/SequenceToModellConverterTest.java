package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.midigateway.converters.SequenceToModellConverter;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;

import static org.junit.jupiter.api.Assertions.*;

class SequenceToModellConverterTest extends ConverterBaseTest{

    public static final int TEMPO = 120;
    SequenceToModellConverter converter;


    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        super.setUp();
        TempoUtil.addTempoEvents(sequence, TEMPO);
        converter = new SequenceToModellConverter(sequence);
    }


    @Test
    void convert() {
        SequenceModell model = converter.convert();
        assertEquals(model.resolution, sequence.getResolution());
        assertEquals(model.division, sequence.getDivisionType());
        assertEquals(TEMPO, model.tempo);
    }

}