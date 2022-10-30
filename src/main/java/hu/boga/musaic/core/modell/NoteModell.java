package hu.boga.musaic.core.modell;

import hu.boga.musaic.musictheory.enums.NoteName;

public class NoteModell extends BaseModell{
    public int midiCode;
    public long tick;
    public long length;
    public int velocity;

    public NoteModell(int midiCode, long tick, long length, int velocity) {
        super();
        this.midiCode = midiCode;
        this.tick = tick;
        this.length = length;
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        NoteName noteName = NoteName.byCode(midiCode);
        return "[" + noteName.name() + "(" + (midiCode / 12) + "), tick:" + tick + ", length: " + length + "]";
    }

    public long getEndTick(){
        return tick + length;
    }
}
