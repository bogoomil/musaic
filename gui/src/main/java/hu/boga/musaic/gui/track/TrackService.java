package hu.boga.musaic.gui.track;

import hu.boga.musaic.gui.trackeditor.panels.Observable;
import hu.boga.musaic.musictheory.enums.ChordType;

public interface TrackService {
    void updateVolume(String id, double v);
    void updateName(String id, String newName);
    void updateChannel(String id, int newValue);
    void mute(String id, boolean value);
    void load(String trackId);
    void addObservable(Observable<TrackModell> observable);
    void addChord(String trackId, int tick, int pitch, int length, ChordType chordType);
}
