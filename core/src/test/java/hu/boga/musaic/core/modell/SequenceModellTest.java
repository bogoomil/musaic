package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.modell.events.ShortMessageEventModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequenceModellTest {

    public static final String NON_EXISTING_TRACKID = "NON_EXISTING_TRACKID";
    SequenceModell modell;
    private TrackModell trackModell;

    @BeforeEach
    void setUp() {
        trackModell = new TrackModell();
        NoteModell noteModell = new NoteModell(0,1,1,100, 0);
        trackModell.eventModells.add(noteModell);
        trackModell.eventModells.add(new ShortMessageEventModell(0, 1, CommandEnum.PROGRAM_CHANGE, 100, 0));

        modell = new SequenceModell();
        modell.tracks.add(trackModell);
    }

    @Test
    void getTicksPerMeasure() {
        assertEquals(512, modell.getTicksPerMeasure());
    }

    @Test
    void getTicksIn32nds() {
        assertEquals(16, modell.getTicksIn32nds());
    }

    @Test
    void ticksPerSecond() {
        assertEquals(256, modell.getTicksPerSecond());
    }

    @Test
    void tickSize() {
        assertEquals(0.00390625, modell.getTickSize());
    }

    @Test
    void testGetTickLength(){
        assertEquals(2, modell.getTickLength());
    }

    @Test
    void getTrackById(){
        assertEquals(trackModell, modell.getTrackById(trackModell.getId()).get());
        assertTrue(modell.getTrackById(NON_EXISTING_TRACKID).isEmpty());
    }

    @Test
    void getChannelToProgramMappings(){
        assertEquals(100, modell.getChannelToProgramMappings()[1]);
        assertEquals(0, modell.getChannelToProgramMappings()[2]);
    }

    @Test
    void updateChannelToProgramMapping(){
        modell.updateChannelToProgramMapping(3, 100);
        modell.updateChannelToProgramMapping(3, 101);
        assertEquals(101, modell.getChannelToProgramMappings()[3]);
        assertEquals(1, modell.tracks.get(0).getShortMessageEventsByCommand(CommandEnum.PROGRAM_CHANGE).size());
    }
}