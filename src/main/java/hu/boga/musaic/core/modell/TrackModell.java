package hu.boga.musaic.core.modell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TrackModell extends BaseModell {

    private static final Logger LOG = LoggerFactory.getLogger(TrackModell.class);

    public int channel;
    public int program;
    public String name;
    public final List<NoteModell> notes = new ArrayList<>();


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
