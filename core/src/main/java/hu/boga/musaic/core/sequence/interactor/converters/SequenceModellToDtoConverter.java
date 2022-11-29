package hu.boga.musaic.core.sequence.interactor.converters;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.track.interactor.converters.TrackModelltoDtoConverter;

import java.util.stream.Collectors;

public class SequenceModellToDtoConverter {

    final SequenceModell modell;

    public SequenceModellToDtoConverter(SequenceModell modell) {
        this.modell = modell;
    }

    public SequenceDto convert() {
        SequenceDto dto = new SequenceDto();
        dto.id = modell.getId();
        dto.division = modell.division;
        dto.resolution = modell.resolution;
        dto.tempo = modell.tempo;
        dto.tickLength = modell.getTickLength();
        dto.ticksPerMeasure = modell.getTicksPerMeasure();
        dto.ticksIn32nds = modell.getTicksIn32nds();
        dto.tickSize = modell.getTickSize();
        dto.ticksPerSecond = modell.getTicksPerSecond();

        dto.tracks = modell.tracks.stream()
                .map(trackModell -> new TrackModelltoDtoConverter(trackModell).convert())
                .collect(Collectors.toList());

        dto.channelToProgramMappings = modell.getChannelToProgramMappings();
        dto.name = modell.name;
        return dto;
    }
}
