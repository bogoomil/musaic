package hu.boga.musaic.core.modell.events;

import hu.boga.musaic.musictheory.enums.NoteName;

public class NoteModell extends EventModell{
    public int midiCode;
    public long length;
    public double velocity = 1;
    public int channel;

    public NoteModell(int midiCode, long tick, long length, double velocity, int channel) {
        super(tick, CommandEnum.NOTE_ON);
        this.midiCode = midiCode;
        this.tick = tick;
        this.length = length;
        this.velocity = velocity;
        this.channel = channel;
    }

    @Override
    public String toString() {
        NoteName noteName = NoteName.byCode(midiCode);
        return "[" + noteName.name() + "(" + (midiCode / 12) + "), tick:" + tick + ", length: " + length + "]";
    }

    public NoteModell clone(){
        return new NoteModell(this.midiCode, this.tick, this.length, this.velocity, this.channel);
    }

    public long getEndTick(){
        return tick + length;
    }
}
