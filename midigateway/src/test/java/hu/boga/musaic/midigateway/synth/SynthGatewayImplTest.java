package hu.boga.musaic.midigateway.synth;

import hu.boga.musaic.core.gateway.synth.SynthGateway;
import hu.boga.musaic.midigateway.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class SynthGatewayImplTest {

    SynthGateway gateway;
    @BeforeEach
    void setUp() {
        gateway = new SynthGatewayImpl();
    }

    @Test
    void playOneNote() {
        try (MockedStatic<Player> mockedStatic = Mockito.mockStatic(Player.class)) {
            gateway.playOneNote(120,0,128,12,128,0);
            mockedStatic.verify(() -> Player.playNote(eq(120),eq(0),eq(128),eq(12),eq(128),eq(0)));
        }

    }
}