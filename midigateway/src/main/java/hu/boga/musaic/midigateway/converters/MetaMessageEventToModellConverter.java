package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.EventModell;
import hu.boga.musaic.core.modell.events.MetaMessageEventModell;
import hu.boga.musaic.midigateway.utils.MidiUtil;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Collection;

public class MetaMessageEventToModellConverter {

    Track track;

    public MetaMessageEventToModellConverter(Track track) {
        this.track = track;
    }

    public Collection<? extends EventModell> convert() {
        Collection<MetaMessageEventModell> retVal = new ArrayList<>();
        MidiUtil.getMidiEventsMetaMessage(track).forEach(midiEvent -> {
            MetaMessage msg = (MetaMessage) midiEvent.getMessage();
            MetaMessageEventModell modell = new MetaMessageEventModell(midiEvent.getTick(), msg.getData(), CommandEnum.byIntValue(msg.getType()).orElse(CommandEnum.UNKNOWN));
            retVal.add(modell);
        });
        return retVal;
    }
}
