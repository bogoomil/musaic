package hu.boga.musaic.gui.trackeditor_old;

import hu.boga.musaic.gui.trackeditor.events.AddChordEvent;
import hu.boga.musaic.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.musaic.gui.trackeditor.events.NoteMovedEvent;
import hu.boga.musaic.gui.trackeditor.events.NotePlayEvent;

public interface NoteChangeListener {
    void onAddChordEvent(AddChordEvent event);
    void onDeleteNoteEvent(DeleteNoteEvent... events);
    void onNoteMoved(NoteMovedEvent... events);
    void onNotePlay(NotePlayEvent event);
}
