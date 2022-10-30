package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.gateway.MidiGateway;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.util.HashMap;
import java.util.Map;

public class MidiGatewayImpl implements MidiGateway {

    protected static final Map<String, Sequence> SEQUENCE_MAP = new HashMap<>();
    protected static final Map<String, Track> TRACK_MAP = new HashMap<>();

    @Override
    public void initMidiSequence(SequenceModell modell) {
        try {
            tryingInitializeSequence(modell);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("init midi seq failed: " + modell.getId(), e);
        }
    }

    private void tryingInitializeSequence(SequenceModell modell) throws InvalidMidiDataException {
        Sequence sequence = new Sequence(modell.division, modell.resolution);
        SEQUENCE_MAP.put(modell.getId(), sequence);
        modell.tracks.forEach(trackModell -> {
            Track track = sequence.createTrack();
            TRACK_MAP.put(trackModell.getId(), track);
        });
    }
}
