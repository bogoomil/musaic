package hu.boga.musaic.gui.sequence;

public interface SequenceService {
    void open(String path);
    void create();
    SequenceModell getSequence();
    void updateChannelMapping(String id, int channel, int program);
    void save(String id, String path);
    void addTrack(String id);
    void duplicateTrack(String id);
    void removeTrack(String id);

    void play(String sequenceId, int from, int to);
    void stop();
}
