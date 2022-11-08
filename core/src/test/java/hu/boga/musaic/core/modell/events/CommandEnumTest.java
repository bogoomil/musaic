package hu.boga.musaic.core.modell.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandEnumTest {

    @Test
    void byIntValue() {
        assertEquals(CommandEnum.TUNE_REQUEST, CommandEnum.byIntValue(246).get());
    }

    @Test
    void getIntValue() {
        assertEquals(246, CommandEnum.TUNE_REQUEST.getIntValue());
    }
}