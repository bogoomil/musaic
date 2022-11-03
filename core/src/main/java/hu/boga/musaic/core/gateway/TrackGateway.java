package hu.boga.musaic.core.gateway;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.core.modell.TrackModell;

import java.util.List;

public interface TrackGateway {
    void updateTrackName(String trackId, String newName);

    void removeTrack(String sequenceId, String trackId);

    void updateTrackProgram(String trackId, int program, int channel);

    void addNotesToTrack(String trackId, List<NoteModell> notesToAdd);

    void moveNote(String trackId, int tick, int pitch, int newTick);

    void deleteNote(String trackId, long tick, int midiCode);
}
