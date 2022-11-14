package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.events.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrackModell extends BaseModell {

    public int channel;
    public boolean muted;
    public boolean solo;
    public List<EventModell> eventModells = new ArrayList<>();


    public String getName(){
        return getMetaMessageEventsByCommand(CommandEnum.TRACK_NAME).stream().map(metaMessageEventModell -> new String(metaMessageEventModell.data, StandardCharsets.UTF_8)).findAny().orElse("");
    }

    public void setName(String name){
        List<MetaMessageEventModell> l = getMetaMessageEventsByCommand(CommandEnum.TRACK_NAME);
        if(l.isEmpty()){
            MetaMessageEventModell modell = new MetaMessageEventModell(0, name.getBytes(StandardCharsets.UTF_8), CommandEnum.TRACK_NAME);
            eventModells.add(modell);
        } else {
            l.get(0).data = name.getBytes(StandardCharsets.UTF_8);
        }
    }

    public long getTickLength() {
        return this.getNotes().stream().mapToLong(noteModell -> noteModell.getEndTick()).max().orElse(0L);
    }

    public Optional<NoteModell> getNoteModellById(String noteId) {
        return this.getNotes().stream()
                .filter(noteModell -> noteModell.getId().equals(noteId))
                .findFirst();
    }

    public List<NoteModell> getNotes(){
        return this.eventModells.stream()
                .filter(eventModell -> eventModell instanceof NoteModell)
                .map(eventModell -> (NoteModell)eventModell )
                .collect(Collectors.toList());
    }

    public List<MetaMessageEventModell> getMetaMessageEventsByCommand(CommandEnum commandEnum){
        return this.eventModells.stream()
                .filter(eventModell -> eventModell instanceof MetaMessageEventModell && ((MetaMessageEventModell)eventModell).command == commandEnum)
                .map(eventModell -> (MetaMessageEventModell)eventModell )
                .collect(Collectors.toList());

    }
    public List<ShortMessageEventModell> getShortMessageEventsByCommand(CommandEnum commandEnum){
        return this.eventModells.stream()
                .filter(eventModell -> eventModell instanceof ShortMessageEventModell && ((ShortMessageEventModell)eventModell).command == commandEnum)
                .map(eventModell -> (ShortMessageEventModell)eventModell )
                .collect(Collectors.toList());

    }

    public List<NoteModell> getNotesBetween(int fromTickInclusive, int toTickExclusive){
        return this.eventModells.stream()
                .filter(eventModell -> eventModell instanceof NoteModell && isTickBetween((NoteModell) eventModell, fromTickInclusive, toTickExclusive))
                .map(eventModell -> (NoteModell)eventModell )
                .collect(Collectors.toList());

    }

    private boolean isTickBetween(NoteModell noteModell, int fromTickInclusive, int toTickExclusive) {
        return noteModell.tick >= fromTickInclusive && noteModell.tick < toTickExclusive;
    }
}
