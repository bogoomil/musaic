package hu.boga.musaic.core.modell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrackModellTest {

    public static final String TRACK_TO_STRING = "\nTrackModell\nid:d5fd3ca0-d7ff-4418-90d7-b42246d39708, ch: 0, pr: 0, name: \n[C(0), tick:1, length: 1]";
    TrackModell trackModell;
    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        trackModell = new TrackModell();
        noteModell = new NoteModell(0,1,1,100, 0);
        trackModell.notes.add(noteModell);
    }

    @Test
    void getTickLength() {
        assertEquals(2, trackModell.getTickLength());
    }
}