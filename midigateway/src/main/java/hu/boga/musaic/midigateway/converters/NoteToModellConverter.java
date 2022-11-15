package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

public class NoteToModellConverter {
    private static final Logger LOG = LoggerFactory.getLogger(NoteToModellConverter.class);
    Track track;
    public NoteToModellConverter(Track track) {
        this.track = track;
    }

    public List<NoteModell> convert() {
        List<NoteModell> notes = new ArrayList<>();
        NoteUtil.getNoteOnEvents(track).forEach(event -> {
            int pitch = ((ShortMessage) event.getMessage()).getData1();
            NoteUtil.NoteOnOffPair pair = NoteUtil.getNoteOnOffPair(track, (int) event.getTick(), pitch);
            long length = pair.noteOff.getTick() - pair.noteOn.getTick();
            int value = NoteUtil.getVelocity(event.getMessage());
            double percent = ((double)value)  / 127;
            NoteModell modell = new NoteModell(pitch, event.getTick(), length, percent, ((ShortMessage)event.getMessage()).getChannel());
            notes.add(modell);
        });
        return notes;
    }


}
