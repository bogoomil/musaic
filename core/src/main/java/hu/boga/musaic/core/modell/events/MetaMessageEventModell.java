package hu.boga.musaic.core.modell.events;

public class MetaMessageEventModell extends EventModell {
    public byte[] data;

    public MetaMessageEventModell(long tick, byte[] data, CommandEnum command) {
        super(tick, command);
        this.data = data;
    }

    @Override
    public MetaMessageEventModell clone() {
        return new MetaMessageEventModell(tick, data, command);
    }
}
