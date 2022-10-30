package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.BaseModell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseModellTest {

    public static final String ID = "id";
    private BaseModell baseModell;
    private BaseModell otherNotEqual;
    private BaseModell otherEqual;

    @BeforeEach
    void setUp() {
        baseModell = new BaseModell(ID);
        otherNotEqual = new BaseModell("id2");
        otherEqual = new BaseModell(ID);
    }

    @Test
    void getId() {
        Assertions.assertEquals("id", baseModell.getId());
    }

    @Test
    void testEquals() {
        assertEquals(baseModell, otherEqual);
        assertFalse(baseModell.equals(otherNotEqual));
        Assertions.assertTrue(baseModell.equals(baseModell));
        Assertions.assertFalse(baseModell.equals(""));
        Assertions.assertFalse(baseModell.equals(null));
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(3386, baseModell.hashCode());
    }
}