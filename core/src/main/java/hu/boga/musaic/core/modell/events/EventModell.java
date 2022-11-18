package hu.boga.musaic.core.modell.events;

import hu.boga.musaic.core.modell.BaseModell;

public abstract class EventModell extends BaseModell {

    public long tick;
    public CommandEnum command;

    protected EventModell(long tick, CommandEnum command) {
        this.tick = tick;
        this.command = command;
    }

    public abstract EventModell clone();
}
