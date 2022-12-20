package hu.boga.musaic.gui.sequence;

import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.gui.track.TrackModell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequenceModell {
    public String id;
    public static final String[] COLOR_MAPPING = new String[]{"0xccffffff","0xcce6ffff","0xe6ccffff","0xffccffff","0xffcce6ff","0xffcce6ff","0xffccccff","0xffccb3ff","0xffe6ccff","0xffffb3ff","0xffffccff","0xe6e6ccff","0xccffccff","0xe6e680ff","0xffe666ff","0xffcc80ff"};
    public int[] channelToProgramMappings;
    public List<TrackModell> tracks = new ArrayList<>();
    public int resolution;
    public int fourthInMeasure = 4;
    public int tempo;

    public SequenceModell(SequenceDto dto){
        this.id = dto.id;
        this.channelToProgramMappings = dto.channelToProgramMappings;
        this.resolution = dto.resolution;
        this.tempo = (int) dto.tempo;

        dto.tracks.forEach(trackDto -> tracks.add(new TrackModell(trackDto)));
    }

    @Override
    public String toString() {
        return "SequenceModell{" +
                "id='" + id + '\'' +
                ", channelToProgramMappings=" + Arrays.toString(channelToProgramMappings) +
                ", tracks=" + tracks +
                ", resolution=" + resolution +
                ", fourthInMeasure=" + fourthInMeasure +
                '}';
    }
}
