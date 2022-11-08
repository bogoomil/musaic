package hu.boga.musaic.core.modell.events;

public class ShortMessageEventModell extends EventModell{
    public int channel, data1, data2;

    public ShortMessageEventModell(int tick, int channel, CommandEnum commandEnum, int data1, int data2) {
        super(tick, commandEnum);
        this.channel = channel;
        this.data1 = data1;
        this.data2 = data2;
    }
}
