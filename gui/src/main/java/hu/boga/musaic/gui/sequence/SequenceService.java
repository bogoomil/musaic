package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;

public interface SequenceService {
    void open(String path);
    void create();
    SequenceModell getSequence();
    void updateChannelMapping(String id, int channel, int program);
    void save(String id, String path);
    void addTrack(String id);
    void duplicateTrack(String id);
}
