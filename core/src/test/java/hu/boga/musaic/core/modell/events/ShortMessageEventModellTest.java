package hu.boga.musaic.core.modell.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortMessageEventModellTest {

    ShortMessageEventModell modell;

    @BeforeEach
    void setUp() {
        modell = new ShortMessageEventModell(100, 1,CommandEnum.PROGRAM_CHANGE, 120,123);
    }

    @Test
    void testClone() {
        ShortMessageEventModell cloned = modell.clone();
        assertEquals(modell.channel, cloned.channel);
        assertEquals(modell.data1, cloned.data1);
        assertEquals(modell.data2, cloned.data2);
        assertEquals(modell.tick, cloned.tick);
        assertEquals(modell.command, cloned.command);
    }
}