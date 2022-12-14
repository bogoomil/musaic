package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.events.NoteModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackModellTest {

    public static final String TRACK_TO_STRING = "[C(0), tick:1, length: 1]";
    TrackModell trackModell;
    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        trackModell = new TrackModell();
        noteModell = new NoteModell(0,1,1,1, 0);
        trackModell.eventModells.add(noteModell);
    }

    @Test
    void getTickLength() {
        assertEquals(2, trackModell.getTickLength());
    }

    @Test
    void cloneTest(){
        trackModell.solo = true;
        trackModell.muted = true;
        TrackModell cloned = trackModell.clone();
        assertEquals(trackModell.channel, cloned.channel);
        assertEquals(trackModell.eventModells.size(), cloned.eventModells.size());
        assertEquals(trackModell.muted, cloned.muted);
        assertEquals(trackModell.solo, cloned.solo);

    }

    @Test
    void getNotesBetween(){
        trackModell.eventModells.add(new NoteModell(0,0,1,1, 0));
        trackModell.eventModells.add(new NoteModell(0,2,1,1, 0));
        List<NoteModell> notes = trackModell.getNotesBetween(1,2);
        assertEquals(1, notes.size());
        assertEquals(1, notes.get(0).tick);
    }

}