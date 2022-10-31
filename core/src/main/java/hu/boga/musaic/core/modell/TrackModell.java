package hu.boga.musaic.core.modell;

import java.util.ArrayList;
import java.util.List;

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
}
