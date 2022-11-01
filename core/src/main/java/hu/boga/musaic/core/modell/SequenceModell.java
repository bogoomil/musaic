package hu.boga.musaic.core.modell;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String toString() {
        return "SequenceModell{" +
                "id='" + getId() + '\'' +
                ", tracks=" + tracks +
                ", resolution=" + resolution +
                ", division=" + division +
                ", tickLength=" + getTickLength() +
                ", tempo=" + tempo +
                '}';
    }

}
