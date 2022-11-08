package hu.boga.musaic.core.sequence.interactor.converters;

import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteModellToDtoConverterTest {

    NoteModell modell;
    NoteModellToDtoConverter converter;

    @BeforeEach
    void setUp(){
        modell = new EasyRandom().nextObject(NoteModell.class);
        converter = new NoteModellToDtoConverter(modell);
    }

    @Test
    void convert() {
        NoteDto dto = converter.convert();
        assertEquals(modell.tick, dto.tick);
        assertEquals(modell.length, dto.length);
        assertEquals(modell.midiCode, dto.midiCode);
        assertEquals(modell.velocity, dto.velocity);
    }
}