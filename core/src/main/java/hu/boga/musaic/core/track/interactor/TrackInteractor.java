package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
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
import java.util.stream.Collectors;

public class TrackInteractor implements TrackBoundaryIn {

    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void addChord(String trackId, int tick, int pitch, int length, ChordType chordType) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                addNotesToTrack(tick, pitch, length, chordType, sequenceModell, trackModell);
            });
        });
        showTrack(trackId);
    }

    @Override
    public void deleteNotes(String trackId, NoteDto[] notes) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                Arrays.stream(notes).forEach(noteDto -> {
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
    public void duplicate(String trackId, String[] ids,  int fromTick, int toTick) {
        List<String> idList = Arrays.asList(ids);
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                List<NoteModell> notesToCopy = trackModell.getNotesBetween(fromTick, toTick).stream().filter(noteModell -> idList.contains(noteModell.getId())).collect(Collectors.toList());
                notesToCopy.forEach(noteModell -> {
                    NoteModell modellToAdd = noteModell.clone();
                    modellToAdd.tick = modellToAdd.tick + (toTick - fromTick);
                    trackModell.eventModells.add(modellToAdd);
                });
                boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
            });
        });
    }

    @Override
    public void moveUpAndDownNotes(String trackId, String[] ids, int move) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                Arrays.stream(ids).forEach(id -> {
                    trackModell.getNoteModellById(id).ifPresent(noteModell -> noteModell.midiCode += move);
                });
                boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
            });
        });
    }

    @Override
    public void showTrack(String id) {
        InMemorySequenceModellStore.getSequenceByTrackId(id).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(id).ifPresent(trackModell -> {
                boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
            });
        });
    }

    private void addNotesToTrack(int tick, int pitch, int length, ChordType chordType, SequenceModell sequenceModell, TrackModell trackModell) {
        final int computedLength = length * sequenceModell.getTicksIn32nds();
        List<NoteModell> notes = prepareNotesToAdd(tick, pitch, chordType, computedLength, trackModell.channel);
        trackModell.eventModells.addAll(notes);
    }

    private List<NoteModell> prepareNotesToAdd(int tick, int pitch, ChordType chordType, int computedLength, int channel) {
        List<NoteModell> notes = new ArrayList<>();
        if(chordType == null){
            NoteModell note = new NoteModell(pitch, tick, computedLength, 1, channel);
            notes.add(note);
        } else {
            Chord chord = Chord.getChord(new Pitch(pitch), chordType);
            Arrays.stream(chord.getPitches()).forEach(midiCode -> {
                NoteModell note = new NoteModell(midiCode.getMidiCode(), tick, computedLength, 1, channel);
                notes.add(note);
            });
        }
        return notes;
    }
}
