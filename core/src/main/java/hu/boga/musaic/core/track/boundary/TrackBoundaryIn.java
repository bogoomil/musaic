package hu.boga.musaic.core.track.boundary;

import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.musictheory.enums.ChordType;

public interface TrackBoundaryIn {
    void updateTrackName(TrackDto trackDto);
    void removeTrack(String id);
    void updateTrackProgram(String trackId, int program, int channel);
    void addChord(String id, int tick, int pitch, int length, int channel, ChordType chordType);
    void deleteNotes(String id, NoteDto[] notes);
    void moveNote(String noteId, int newTick);
    void showTrack(String id);

    void setMuted(String id, boolean muted);
}
