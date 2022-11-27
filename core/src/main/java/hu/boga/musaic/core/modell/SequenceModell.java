package hu.boga.musaic.core.modell;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.ShortMessageEventModell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class SequenceModell extends BaseModell{

    public static final int DEFAULT_TEMPO = 120;
    public static final int DEFAULT_DIVISION = 0;
    public static final int DEFAULT_RESOLUTION = 128;

    public List<TrackModell> tracks = new ArrayList();
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

    public String name;

    public String[] channelToColorMapping = new String[]{"0xccffffff","0xcce6ffff","0xe6ccffff","0xffccffff","0xffcce6ff","0xffcce6ff","0xffccccff","0xffccb3ff","0xffe6ccff","0xffffb3ff","0xffffccff","0xe6e6ccff","0xccffccff","0xe6e680ff","0xffe666ff","0xffcc80ff"};

    public long getTickLength() {
        return tracks.stream().mapToLong(trackModell -> trackModell.getTickLength()).max().orElse(0);
    }

    public Optional<TrackModell> getTrackById(String trackId){
        return tracks.stream().filter(trackModell -> trackModell.getId().equals(trackId)).findFirst();
    }

    public int[] getChannelToProgramMappings(){
        int[] channelToProgram = new int[16];
        tracks.forEach(trackModell -> {
            trackModell.getShortMessageEventsByCommand(CommandEnum.PROGRAM_CHANGE).forEach(shortMessageEventModell -> {
                channelToProgram[shortMessageEventModell.channel] = shortMessageEventModell.data1;
            });
        });
        return channelToProgram;
    }

    public void updateChannelToProgramMapping(int channel, int selectedProgram) {
        getProgramChangeEvent(channel)
                .ifPresentOrElse(shortMessageEventModell -> shortMessageEventModell.data1 = selectedProgram,
                        () -> createProgramChangeEvent(channel, selectedProgram));
    }

    private void createProgramChangeEvent(int channel, int selectedProgram) {
        ShortMessageEventModell shortMessageEventModell = new ShortMessageEventModell(0, channel, CommandEnum.PROGRAM_CHANGE, selectedProgram, 0);
        tracks.get(0).eventModells.add(shortMessageEventModell);
    }

    private Optional<ShortMessageEventModell> getProgramChangeEvent(int channel) {
        return tracks
                .stream()
                .flatMap(trackModell -> trackModell.getShortMessageEventsByCommand(CommandEnum.PROGRAM_CHANGE)
                        .stream())
                .filter(shortMessageEventModell -> shortMessageEventModell.channel == channel).findFirst();
    }
}
