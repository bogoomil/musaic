package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.note.NoteModell;

import java.util.ArrayList;
import java.util.List;

public class TrackModell {
    public String id;
    public String name;
    public int channel;
    public boolean muted;
    public boolean solo;
    public List<NoteModell> notes = new ArrayList<>();

    public TrackModell(TrackDto dto) {
        this.id = dto.id;
        this.channel = dto.channel;
        this.muted = dto.muted;
        this.name = dto.name;

        dto.notes.forEach(noteDto -> notes.add(new NoteModell(noteDto)));
    }

    @Override
    public String toString() {
        return "TrackModell{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", channel=" + channel +
                ", muted=" + muted +
                ", solo=" + solo +
                ", notes=" + notes +
                '}';
    }
}
