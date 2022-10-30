package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.TrackModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackModellTest {

    public static final String TRACK_TO_STRING = "TrackModell{, channel=0, program=0, name='null', notes=[[C(0), tick:1, length: 1]]}";
    TrackModell trackModell;
    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        trackModell = new TrackModell();
        noteModell = new NoteModell(0,1,1,100);
        trackModell.notes.add(noteModell);
    }

    @Test
    void testToString() {
        assertEquals(TRACK_TO_STRING, trackModell.toString());
    }

    @Test
    void getTickLength() {
        assertEquals(2, trackModell.getTickLength());
    }
}