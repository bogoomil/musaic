package hu.boga.musaic.core.sequence.boundary;

import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;

public interface SequenceBoundaryIn {
    void create();
    void load(String id);
    void open(String path);
    void play(String sequenceId);
    void save(String sequenceId, String path);

    void setTempo(String sequenceId, int intValue);

    void addTrack(String sequenceId);

    void stop();

    void reloadSequence(SequenceDto sequenceDto);

    void updateTrackProgram(String trackId, int program, int channel);
}
