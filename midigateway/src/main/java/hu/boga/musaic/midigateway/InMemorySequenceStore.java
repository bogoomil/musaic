package hu.boga.musaic.midigateway;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.util.HashMap;
import java.util.Map;

public class InMemorySequenceStore {
    public static final Map<String, Sequence> SEQUENCE_MAP = new HashMap<>();
    public static final Map<String, Track> TRACK_MAP = new HashMap<>();
}
