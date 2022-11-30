package hu.boga.musaic.gui.track;

public interface TrackService {
    TrackModell getModell();

    void updateVolume(String id, double v);

    void updateName(String id, String newName);

    void updateChannel(String id, int newValue);

    void mute(String id, boolean value);

    void load(String trackId);
}
