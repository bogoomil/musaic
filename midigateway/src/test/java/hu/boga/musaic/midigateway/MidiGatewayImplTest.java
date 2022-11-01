package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.gateway.MidiGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

class MidiGatewayImplTest {

    private static final String PATH = "src/main/resources/phrygian.mid";

    public static final float INVALID_DIVISION_TYPE = 0.123445f;
    public static final String PATH_TO_SAVE = "path-to-save";
    private EasyRandom easyRandom;
    private MidiGateway midiGateway;
    private SequenceModell sequenceModell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        easyRandom = new EasyRandom();
        sequenceModell = new SequenceModell();
        sequenceModell.division = SequenceModell.DEFAULT_DIVISION;
        sequenceModell.resolution = SequenceModell.DEFAULT_RESOLUTION;
        trackModell = new TrackModell();
        sequenceModell.tracks.add(trackModell);
        midiGateway = new MidiGatewayImpl();
    }

    @Test
    void initMidiSequence() {
        midiGateway.initMidiSequence(sequenceModell);
        assertTrue(MidiGatewayImpl.SEQUENCE_MAP.containsKey(sequenceModell.getId()));
        assertTrue(MidiGatewayImpl.TRACK_MAP.containsKey(trackModell.getId()));
    }

    @Test
    void initMidiSequenceNegativePath() {
        sequenceModell.division = INVALID_DIVISION_TYPE;
        Assertions.assertThrows(MusaicException.class, () -> midiGateway.initMidiSequence(sequenceModell));
    }

    @Test
    void open(){
        SequenceModell model = midiGateway.open(PATH);
        assertNotNull(model);
        assertEquals(1, model.tracks.size());
        assertEquals(3, model.tracks.get(0).notes.size());
    }
    @Test
    void openNegativePath(){
        assertThrows(MusaicException.class, () ->  midiGateway.open(""));
    }

    @Test
    void play(){
        SequenceModell model = midiGateway.open(PATH);
        try (MockedStatic<Player> mockedStatic = Mockito.mockStatic(Player.class)) {
            midiGateway.play(model.getId());
            mockedStatic.verify(() -> Player.playSequence(Mockito.any()), times(1));
        }
    }

    @Test
    void updateTempo(){
        SequenceModell model = midiGateway.open(PATH);
        model.tempo = 234;

        try (MockedStatic<TempoUtil> mockedStatic = Mockito.mockStatic(TempoUtil.class)) {
            midiGateway.updateTempo(model);
            mockedStatic.verify(() -> TempoUtil.getTempo(Mockito.any()), times(2));
            mockedStatic.verify(() -> TempoUtil.removeTempoEvents(Mockito.any()), times(1));
            mockedStatic.verify(() -> TempoUtil.addTempoEvents(Mockito.any(), eq(234)), times(1));
        }
    }

    @Test
    void save(){
        File f = new File(PATH_TO_SAVE);
        try (MockedStatic<Saver> mockedStatic = Mockito.mockStatic(Saver.class)) {
            midiGateway.save("", PATH_TO_SAVE);
            mockedStatic.verify(() -> Saver.save(Mockito.any(), eq(PATH_TO_SAVE)));
        }
    }
    @Test
    void saveNegativePath(){
        File f = new File(PATH_TO_SAVE);
        try (MockedStatic<Saver> mockedStatic = Mockito.mockStatic(Saver.class)) {
            mockedStatic.when(() -> Saver.save(Mockito.any(), eq(PATH_TO_SAVE))).thenThrow(new MusaicException("blabla"));
            assertThrows(MusaicException.class, () -> midiGateway.save("", PATH_TO_SAVE));
        }
    }

    @Test
    void addTrack(){
        MidiGatewayImpl.TRACK_MAP.clear();//Ha ez nincs itt a pitest fails.
        SequenceModell modell = new SequenceModell();
        modell.tracks.add(new TrackModell());
        midiGateway.initMidiSequence(modell);
        modell.tracks.add(new TrackModell());
        midiGateway.addTrack(modell);

        assertEquals(2, MidiGatewayImpl.TRACK_MAP.size());
    }
}