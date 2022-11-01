package hu.boga.musaic.gui.trackeditor.events;

import hu.boga.musaic.musictheory.enums.ChordType;

public class AddChordEvent extends AddNoteEvent{

    ChordType chordType;

    public AddChordEvent(int tick, int pitch, int length, ChordType chordType) {
        super(tick, pitch, length);
        this.chordType = chordType;
    }

    public ChordType getChordType() {
        return this.chordType;
    }
}
