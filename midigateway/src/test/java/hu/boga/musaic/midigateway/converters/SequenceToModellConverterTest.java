package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.sequence.SequenceGatewayImpl;
import hu.boga.musaic.midigateway.sequence.SequenceReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Sequence;

import static org.junit.jupiter.api.Assertions.*;

class SequenceToModellConverterTest {

    private static final String PATH = "src/main/resources/phrygian.mid";

    @Test
    void convert() {
        SequenceModell modell = new SequenceReader().open(PATH);
        assertEquals(0, modell.tempo);
        assertEquals(1, modell.tracks.size());
        assertEquals(128, modell.resolution);
        assertEquals(0, modell.division);

    }
}