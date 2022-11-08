package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.EventModell;
import hu.boga.musaic.core.modell.events.MetaMessageEventModell;
import hu.boga.musaic.core.modell.events.NoteModell;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrackModell extends BaseModell {

    public int channel;
    public int program;
    public boolean muted;
    public boolean solo;
    public List<EventModell> eventModells = new ArrayList<>();

    @Override
    public String toString() {
        return "\nTrackModell" +
                "\nid:" + getId() +
                ", ch: " + channel +
                ", pr: " + program +
                ", name: " + getName() + ", notes: " + eventModells;

    }

//    private String getNotesString() {
//        StringBuilder stringBuilder = new StringBuilder();
//
//        notes.stream().sorted(Comparator.comparingLong(n -> n.tick)).forEach(note -> {
//            stringBuilder.append("\n" + getSpaces(note.tick) + note.toString());
//        });
//
//        return stringBuilder.toString();
//    }
//
//    private String getSpaces(long tick) {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < tick / 16; i++){
//            stringBuilder.append("[]");
//        }
//        return stringBuilder.toString();
//    }

    public String getName(){
        return getMetaEventByCommand(CommandEnum.TRACK_NAME).stream().map(metaMessageEventModell -> new String(metaMessageEventModell.data, StandardCharsets.UTF_8)).findAny().orElse("");
    }

    public void setName(String name){
        List<MetaMessageEventModell> l = getMetaEventByCommand(CommandEnum.TRACK_NAME);
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

    private List<MetaMessageEventModell> getMetaEventByCommand(CommandEnum commandEnum){
        return this.eventModells.stream()
                .filter(eventModell -> eventModell instanceof MetaMessageEventModell && ((MetaMessageEventModell)eventModell).command == commandEnum)
                .map(eventModell -> (MetaMessageEventModell)eventModell )
                .collect(Collectors.toList());

    }
}
