package hu.boga.musaic.midigateway.sequence;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.converters.NoteToModellConverter;
import hu.boga.musaic.midigateway.converters.SequenceToModellConverter;
import hu.boga.musaic.midigateway.converters.TrackToModellConverter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SequenceReader {
    public SequenceModell open(String path) {
        try {
            return tryingToOpen(path);
        } catch (InvalidMidiDataException | IOException e) {
            throw new MusaicException(e.getMessage(), e);
        }
    }

    private SequenceModell tryingToOpen(String path) throws InvalidMidiDataException, IOException {
        File file = new File(path);
        Sequence sequence = MidiSystem.getSequence(file);
        return convertSquence(sequence);
    }

    private SequenceModell convertSquence(Sequence sequence) {
        SequenceModell sequenceModell = new SequenceToModellConverter(sequence).convert();
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            sequenceModell.tracks.add(convertTracks(track));
        });
        return sequenceModell;
    }

    private TrackModell convertTracks(Track track) {
        TrackModell trackModell = new TrackToModellConverter(track).convert();
        trackModell.notes = new NoteToModellConverter(track).convert();
        return trackModell;
    }
}
