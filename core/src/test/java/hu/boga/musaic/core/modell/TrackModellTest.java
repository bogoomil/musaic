package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.events.NoteModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackModellTest {

    public static final String TRACK_TO_STRING = "[C(0), tick:1, length: 1]";
    TrackModell trackModell;
    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        trackModell = new TrackModell();
        noteModell = new NoteModell(0,1,1,100, 0);
        trackModell.eventModells.add(noteModell);
    }

    @Test
    void getTickLength() {
        assertEquals(2, trackModell.getTickLength());
    }
}