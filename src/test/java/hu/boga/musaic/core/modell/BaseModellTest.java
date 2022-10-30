package hu.boga.musaic.core.modell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseModellTest {

    public static final String ID = "id";
    private BaseModell baseModell;

    @BeforeEach
    void setUp(){
        baseModell = new BaseModell(ID);
    }

    @Test
    void getId() {
        assertEquals("id", baseModell.getId());
    }

    @Test
    void testEquals() {
        BaseModell otherNotEqual = new BaseModell("id2");
        BaseModell otherEqual = new BaseModell(ID);
        assertEquals(baseModell, otherEqual);
        assertFalse(baseModell.equals(otherNotEqual));
        assertTrue(baseModell.equals(baseModell));
        assertFalse(baseModell.equals(""));
        assertFalse(baseModell.equals(null));
    }

    @Test
    void testHashCode() {
        assertEquals(3386, baseModell.hashCode());
    }
}