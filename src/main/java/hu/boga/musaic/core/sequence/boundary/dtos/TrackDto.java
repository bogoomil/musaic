package hu.boga.musaic.core.sequence.boundary.dtos;

import java.util.List;

public class TrackDto {
    public int channel;
    public int program;
    public String name;
    public List<NoteDto> notes;

    public String id;
}
