package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.core.exceptions.MusaicException;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MidiUtil {
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

    protected static void addShortMessage(final Track track, final int tick, final int command, final int channel, final int data1, final int data2) throws InvalidMidiDataException {
        final ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(command, channel, data1, data2);
        final MidiEvent event = new MidiEvent(shortMessage, tick);
        track.add(event);
    }

    //    public void updateTrackName(final String name) {
//        final List<MidiEvent> tempoEvents = this.getMetaEventsByType(Constants.METAMESSAGE_SET_NAME);
//        this.removeEvents(tempoEvents);
//
//        final MidiEvent event = this.createMetaEvent(0, Constants.METAMESSAGE_SET_NAME, name.getBytes(StandardCharsets.UTF_8));
//        this.track.add(event);
//
//    }
//
//    private List<ShortMessage> getShortMessagesByCommand(final int command) {
//        final List<ShortMessage> retVal = new ArrayList<>();
//        this.getEventsByCommand(command).forEach(midiEvent -> {
//            final ShortMessage msg = (ShortMessage) midiEvent.getMessage();
//            retVal.add(msg);
//        });
//        return retVal;
//    }

//    public void addChord(final int tick, final int pitch, final int length, final ChordType chordType) {
//        final Chord chord = Chord.getChord(new Pitch(pitch), chordType);
//        Arrays.stream(chord.getPitches()).forEach(pitch1 -> {
//            this.addNote(tick, pitch1.getMidiCode(), length);
//        });
//    }

}
