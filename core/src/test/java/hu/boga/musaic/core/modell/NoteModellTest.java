package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.NoteModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteModellTest {

    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        noteModell = new NoteModell(1,1L,1L,1);
    }

    @Test
    void testToString() {
        assertEquals("[Cs(0), tick:1, length: 1]", noteModell.toString());
    }

    @Test
    void testGetEndTick(){
        assertEquals(2, noteModell.getEndTick());
    }
}