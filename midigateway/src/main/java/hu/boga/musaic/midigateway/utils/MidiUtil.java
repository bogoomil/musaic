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

    public static MidiEvent createMidiEventMetaMessage(final long tick, final int type, final byte[] array) {
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
        return getMidiEventsMetaMessage(track)
                .stream()
                .filter(midiEvent -> ((MetaMessage)midiEvent.getMessage()).getType() == type)
                .collect(Collectors.toList());
    }

    protected static void addMidiEventShortMessage(final Track track, final int tick, final int command, final int channel, final int data1, final int data2) {
        final MidiEvent event = createMidiEventShortMessage(tick, command, channel, data1, data2);
        track.add(event);
    }

    protected static void addMidiEventMetaMessage(final Track track, int tick, int type, byte[] data){
        final MidiEvent event = createMidiEventMetaMessage(tick, type, data);
    }

    public static MidiEvent createMidiEventShortMessage(int tick, int command, int channel, int data1, int data2) {
        final ShortMessage shortMessage = new ShortMessage();
        try {
            shortMessage.setMessage(command, channel, data1, data2);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException(e.getMessage(), e);
        }
        final MidiEvent event = new MidiEvent(shortMessage, tick);
        return event;
    }

//    public static void removeEvents(Track track, final List<MidiEvent> events) {
//        events.forEach(track::remove);
//    }

//    protected static List<MidiMessage> getShortMessagesByCommand(final Track track, final int command) {
//        return getMidiEventsByShortMessageCommand(track, command).stream().map(MidiEvent::getMessage).collect(Collectors.toList());
//    }

//    public static List<MidiEvent> getMidiEventsByShortMessageCommand(final Track track, final int command) {
//        return getMidiEventsShortMessage(track)
//                .stream().filter(midiEvent -> ((ShortMessage)midiEvent.getMessage()).getCommand() == command)
//                .collect(Collectors.toList());
//    }

    public static List<MidiEvent> getMidiEventsShortMessage(Track track){
        final List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            final MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                retVal.add(event);
            }
        }
        return retVal;
    }

    public static List<MidiEvent> getMidiEventsMetaMessage(Track track){
        final List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            final MidiEvent event = track.get(i);
            if (event.getMessage() instanceof MetaMessage) {
                retVal.add(event);
            }
        }
        return retVal;
    }
}
