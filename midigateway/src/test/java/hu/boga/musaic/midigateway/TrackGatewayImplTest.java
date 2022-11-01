package hu.boga.musaic.midigateway;

import edu.emory.mathcs.backport.java.util.Arrays;
import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.sound.midi.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.shortThat;
import static org.mockito.Mockito.times;

class TrackGatewayImplTest {

    public static final String NEW_NAME = "newName";
    public static final String TRACK_ID = "trackId";
    public static final String SEQ_ID = "SEQID";
    private TrackGateway gateway;
    private Sequence modell;
    private Track track;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        InMemorySequenceStore.TRACK_MAP.clear();
        InMemorySequenceStore.SEQUENCE_MAP.clear();
        modell = new Sequence(Sequence.PPQ, 128);
        track = modell.createTrack();

        InMemorySequenceStore.SEQUENCE_MAP.put(SEQ_ID, modell);
        InMemorySequenceStore.TRACK_MAP.put(TRACK_ID, track);

        gateway = new TrackGatewayImpl();
    }

    @Test
    void updateTrackName() {
        try (MockedStatic<TrackUtil> mockedStatic = Mockito.mockStatic(TrackUtil.class)) {
            gateway.updateTrackName(TRACK_ID, NEW_NAME);
            mockedStatic.verify(() -> TrackUtil.updateTrackName(Mockito.any(), eq(NEW_NAME)), times(1));
        }
    }

    @Test
    void removeTrack() {
        gateway.removeTrack(SEQ_ID, TRACK_ID);
        assertEquals(0, modell.getTracks().length);
    }

    @Test
    void updateTrackProgram(){
        InMemorySequenceStore.TRACK_MAP.put(TRACK_ID, track);
        try (MockedStatic<TrackUtil> mockedStatic = Mockito.mockStatic(TrackUtil.class)) {
            gateway.updateTrackProgram(TRACK_ID, 0,0);
            List<MidiEvent> events = TrackUtil.getMidiEventsByCommand(track, ShortMessage.PROGRAM_CHANGE);
            mockedStatic.verify(() -> TrackUtil.addProgramChangeEvent(Mockito.any(), eq(0), eq(0), eq(0)));
        }

    }
    @Test
    void addNotesToTrack(){
        InMemorySequenceStore.TRACK_MAP.put(TRACK_ID, track);
        try (MockedStatic<NoteUtil> mockedStatic = Mockito.mockStatic(NoteUtil.class)) {
            gateway.addNotesToTrack(TRACK_ID, Arrays.asList(new NoteModell[]{new NoteModell(12,0,512, 100, 0)}));
            mockedStatic.verify(() -> NoteUtil.addNote(track, 0,12,512,100,0));
        }
    }
}