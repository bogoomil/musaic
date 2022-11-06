package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import hu.boga.musaic.midigateway.utils.TrackUtil;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class SequenceModellToSequenceConverter {
    private SequenceModell modell;
    public SequenceModellToSequenceConverter(SequenceModell modell) {
        this.modell = modell;
    }

    public Sequence convert() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(modell.division, modell.resolution);
        modell.tracks.forEach(trackModell -> {
            convertTracks(sequence, trackModell);
        });
        return sequence;
    }

    private void convertTracks(Sequence sequence, TrackModell trackModell) {
        Track track = sequence.createTrack();
        TrackUtil.updateTrackName(track, trackModell.name);
        TrackUtil.addProgramChangeEvent(track, trackModell.channel, trackModell.program, 0);
        trackModell.notes.forEach(noteModell -> {
            NoteUtil.addNote(track, (int) noteModell.tick, noteModell.midiCode, (int) noteModell.length, noteModell.velocity, noteModell.channel);
        });
    }
}
