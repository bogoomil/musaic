package hu.boga.musaic.core.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TickEventTest {

    public static final String SEQUENCE_ID = "SEQUENCE_ID";
    TickEvent event;
    @BeforeEach
    void setUp() {
        event = new TickEvent(SEQUENCE_ID, 1);
    }

    @Test
    void getSequenceId() {
        assertEquals(SEQUENCE_ID, event.getSequenceId());
    }

    @Test
    void getTick() {
        assertEquals(1, event.getTick());
    }
}