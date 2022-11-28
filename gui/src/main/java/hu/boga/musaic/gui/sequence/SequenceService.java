package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;

public interface SequenceService {
    void open(String path);
    void create();
    SequenceDto getSequenceDto();
}
