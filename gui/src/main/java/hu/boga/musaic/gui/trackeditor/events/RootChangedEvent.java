package hu.boga.musaic.gui.trackeditor.events;

import hu.boga.musaic.musictheory.enums.NoteName;

public class RootChangedEvent {
    NoteName noteName;

    public RootChangedEvent(final NoteName noteName) {
        this.noteName = noteName;
    }

    public NoteName getNoteName() {
        return this.noteName;
    }
}
