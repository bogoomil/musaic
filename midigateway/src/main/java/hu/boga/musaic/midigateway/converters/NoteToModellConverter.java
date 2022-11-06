package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.midigateway.utils.MidiUtil;
import hu.boga.musaic.midigateway.utils.NoteUtil;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

public class NoteToModellConverter {
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
            NoteModell modell = new NoteModell(pitch, event.getTick(), length, NoteUtil.getVelocity(event.getMessage()), ((ShortMessage)event.getMessage()).getChannel());
            notes.add(modell);
        });
        return notes;
    }
}
