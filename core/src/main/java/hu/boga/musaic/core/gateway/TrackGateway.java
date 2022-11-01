package hu.boga.musaic.core.gateway;

public interface TrackGateway {
    void updateTrackName(String trackId, String newName);

    void removeTrack(String sequenceId, String trackId);

    void updateTrackProgram(String trackId, int program, int channel);
}
