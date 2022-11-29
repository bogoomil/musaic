package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;

public class SequenceDtoToModellConverter {
    private SequenceDto dto;

    public SequenceDtoToModellConverter(SequenceDto dto) {
        this.dto = dto;
    }

    public SequenceModell convert(){
        SequenceModell modell = new SequenceModell();
        modell.id = dto.id;
        modell.channelToProgramMappings = dto.channelToProgramMappings;

        return modell;
    }
}
