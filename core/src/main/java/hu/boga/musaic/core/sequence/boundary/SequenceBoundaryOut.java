package hu.boga.musaic.core.sequence.boundary;

import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.sequence.boundary.dtos.TrackDto;

public interface SequenceBoundaryOut {
    void displaySequence(SequenceDto dto);
    void displayNewTrack(TrackDto trackDto);
}
