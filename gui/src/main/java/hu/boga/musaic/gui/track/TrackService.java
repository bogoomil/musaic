package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.enums.ChordType;

public interface TrackService {
    void updateVolume(String id, double v);
    void updateName(String id, String newName);
    void updateChannel(String id, int newValue);
    void mute(String id, boolean value);
    void load(String trackId);
    void addObservable(Observable<TrackModell> observable);
    void addChord(String trackId, int tick, int pitch, int length, ChordType chordType);
    void noteVolumeChanged(String noteId, double v);
    void noteDeleted(String id, NoteDto[] notes);

    void updateNoteTick(String noteId, int increment);
    void updateNotePitch(String trackId, String[] ids, int move);

    void duplicateSelection(String trackId, int start, int end);

    void playChord(String trackId, int midiCode, int ertek);
}
