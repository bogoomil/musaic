package hu.boga.musaic.core.note;

public interface NoteBoundaryIn {

    void play(String trackId, int midiCode, int lengthInTicks);
    void setNoteVolume(String noteId, double volume);
}
