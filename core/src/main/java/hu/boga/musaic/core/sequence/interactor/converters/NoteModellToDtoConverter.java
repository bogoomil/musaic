package hu.boga.musaic.core.sequence.interactor.converters;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;

public class NoteModellToDtoConverter {
    final NoteModell modell;
    public NoteModellToDtoConverter(NoteModell noteModell) {
        this.modell = noteModell;
    }

    public NoteDto convert() {
        NoteDto dto = new NoteDto();
        dto.id = modell.getId();
        dto.length = modell.length;
        dto.midiCode = modell.midiCode;
        dto.tick = modell.tick;
        dto.velocity = modell.velocity;
        return dto;
    }
}
