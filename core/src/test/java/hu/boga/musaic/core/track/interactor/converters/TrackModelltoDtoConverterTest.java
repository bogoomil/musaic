package hu.boga.musaic.core.track.interactor.converters;

import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackModelltoDtoConverterTest {

    TrackModell trackModell;
    TrackModelltoDtoConverter converter;
    EasyRandom easyRandom = new EasyRandom();
    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        trackModell = easyRandom.nextObject(TrackModell.class);
        noteModell = easyRandom.nextObject(NoteModell.class);

        trackModell.eventModells.add(noteModell);
        converter = new TrackModelltoDtoConverter(trackModell);
    }

    @Test
    void convert() {
        TrackDto dto = converter.convert();
        assertEquals(trackModell.eventModells.size(), dto.notes.size());
        assertEquals(trackModell.channel, dto.channel);
        assertEquals(trackModell.program, dto.program);
        assertEquals(trackModell.getId(), dto.id);
        assertEquals(trackModell.getName(), dto.name);

        assertEquals(noteModell.length, dto.notes.get(0).length);
    }
}