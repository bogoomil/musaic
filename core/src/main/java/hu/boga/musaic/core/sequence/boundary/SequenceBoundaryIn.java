package hu.boga.musaic.core.sequence.boundary;

public interface SequenceBoundaryIn {
    void create();
    void load(String id);
    void open(String path);
    void play(String sequenceId);
}
