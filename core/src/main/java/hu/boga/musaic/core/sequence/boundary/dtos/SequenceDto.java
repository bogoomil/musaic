package hu.boga.musaic.core.sequence.boundary.dtos;

import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

import java.util.List;

public class SequenceDto {

    public String id;
    public String name;
    public List<TrackDto> tracks;
    public int resolution;
    public float division;
    public float tempo;

    public long tickLength;
    public int ticksPerMeasure;
    public int ticksIn32nds;
    public float tickSize;
    public float ticksPerSecond;

    public int[] channelToProgramMappings;
    public String [] channelToColorMappings;



}
