package hu.boga.musaic.core.track.interactor.converters;

import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.interactor.converters.NoteModellToDtoConverter;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

import java.util.stream.Collectors;

public class TrackModelltoDtoConverter {
    TrackModell modell;
    public TrackModelltoDtoConverter(TrackModell modell) {
        this.modell = modell;
    }

    public TrackDto convert() {
        TrackDto dto = new TrackDto();

        dto.channel = modell.channel;
        dto.name = modell.name;
        dto.program = modell.program;
        dto.id = modell.getId();

        dto.notes = modell.notes.stream()
                .map(noteModell -> new NoteModellToDtoConverter(noteModell).convert())
                .collect(Collectors.toList());

        return dto;
    }
}
