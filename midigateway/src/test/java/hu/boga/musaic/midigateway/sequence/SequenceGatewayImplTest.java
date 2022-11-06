package hu.boga.musaic.midigateway.sequence;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.Player;
import hu.boga.musaic.midigateway.Saver;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class SequenceGatewayImplTest {
    private static final String PATH = "src/main/resources/phrygian.mid";
    public static final String PATH_TO_SAVE = "src/main/resources/tmp.mid";

    private SequenceGateway gateway;
    private SequenceModell sequenceModell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        sequenceModell = new SequenceModell();
        sequenceModell.division = SequenceModell.DEFAULT_DIVISION;
        sequenceModell.resolution = SequenceModell.DEFAULT_RESOLUTION;
        trackModell = new TrackModell();
        trackModell.name = "teszt";
        sequenceModell.tracks.add(trackModell);

        gateway = new SequenceGatewayImpl();
    }

    @Test
    void open() {
        SequenceModell model = gateway.open(PATH);
        assertNotNull(model);
        assertEquals(1, model.tracks.size());
        assertEquals(3, model.tracks.get(0).notes.size());
    }

    @Test
    void openNegativePath() {
        assertThrows(MusaicException.class, () -> gateway.open(""));
    }

    @Test
    void save() {
        try (MockedStatic<Saver> mockedStatic = Mockito.mockStatic(Saver.class)) {
            gateway.save(sequenceModell, PATH_TO_SAVE);
            mockedStatic.verify(() -> Saver.save(Mockito.any(), eq(PATH_TO_SAVE)));
        }
    }

    @Test
    void play() {
        try (MockedStatic<Player> mockedStatic = Mockito.mockStatic(Player.class)) {
            gateway.play(sequenceModell);
            mockedStatic.verify(() -> Player.playSequence(Mockito.any()), times(1));
        }
    }

    @Test
    void stop() {
        SequenceModell model = gateway.open(PATH);
        try (MockedStatic<Player> mockedStatic = Mockito.mockStatic(Player.class)) {
            gateway.stop();
            mockedStatic.verify(() -> Player.stopPlayback(), times(1));
        }
    }
}