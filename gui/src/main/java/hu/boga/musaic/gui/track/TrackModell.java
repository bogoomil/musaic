package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.trackeditor.NoteModell;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<NoteModell> getNotesNormalized(){
        List<NoteModell> notes = new ArrayList<>();
        int lowestMidiCode = getLowestPitch();
        this.notes.forEach(noteModell -> {
            NoteModell clone = noteModell.clone();
            clone.midiCode = noteModell.midiCode - lowestMidiCode;
            notes.add(clone);
        });
        return notes;
    }

    private int getLowestPitch(){
        if(!notes.isEmpty()){
            this.notes.sort(Comparator.comparingInt(noteModell -> noteModell.midiCode));
            return notes.get(0).midiCode;
        }
        return 0;
    }
}
