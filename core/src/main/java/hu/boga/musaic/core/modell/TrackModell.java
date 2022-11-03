package hu.boga.musaic.core.modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackModell extends BaseModell {

    public int channel;
    public int program;
    public String name;
    public List<NoteModell> notes = new ArrayList<>();

    @Override
    public String toString() {
        return "TrackModell{" +
                ", channel=" + channel +
                ", program=" + program +
                ", name='" + name + '\'' +
                ", notes=" + notes +
                '}';
    }

    public long getTickLength() {
        return this.notes.stream().mapToLong(noteModell -> noteModell.getEndTick()).max().orElse(0L);
    }

    public Optional<NoteModell> gtNoteModellById(String noteId){
        return this.notes.stream().filter(noteModell -> noteModell.getId().equals(noteId)).findFirst();
    }
}
