package hu.boga.musaic.core.track.boundary.dtos;

import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;

import java.util.List;

public class TrackDto {
    public int channel;
    public int program;
    public String name;
    public List<NoteDto> notes;

    public String id;
    public boolean muted;

    @Override
    public String toString() {
        return "TrackDto{" +
                "channel=" + channel +
                ", program=" + program +
                ", name='" + name + '\'' +
                ", notes=" + notes +
                ", id='" + id + '\'' +
                '}';
    }
}
