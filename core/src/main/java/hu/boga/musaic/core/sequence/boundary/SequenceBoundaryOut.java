package hu.boga.musaic.core.sequence.boundary;

import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

public interface SequenceBoundaryOut {
    void displaySequence(SequenceDto dto);
}
