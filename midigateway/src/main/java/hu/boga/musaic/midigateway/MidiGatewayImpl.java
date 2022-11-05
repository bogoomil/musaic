package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.gateway.MidiGateway;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MidiGatewayImpl implements MidiGateway {

    private static final Logger LOG = LoggerFactory.getLogger(MidiGatewayImpl.class);

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
        Player.playSequence(InMemorySequenceStore.SEQUENCE_MAP.get(sequenceId));
    }

    @Override
    public void updateTempo(SequenceModell modell) {
        Sequence sequence = InMemorySequenceStore.SEQUENCE_MAP.get(modell.getId());
        TempoUtil.removeTempoEvents(sequence);
        TempoUtil.addTempoEvents(sequence, (int) modell.tempo);
    }

    @Override
    public void save(String sequenceId, String path) {
        Saver.save(InMemorySequenceStore.SEQUENCE_MAP.get(sequenceId), path);
    }

    @Override
    public void addTrack(SequenceModell modell) {
        Sequence sequence = InMemorySequenceStore.SEQUENCE_MAP.get(modell.getId());
        Track newTrack = sequence.createTrack();
        modell.tracks.forEach(trackModell -> {
            if(!InMemorySequenceStore.TRACK_MAP.containsKey(trackModell.getId())){
                InMemorySequenceStore.TRACK_MAP.put(trackModell.getId(), newTrack);
            }
        });
    }

    @Override
    public void stop() {
        Player.stopPlayback();

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
        InMemorySequenceStore.SEQUENCE_MAP.put(sequenceModell.getId(), sequence);
        return sequenceModell;
    }

    private void convertTracks(Sequence sequence, SequenceModell sequenceModell) {
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            TrackModell modell = new TrackToModellConverter(track).convert();
            sequenceModell.tracks.add(modell);
            InMemorySequenceStore.TRACK_MAP.put(modell.getId(), track);
            convertNotes(track, modell);
        });
    }

    private void convertNotes(Track track, TrackModell modell) {
        modell.notes = new NoteToModellConverter(track).convert();
    }

    private void tryingInitializeSequence(SequenceModell modell) throws InvalidMidiDataException {
        Sequence sequence = new Sequence(modell.division, modell.resolution);
        InMemorySequenceStore.SEQUENCE_MAP.put(modell.getId(), sequence);
        modell.tracks.forEach(trackModell -> {
            Track track = sequence.createTrack();
            InMemorySequenceStore.TRACK_MAP.put(trackModell.getId(), track);
        });
    }

}
