package hu.boga.musaic.gui.trackeditor.layered;

public interface NoteChangedListener {
    void volumeChanged(String noteId, double newVolume);
    void noteDeleted(String noteId);
}
