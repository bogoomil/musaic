package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.core.exceptions.MusaicException;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MidiUtil {

    MidiUtil(){
        throw new UnsupportedOperationException();
    }

    protected static MidiEvent createMetaEvent(final long tick, final int type, final byte[] array) {
        final MetaMessage metaMessage = new MetaMessage();
        try {
            metaMessage.setMessage(type, array, array.length);
        } catch (final Exception e) {
            throw new MusaicException("unable to set message: " + e.getMessage(), e);
        }
        return new MidiEvent(metaMessage, tick);
    }

    protected static List<MidiEvent> getMetaEventsByType(Sequence sequence, int type) {
        return Arrays.stream(sequence.getTracks())
                .flatMap(track -> getMetaEventsByType(track, type).stream())
                .collect(Collectors.toList());
    }

    protected static List<MidiEvent> getMetaEventsByType(Track track, int type) {
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == type) {
                events.add(event);
            }
        }
        return events;
    }

    protected static void addShortMessage(final Track track, final int tick, final int command, final int channel, final int data1, final int data2) {
        final ShortMessage shortMessage = new ShortMessage();
        try {
            shortMessage.setMessage(command, channel, data1, data2);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException(e.getMessage(), e);
        }
        final MidiEvent event = new MidiEvent(shortMessage, tick);
        track.add(event);
    }

    public static void removeEvents(Track track, final List<MidiEvent> events) {
        events.forEach(track::remove);
    }

    protected static List<MidiMessage> getMidiMessagesByCommand(final Track track, final int command) {
        return getMidiEventsByCommand(track, command).stream().map(MidiEvent::getMessage).collect(Collectors.toList());
    }

    public static List<MidiEvent> getMidiEventsByCommand(final Track track, final int command) {
        final List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            final MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                final ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(event);
                }
            }
        }
        return retVal;
    }
}
