package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.EventModell;
import hu.boga.musaic.core.modell.events.ShortMessageEventModell;
import hu.boga.musaic.midigateway.utils.MidiUtil;
import hu.boga.musaic.midigateway.utils.NoteUtil;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Collection;

public class ShortMessageEventToModellConverter {
    Track track;
    public ShortMessageEventToModellConverter(Track track) {
        this.track = track;
    }

    public Collection<? extends EventModell> convert() {
        Collection<ShortMessageEventModell> retVal = new ArrayList<>();
        MidiUtil.getMidiEventsShortMessage(track).stream()
                .filter(midiEvent -> !NoteUtil.isNoteOnOffEvent(midiEvent))
                .forEach(midiEvent -> {
            ShortMessage msg = (ShortMessage) midiEvent.getMessage();
            ShortMessageEventModell modell = new ShortMessageEventModell((int) midiEvent.getTick(), msg.getChannel(), CommandEnum.byIntValue(msg.getCommand()).orElse(CommandEnum.UNKNOWN), msg.getData1(), msg.getData2());
            retVal.add(modell);
        });
        return retVal;
    }
}
