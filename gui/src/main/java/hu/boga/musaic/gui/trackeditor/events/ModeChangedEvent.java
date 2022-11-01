package hu.boga.musaic.gui.trackeditor.events;

import hu.boga.musaic.musictheory.enums.Tone;

public class ModeChangedEvent {
    Tone tone;

    public ModeChangedEvent(final Tone tone) {
        this.tone = tone;
    }

    public Tone getTone() {
        return this.tone;
    }
}
