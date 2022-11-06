package hu.boga.musaic.core.modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SequenceModell extends BaseModell{

    public static final int DEFAULT_TEMPO = 120;
    public static final int DEFAULT_DIVISION = 0;
    public static final int DEFAULT_RESOLUTION = 128;

    public List<TrackModell> tracks = new ArrayList<>();
    public int resolution = DEFAULT_RESOLUTION;
    public float division = DEFAULT_DIVISION;
    public float tempo = DEFAULT_TEMPO;

    public int getTicksPerMeasure() {
        return 4 * resolution;
    }

    public int getTicksIn32nds() {
        return getTicksPerMeasure() / 32;
    }

    public float getTicksPerSecond() {
        return resolution * (tempo / 60);
    }

    public float getTickSize() {
        return 1 / getTicksPerSecond();
    }

    public long getTickLength() {
        return tracks.stream().mapToLong(trackModell -> trackModell.getTickLength()).max().orElse(0);
    }

    public Optional<TrackModell> getTrackById(String trackId){
        return tracks.stream().filter(trackModell -> trackModell.getId().equals(trackId)).findFirst();
    }

    @Override
    public String toString() {
        return "\n--------------------------------------------------------" +
                "\nSequenceModell" +
                "\nid: " + getId() +
                ", res: " + resolution +
                ", div: " + division +
                ", tempo: " + tempo +
                "\n" + tracks;

    }
}
