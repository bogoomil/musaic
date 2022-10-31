package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.midigateway.MidiConstants;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.util.Arrays;
import java.util.List;

public class TempoUtil extends MidiUtil {

    private TempoUtil() {
        super();
    }

    public static void addTempoEvents(Sequence sequence, int tempo) {
        Arrays.stream(sequence.getTracks()).forEach(track -> addTempoEvents(track, tempo));
    }

    public static void removeTempoEvents(Sequence sequence) {
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            final List<MidiEvent> tempoEvents = getMetaEventsByType(track, MidiConstants.METAMESSAGE_SET_TEMPO);
            tempoEvents.forEach(track::remove);
        });

    }

    public static int getTempo(Sequence sequence) {
        List<MidiEvent> tempoEvents = getMetaEventsByType(sequence, MidiConstants.METAMESSAGE_SET_TEMPO);
        if (tempoEvents.isEmpty()) {
            return 0;
        } else {
            return getTempoInBPM((MetaMessage) tempoEvents.get(0).getMessage());
        }
    }

    private static void addTempoEvents(Track track, final long tempo) {
        final long microSecsPerQuarterNote = MidiConstants.MICROSECONDS_IN_MINUTE / tempo;
        final byte[] array = {0, 0, 0};
        for (int i = 0; i < 3; i++) {
            final int shift = (3 - 1 - i) * 8;
            array[i] = (byte) (microSecsPerQuarterNote >> shift);
        }
        track.add(createMetaEvent(0, MidiConstants.METAMESSAGE_SET_TEMPO, array));
    }

    private static Integer getTempoInBPM(MetaMessage mm) {
        byte[] data = mm.getData();
        if (mm.getType() != 81 || data.length != 3) {
            throw new IllegalArgumentException("mm=" + mm);
        }
        int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
        return Math.round(60000001f / mspq);
    }
}
