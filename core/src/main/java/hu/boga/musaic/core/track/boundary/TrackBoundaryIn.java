package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.musictheory.enums.ChordType;

public interface TrackBoundaryIn {
    void showTrack(String trackId);
    void addChord(String trackId, int tick, int pitch, int length, ChordType chordType);
    void deleteNotes(String trackId, NoteDto[] notes);
    void moveNote(String noteId, int newTick);
    void duplicate(String trackId, String[] ids,  int fromTick, int toTick);
    void moveUpAndDownNotes(String trackId, String[] ids, int move);
}
