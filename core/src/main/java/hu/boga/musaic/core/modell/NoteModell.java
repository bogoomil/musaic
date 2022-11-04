package hu.boga.musaic.core.modell;

import hu.boga.musaic.musictheory.enums.NoteName;

public class NoteModell extends BaseModell{
    public int midiCode;
    public long tick;
    public long length;
    public int velocity;
    public int channel;

    public NoteModell(int midiCode, long tick, long length, int velocity, int channel) {
        super();
        this.midiCode = midiCode;
        this.tick = tick;
        this.length = length;
        this.velocity = velocity;
        this.channel = channel;
    }

    @Override
    public String toString() {
        NoteName noteName = NoteName.byCode(midiCode);
        return "\n[" + noteName.name() + "(" + (midiCode / 12) + "), tick:" + tick + ", length: " + length + "]\n";
    }

    public long getEndTick(){
        return tick + length;
    }
}
