package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.events.EventModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.core.track.interactor.converters.TrackModelltoDtoConverter;
import hu.boga.musaic.musictheory.Chord;
import hu.boga.musaic.musictheory.Pitch;
import hu.boga.musaic.musictheory.enums.ChordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TrackInteractor implements TrackBoundaryIn {

    private static final Logger LOG = LoggerFactory.getLogger(TrackInteractor.class);

    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void addChord(String trackId, int tick, int pitch, int length, ChordType chordType) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                addNotesToTrack(trackId, tick, pitch, length, chordType, sequenceModell, trackModell);
            });
        });
        showTrack(trackId);
    }

    @Override
    public void deleteNotes(String trackId, NoteDto[] notes) {
        LOG.debug("deleting notes: {}, trackid: {}", Arrays.asList(notes), trackId);
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            LOG.debug("sequence: {}", sequenceModell.getId());
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                LOG.debug("track: {}", trackModell.getId());
                Arrays.stream(notes).forEach(noteDto -> {
                    LOG.debug("deleting note: {}", noteDto.id);
                    trackModell.eventModells.removeIf(noteModell -> noteModell.getId().equals(noteDto.id));
                });
            });
        });
        showTrack(trackId);
    }

    @Override
    public void moveNote(String noteId, int newTick) {
        InMemorySequenceModellStore.getTrackByNoteId(noteId).ifPresent(trackModell -> {
            trackModell.getNoteModellById(noteId).ifPresent(noteModell -> {
                noteModell.tick = newTick;
            });
            showTrack(trackModell.getId());
        });
    }

    @Override
    public void duplicate(String trackId, int fromTick, int toTick) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {

                trackModell.getNotes().stream()
                        .filter(noteModell -> noteModell.tick >= fromTick && noteModell.tick < toTick)
                        .forEach(noteModell -> {
                            NoteModell modellToAdd = noteModell.clone();
                            modellToAdd.tick = modellToAdd.tick + sequenceModell.resolution * 4;
                            trackModell.eventModells.add(modellToAdd);
                        });

                TrackModelltoDtoConverter converter = new TrackModelltoDtoConverter(trackModell);
                TrackDto dto = converter.convert();
                boundaryOut.displayTrack(dto);
            });
        });
    }

    private Optional<TrackModell> getTrackByTrackId(String trackId) {
        return InMemorySequenceModellStore.getTrackById(trackId);
    }

    @Override
    public void showTrack(String id) {
        InMemorySequenceModellStore.getSequenceByTrackId(id).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(id).ifPresent(trackModell -> {
                boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
            });
        });
    }


    private void addNotesToTrack(String trackId, int tick, int pitch, int length, ChordType chordType, SequenceModell sequenceModell, TrackModell trackModell) {
        final int computedLength = length * sequenceModell.getTicksIn32nds();
        List<NoteModell> notes = prepareNotesToAdd(tick, pitch, chordType, computedLength, trackModell.channel);
        trackModell.eventModells.addAll(notes);
    }

    private List<NoteModell> prepareNotesToAdd(int tick, int pitch, ChordType chordType, int computedLength, int channel) {
        List<NoteModell> notes = new ArrayList<>();
        if(chordType == null){
            NoteModell note = new NoteModell(pitch, tick, computedLength, 100, channel);
            notes.add(note);
        } else {
            Chord chord = Chord.getChord(new Pitch(pitch), chordType);
            Arrays.stream(chord.getPitches()).forEach(midiCode -> {
                NoteModell note = new NoteModell(midiCode.getMidiCode(), tick, computedLength, 100, channel);
                notes.add(note);
            });
        }
        return notes;
    }
}
