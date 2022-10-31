package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.gateway.MidiGateway;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    @Override
    public SequenceModell open(String path) {
        try {
            return tryingToOpen(path);
        } catch (InvalidMidiDataException | IOException e) {
            throw new MusaicException(e.getMessage(), e);
        }
    }

    @Override
    public void play(String sequenceId) {
        Player.playSequence(SEQUENCE_MAP.get(sequenceId));
    }

    private SequenceModell tryingToOpen(String path) throws InvalidMidiDataException, IOException {
        File file = new File(path);
        Sequence sequence = MidiSystem.getSequence(file);
        SequenceModell sequenceModell = convertSquence(sequence);
        return sequenceModell;
    }

    private SequenceModell convertSquence(Sequence sequence) {
        SequenceModell sequenceModell = new SequenceToModellConverter(sequence).convert();
        convertTracks(sequence, sequenceModell);
        SEQUENCE_MAP.put(sequenceModell.getId(), sequence);
        return sequenceModell;
    }

    private void convertTracks(Sequence sequence, SequenceModell sequenceModell) {
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            TrackModell modell = new TrackToModellConverter(track).convert();
            sequenceModell.tracks.add(modell);
            TRACK_MAP.put(modell.getId(), track);
            convertNotes(track, modell);
        });
    }

    private void convertNotes(Track track, TrackModell modell) {
        modell.notes = new NoteToModellConverter(track).convert();
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
