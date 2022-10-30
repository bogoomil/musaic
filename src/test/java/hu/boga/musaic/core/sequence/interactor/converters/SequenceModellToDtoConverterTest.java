package hu.boga.musaic.core.sequence.interactor.converters;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequenceModellToDtoConverterTest {

    SequenceModell modell;
    SequenceModellToDtoConverter converter;
    EasyRandom easyRandom = new EasyRandom();
    private TrackModell trackModell;
    private NoteModell noteModell;

    @BeforeEach
    void setUp() {
        trackModell = easyRandom.nextObject(TrackModell.class);
        noteModell = easyRandom.nextObject(NoteModell.class);
        trackModell.notes.add(noteModell);

        modell = easyRandom.nextObject(SequenceModell.class);
        modell.tracks.add(trackModell);

        converter = new SequenceModellToDtoConverter(modell);
    }

    @Test
    void convert() {
        SequenceDto dto  = converter.convert();
        assertEquals(dto.division, modell.division);
        assertEquals(dto.resolution, modell.resolution);
        assertEquals(dto.tempo, modell.tempo);
        assertEquals(dto.name, modell.name);
        assertEquals(dto.id, modell.getId());
        assertEquals(dto.tickLength, modell.getTickLength());
        assertEquals(dto.ticksIn32nds, modell.getTicksIn32nds());
        assertEquals(dto.tickSize, modell.getTickSize());

        assertEquals(dto.tracks.get(0).id, trackModell.getId());
    }
}