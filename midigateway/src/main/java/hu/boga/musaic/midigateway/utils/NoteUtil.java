package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.core.exceptions.MusaicException;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class NoteUtil extends MidiUtil {

    public static List<MidiEvent> getNoteOnEvents(Track track){
        List<MidiEvent> events = new ArrayList<>();
        for(int i = 0; i < track.size(); i++){
            if(isNoteOnEvent(track.get(i))){
                events.add(track.get(i));
            }
        }
        return events;
    }

    public static void addNote(final Track track, final int tick, final int pitch, final int length, int volume, int channel) {
        try {
            addShortMessage(track, tick, ShortMessage.NOTE_ON, channel, pitch, volume);
            addShortMessage(track, tick + length, ShortMessage.NOTE_OFF, channel, pitch, 0);
        } catch (final InvalidMidiDataException e) {
            throw new MusaicException(e.getMessage(), e);
        }
    }

    public static void moveNote(Track track, final int tick, final int pitch, final int newTick) {
        final int index = indexOfNoteOnEvent(track, tick, pitch);
        final MidiEvent noteOn = track.get(index);
        final MidiEvent noteOff = findMatchingNoteOff(track, index, noteOn);
        final long length = noteOff.getTick() - noteOn.getTick();
        final ShortMessage shortMessage = (ShortMessage) noteOn.getMessage();
        try {
            addShortMessage(track, newTick, ShortMessage.NOTE_ON, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
            addShortMessage(track, (int) (newTick + length), ShortMessage.NOTE_OFF, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
        } catch (final InvalidMidiDataException e) {
            e.printStackTrace();
        }
        track.remove(noteOn);
        track.remove(noteOff);
    }

    public static void deleteNote(Track track, final int tick, final int pitch) {
        final int index = indexOfNoteOnEvent(track, tick, pitch);
        final MidiEvent noteOn = track.get(index);
        final MidiEvent noteOff = findMatchingNoteOff(track, index, noteOn);
        track.remove(noteOn);
        track.remove(noteOff);

    }

    public static NoteOnOffPair getNoteOnOffPair(Track track, int tick, int pitch){
        int index = indexOfNoteOnEvent(track, tick, pitch);
        MidiEvent noteOn = track.get(index);
        MidiEvent noteOff = NoteUtil.findMatchingNoteOff(track, index, noteOn);
        return new NoteOnOffPair(noteOn, noteOff);
    }

    private static int indexOfNoteOnEvent(Track track, final int tick, final int pitch) {
        int index = 0;
        for (int i = 0; i < track.size(); i++) {
            final MidiEvent event = track.get(i);
            if (event.getTick() == tick && isNoteOnEvent(event) && getNoteValue(event) == pitch) {
                index = i;
            }
        }
        return index;
    }

    private static MidiEvent findMatchingNoteOff(Track track, int noteOnIndex, MidiEvent noteOn) {
        assert isNoteOnEvent(noteOn);
        for (int i = noteOnIndex; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (isNoteOffEvent(event)
                    && (getNoteValue(noteOn) == getNoteValue(event))) {
                return event;
            }
        }
        throw new MusaicException("unable to find matching note off event" );
    }

    private static boolean isNoteOffEvent(MidiEvent event) {
        return isNoteOffMessage(event.getMessage());
    }

    private static boolean isNoteOnMessage(MidiMessage message) {
        return message.getStatus() >= 144 && message.getStatus() < 160
                && getVelocity(message) > 0;
    }

    private static boolean isNoteOffMessage(MidiMessage message) {
        return (message.getStatus() >= 128 && message.getStatus() < 144)
                || (message.getStatus() >= 144 && message.getStatus() < 160 && getVelocity(message) == 0);
    }

    private static int getNoteValue(MidiEvent noteOnOff) {
        assert isNoteOnEvent(noteOnOff) || isNoteOffEvent(noteOnOff);

        return getNoteValue(noteOnOff.getMessage());
    }

    private static int getNoteValue(MidiMessage noteOnOff) {
        assert isNoteOnMessage(noteOnOff) || isNoteOffMessage(noteOnOff);

        return noteOnOff.getMessage()[1];
    }

    private static int getVelocity(MidiEvent noteOnOff) {
        return getVelocity(noteOnOff.getMessage());
    }

    private static int getVelocity(MidiMessage noteOnOff) {
        return noteOnOff.getMessage()[2];
    }

    private static boolean isNoteOnEvent(MidiEvent event) {
        return isNoteOnMessage(event.getMessage());
    }

    static class NoteOnOffPair{
        MidiEvent noteOn;
        MidiEvent noteOff;

        public NoteOnOffPair(MidiEvent noteOn, MidiEvent noteOff) {
            this.noteOn = noteOn;
            this.noteOff = noteOff;
        }
    }
}
