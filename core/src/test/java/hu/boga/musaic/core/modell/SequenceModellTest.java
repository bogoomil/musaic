package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequenceModellTest {

    public static final String TO_STRING = "tracks=[TrackModell{, channel=0, program=0, name='null', notes=[[C(0), tick:1, length: 1]]}], resolution=128, division=0.0, tickLength=2, tempo=120.0}";
    SequenceModell modell;

    @BeforeEach
    void setUp() {
        TrackModell trackModell = new TrackModell();
        NoteModell noteModell = new NoteModell(0,1,1,100);
        trackModell.notes.add(noteModell);

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
    void testToString() {
        assertTrue(modell.toString().contains(TO_STRING));
    }

    @Test
    void testGetTickLength(){
        assertEquals(2, modell.getTickLength());
    }
}