package hu.boga.musaic.gui.note;

import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.musictheory.enums.NoteName;

public class NoteModell {
    public long tick;
    public int midiCode;
    public long length;
    public double velocity = 1;

    public NoteModell(int midiCode, long tick, long length, double velocity) {
        this.midiCode = midiCode;
        this.tick = tick;
        this.length = length;
        this.velocity = velocity;
    }

    public NoteModell(NoteDto noteDto) {
        this(noteDto.midiCode, noteDto.tick, noteDto.length, noteDto.velocity);
    }

    @Override
    public String toString() {
        NoteName noteName = NoteName.byCode(midiCode);
        return "[" + noteName.name() + "(" + (midiCode / 12) + "), tick:" + tick + ", length: " + length + "]";
    }

    public NoteModell clone(){
        return new NoteModell(this.midiCode, this.tick, this.length, this.velocity);
    }

    public long getEndTick(){
        return tick + length;
    }
}
