package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.NoteModell;
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
import java.util.stream.Collectors;

public class TrackInteractor implements TrackBoundaryIn {
    private static final Logger LOG = LoggerFactory.getLogger(TrackInteractor.class);

    private TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void setMuted(String id, boolean muted) {
        InMemorySequenceModellStore.getSequenceByTrackId(id).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(id).ifPresent(trackModell -> {
                trackModell.muted = muted;
                LOG.debug("track muted: {}, val: {}", id, muted);
            });
        });
    }

    @Override
    public void load(String trackId) {
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    @Override
    public void updateTrackChannel(String trackId, int channel) {
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            trackModell.channel = channel;
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    @Override
    public void updateTrackName(TrackDto trackDto) {
        InMemorySequenceModellStore.getTrackById(trackDto.id).ifPresent(trackModell -> {
            LOG.debug("updating track name: {}", trackDto.name);
            trackModell.setName(trackDto.name);
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    @Override
    public void updateVolume(String trackId, double velocityPercent) {
        InMemorySequenceModellStore.getTrackById(trackId).ifPresent(trackModell -> {
            trackModell.getNotes().forEach(noteModell -> noteModell.velocity = calcNewVelocity(noteModell.velocity, velocityPercent));
            LOG.debug("updating volume: {}, vol: {}", trackId, velocityPercent);
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });
    }

    private double calcNewVelocity(double current, double percent){
        double velocity = current + percent;
        return isVelocityOutOfBounds(velocity) ? current : velocity;
    }

    private boolean isVelocityOutOfBounds(double velocity){
        return velocity < 0 || velocity > 1;
    }


    @Override
    public void addChord(String trackId, int tick, int pitch, int length, ChordType chordType) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                addNotesToTrack(tick, pitch, length, chordType, sequenceModell, trackModell);
            });
        });
        load(trackId);
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
        load(trackId);
    }

    @Override
    public void moveNote(String noteId, int newTick) {
        InMemorySequenceModellStore.getTrackByNoteId(noteId).ifPresent(trackModell -> {
            trackModell.getNoteModellById(noteId).ifPresent(noteModell -> {
                noteModell.tick = newTick;
            });
            load(trackModell.getId());
        });
    }

    @Override
    public void duplicate(String trackId,  int fromTick, int toTick) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.getTrackById(trackId).ifPresent(trackModell -> {
                List<NoteModell> notesToCopy = trackModell.getNotesBetween(fromTick, toTick).stream().collect(Collectors.toList());
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
    public void noteVolumeChanged(String noteId, double v) {
        InMemorySequenceModellStore.getTrackByNoteId(noteId).ifPresent(trackModell -> {
            trackModell
                    .getNotes().stream()
                    .filter(noteModell -> noteId.equals(noteModell.getId()))
                    .findFirst()
                    .ifPresent(noteModell -> {
                        noteModell.velocity = v;
                        LOG.debug("setting note {}, volume to: {}", noteId, v);
                    });
            boundaryOut.displayTrack(new TrackModelltoDtoConverter(trackModell).convert());
        });

    }

    @Override
    public void setSolo(String trackId, boolean solo) {
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            sequenceModell.tracks.forEach(trackModell -> {
                if(solo){
                    if(trackModell.getId().equals(trackId)){
                        trackModell.muted = false;
                    } else {
                        trackModell.muted = true;
                    }
                } else {
                    trackModell.muted = false;
                }
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
        if(chordType == ChordType.NONE){
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
