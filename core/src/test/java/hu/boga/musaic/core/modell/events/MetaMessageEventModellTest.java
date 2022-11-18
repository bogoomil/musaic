package hu.boga.musaic.core.modell.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaMessageEventModellTest {

    MetaMessageEventModell modell;

    @BeforeEach
    void setUp() {
        modell = new MetaMessageEventModell(100, new byte[]{1, 2, 3}, CommandEnum.CHANNEL_PREFIX);
    }

    @Test
    void testClone() {
        MetaMessageEventModell cloned = modell.clone();
        assertEquals(modell.tick, cloned.tick);
        assertEquals(modell.data, cloned.data);
        assertEquals(modell.command, cloned.command);

    }
}