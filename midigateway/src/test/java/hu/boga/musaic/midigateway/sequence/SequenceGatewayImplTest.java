package hu.boga.musaic.midigateway.sequence;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.midigateway.Player;
import hu.boga.musaic.midigateway.Saver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class SequenceGatewayImplTest {
    private static final String PATH = "src/main/resources/phrygian.mid";
    public static final String PATH_TO_SAVE = "src/main/resources/tmp.mid";

    private SequenceGatewayImpl gateway;
    private SequenceModell sequenceModell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        sequenceModell = new SequenceModell();
        sequenceModell.division = SequenceModell.DEFAULT_DIVISION;
        sequenceModell.resolution = SequenceModell.DEFAULT_RESOLUTION;
        trackModell = new TrackModell();
        trackModell.setName("teszt");
        trackModell.eventModells.add(new NoteModell(0, 0, 128, 1,3));
        sequenceModell.tracks.add(trackModell);

        gateway = new SequenceGatewayImpl();

        gateway.save(sequenceModell, PATH_TO_SAVE);
    }

    @Test
    void open() {
        SequenceModell model = gateway.open(PATH_TO_SAVE);
        assertNotNull(model);
        assertEquals(1, model.tracks.size());
        assertEquals(4, model.tracks.get(0).eventModells.size());
        assertEquals(0, model.tracks.get(0).channel);
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
            gateway.play(sequenceModell, 0, 1);
            mockedStatic.verify(() -> Player.playSequence(Mockito.any(), eq(0L), eq(1L)), times(1));
            mockedStatic.verify(() -> Player.removeMetaEventListener(Mockito.any()), times(1));
            mockedStatic.verify(() -> Player.createMetaEventListener(eq(sequenceModell)), times(1));
        }
    }

    @Test
    void play_negative(){
        sequenceModell.tracks.get(0).eventModells.add(new NoteModell(12, 0, 128, 1000, 123));
        assertThrows(MusaicException.class, () -> gateway.play(sequenceModell, 0, 1));
    }

    @Test
    void save_negative(){
        sequenceModell.tracks.get(0).eventModells.add(new NoteModell(12, 0, 128, 1000, 123));
        assertThrows(MusaicException.class, () -> gateway.save(sequenceModell, ""));
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
