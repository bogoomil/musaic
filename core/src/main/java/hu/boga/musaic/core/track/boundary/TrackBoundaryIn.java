package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.musictheory.enums.ChordType;

public interface TrackBoundaryIn {
    void load(String trackId);
    void updateTrackChannel(String trackId, int channel);
    void updateTrackName(TrackDto trackDto);
    void setMuted(String trackId, boolean muted);
    void updateVolume(String trackId, double velocityPercent);
    void addChord(String trackId, int tick, int pitch, int length, ChordType chordType);
    void deleteNotes(String trackId, NoteDto[] notes);
    void moveNote(String noteId, int newTick);
    void duplicate(String trackId, int fromTick, int toTick);
    void moveUpAndDownNotes(String trackId, String[] ids, int move);
    void noteVolumeChanged(String noteId, double v);
}
