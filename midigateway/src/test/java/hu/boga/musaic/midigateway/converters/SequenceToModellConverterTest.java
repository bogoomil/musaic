package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.Player;
import hu.boga.musaic.midigateway.sequence.SequenceGatewayImpl;
import hu.boga.musaic.midigateway.sequence.SequenceReader;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.sound.midi.Sequence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

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

    @Test
    void getTempo(){
        try (MockedStatic<TempoUtil> mockedStatic = Mockito.mockStatic(TempoUtil.class)) {
            SequenceModell modell = new SequenceReader().open(PATH);
            mockedStatic.verify(() -> TempoUtil.getTempo(Mockito.any()), times(1));
        }
    }
}