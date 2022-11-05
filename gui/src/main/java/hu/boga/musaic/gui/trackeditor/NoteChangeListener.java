package hu.boga.musaic.gui.trackeditor;

import hu.boga.musaic.gui.trackeditor.events.AddChordEvent;
import hu.boga.musaic.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.musaic.gui.trackeditor.events.NoteMovedEvent;

public interface NoteChangeListener {
    void onAddChordEvent(AddChordEvent event);
    void onDeleteNoteEvent(DeleteNoteEvent... events);
    void onNoteMoved(NoteMovedEvent... events);
}
