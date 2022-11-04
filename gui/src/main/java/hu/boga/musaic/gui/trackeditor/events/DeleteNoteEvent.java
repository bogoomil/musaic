package hu.boga.musaic.gui.trackeditor.events;

public class DeleteNoteEvent {

    String noteId;

    public DeleteNoteEvent(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteId() {
        return noteId;
    }
}
