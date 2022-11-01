package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.midigateway.MidiConstants;

import javax.sound.midi.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TrackUtil extends MidiUtil {

    private TrackUtil(){
        super();
    }

    public static Optional<String> getTrackName(Track track) {
        final List<MidiEvent> events = getMetaEventsByType(track, MidiConstants.METAMESSAGE_SET_NAME);
        if (events.size() == 1) {
            final MetaMessage metaMessage = (MetaMessage) events.get(0).getMessage();
            final String name = new String(metaMessage.getData());
            return Optional.of(name);
        }
        return Optional.empty();
    }

    public static void updateTrackName(Track track, final String name) {
        final List<MidiEvent> events = getMetaEventsByType(track, MidiConstants.METAMESSAGE_SET_NAME);
        removeEvents(track, events);
        final MidiEvent event = createMetaEvent(0, MidiConstants.METAMESSAGE_SET_NAME, name.getBytes(StandardCharsets.UTF_8));
        track.add(event);
    }

    public static Optional<Integer> getChannel(Track track) {
        final List<MidiMessage> programChanges = getMidiMessagesByCommand(track, ShortMessage.PROGRAM_CHANGE);
        if (programChanges.size() == 1) {
            final ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getChannel());
        } else if (programChanges.size() == 0) {
            return Optional.empty();
        }
        throw new MusaicException("Multiple programchanges found in track");
    }

    public static Optional<Integer> getProgram(Track track) {
        final List<MidiMessage> programChanges = getMidiMessagesByCommand(track, ShortMessage.PROGRAM_CHANGE);
        if (programChanges.size() == 1) {
            final ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getData1());
        } else if (programChanges.size() == 0) {
            return Optional.empty();
        }
        throw new MusaicException("Multiple programchanges found in track");
    }

    public static void addProgramChangeEvent(Track track, final int channel, final int program, final int tick) {
        removeEvents(track, TrackUtil.getMidiEventsByCommand(track, ShortMessage.PROGRAM_CHANGE));
        addShortMessage(track, tick, ShortMessage.PROGRAM_CHANGE, channel, program, 0);
    }

}
