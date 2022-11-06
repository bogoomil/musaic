package hu.boga.musaic.core.modell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TrackModell extends BaseModell {

    public int channel;
    public int program;
    public String name = "";
    public boolean muted;
    public boolean solo;
    public List<NoteModell> notes = new ArrayList<>();

    @Override
    public String toString() {
        return "\nTrackModell" +
                "\nid:" + getId() +
                ", ch: " + channel +
                ", pr: " + program +
                ", name: " + name + ", notes: " + notes;

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

    public long getTickLength() {
        return this.notes.stream().mapToLong(noteModell -> noteModell.getEndTick()).max().orElse(0L);
    }

    public Optional<NoteModell> gtNoteModellById(String noteId){
        return this.notes.stream().filter(noteModell -> noteModell.getId().equals(noteId)).findFirst();
    }
}
