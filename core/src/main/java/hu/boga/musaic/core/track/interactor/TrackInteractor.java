package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.NoteModell;
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

public class TrackInteractor implements TrackBoundaryIn {

    private static final Logger LOG = LoggerFactory.getLogger(TrackInteractor.class);

    TrackGateway gateway;
    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackGateway gateway, TrackBoundaryOut boundaryOut) {
        this.gateway = gateway;
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void updateTrackName(TrackDto trackDto) {
        InMemorySequenceModellStore.getTrackById(trackDto.id).ifPresent(trackModell -> {
            trackModell.name = trackDto.name;
            gateway.updateTrackName(trackDto.id, trackDto.name);
        });
    }

    @Override
    public void removeTrack(String trackId) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.tracks.removeIf(trackModell -> trackModell.getId().equals(trackId));
            gateway.removeTrack(sequenceModell.getId(), trackId);
        });
    }

    @Override
    public void updateTrackProgram(String trackId, int program, int channel) {
        LOG.debug("updating track: {} program: {} channel: {}", trackId, program, channel);
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            trackModell.program = program;
            trackModell.channel = channel;
            gateway.updateTrackProgram(trackId, program, channel);
        });
    }

    @Override
    public void addChord(String trackId, int tick, int pitch, int length, int channel, ChordType chordType) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                addNotesToTrack(trackId, tick, pitch, length, channel, chordType, sequenceModell, trackModell);
            });
            LOG.debug("TRACK: {}", sequenceModell.getTrackById(trackId));
        });
    }

    @Override
    public void deleteNotes(String trackId, NoteDto[] notes) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
                Arrays.stream(notes).forEach(noteDto -> {
                    LOG.debug("deleting note: {}", noteDto.id);
                    trackModell.notes.removeIf(noteModell -> noteModell.getId().equals(noteDto.id));
                    gateway.deleteNote(trackId, noteDto.tick, noteDto.midiCode);
                });
                boundaryOut.setTrackDto(new TrackModelltoDtoConverter(trackModell).convert(), sequenceModell.resolution);
            });
            LOG.debug("TRACK: {}", sequenceModell.getTrackById(trackId));
        });
    }

    @Override
    public void moveNote(String noteId, int newTick) {
        InMemorySequenceModellStore.getTrackByNoteId(noteId).ifPresent(trackModell -> {
            trackModell.gtNoteModellById(noteId).ifPresent(noteModell -> {
                gateway.moveNote(trackModell.getId(), (int) noteModell.tick, noteModell.midiCode, newTick);
                noteModell.tick = newTick;
            });
            LOG.debug("TRACK: {}", trackModell);

        });
    }

    @Override
    public void showTrack(String id) {
        InMemorySequenceModellStore.getSequenceByTrackId(id).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(id).ifPresent(trackModell -> {
                boundaryOut.setTrackDto(new TrackModelltoDtoConverter(trackModell).convert(), sequenceModell.resolution);
            });
        });

    }

    private void addNotesToTrack(String trackId, int tick, int pitch, int length, int channel, ChordType chordType, SequenceModell sequenceModell, TrackModell trackModell) {
        final int computedLength = length * sequenceModell.getTicksIn32nds();
        List<NoteModell> notes = getNotesToAdd(tick, pitch, chordType, computedLength, channel);
        trackModell.notes.addAll(notes);
        gateway.addNotesToTrack(trackId, notes);
        boundaryOut.setTrackDto(new TrackModelltoDtoConverter(trackModell).convert(), sequenceModell.resolution);
    }

    private List<NoteModell> getNotesToAdd(int tick, int pitch, ChordType chordType, int computedLength, int channel) {
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
