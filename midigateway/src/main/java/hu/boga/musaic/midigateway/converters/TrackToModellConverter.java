package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.utils.TrackUtil;

import javax.sound.midi.Track;

public class TrackToModellConverter {
    Track track;
    public TrackToModellConverter(Track track) {
        this.track = track;
    }

    public TrackModell convert() {
        TrackModell trackModell = new TrackModell();
        TrackUtil.getTrackName(track).ifPresent(name -> trackModell.name = name);
        TrackUtil.getChannel(track).ifPresent(channel -> trackModell.channel = channel);
        TrackUtil.getProgram(track).ifPresent(program -> trackModell.program = program);

        return trackModell;
    }
}
