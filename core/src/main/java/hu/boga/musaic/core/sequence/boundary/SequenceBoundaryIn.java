package hu.boga.musaic.core.sequence.boundary;

public interface SequenceBoundaryIn {
    void create();
    void load(String id);
    void open(String path);
    void play(String sequenceId, long fromTick, long toTick);
    void save(String sequenceId, String path);
    void setTempo(String sequenceId, int intValue);
    void addTrack(String sequenceId);
    void stop();
    void updateChannelToProgramMappings(String sequenceId, int channel, int program);
    void duplicateTrack(String trackId);

    void removeTrack(String trackId);
}
