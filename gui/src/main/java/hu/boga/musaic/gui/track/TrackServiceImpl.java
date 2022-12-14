package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.note.NoteBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.logic.Observable;
import hu.boga.musaic.musictheory.enums.ChordType;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TrackServiceImpl implements TrackService, TrackBoundaryOut {

    TrackBoundaryIn boundaryIn;
    NoteBoundaryIn noteBoundaryIn;
    TrackDto dto;

    private final Map<String, Observable<TrackModell>> observableMap = new HashMap<>();

    @Inject
    public TrackServiceImpl(TrackBoundaryIn boundaryIn, NoteBoundaryIn noteBoundaryIn) {
        this.boundaryIn = boundaryIn;
        this.noteBoundaryIn = noteBoundaryIn;
    }

    @Override
    public void displayTrack(TrackDto trackDto) {
        observableMap.get(trackDto.id).setValue(new TrackModell(trackDto));
    }

    @Override
    public void updateVolume(String id, double v) {
        boundaryIn.updateVolume(id, v);
    }

    @Override
    public void updateName(String id, String newName) {
        TrackDto dto = new TrackDto();
        dto.id = id;
        dto.name = newName;
        boundaryIn.updateTrackName(dto);
    }

    @Override
    public void updateChannel(String id, int newValue) {
        boundaryIn.updateTrackChannel(id, newValue);
    }

    @Override
    public void mute(String id, boolean value) {
        boundaryIn.setMuted(id, value);
    }

    @Override
    public void load(String trackId) {
        boundaryIn.load(trackId);
    }

    @Override
    public void addObservable(Observable<TrackModell> observable) {
        observableMap.put(observable.getName(), observable);
    }

    @Override
    public void addChord(String trackId, int tick, int pitch, int length, ChordType chordType) {
        boundaryIn.addChord(trackId, tick, pitch, length, chordType);
    }

    @Override
    public void noteVolumeChanged(String noteId, double v) {
        boundaryIn.noteVolumeChanged(noteId, v);
    }

    @Override
    public void noteDeleted(String trackId, NoteDto[] notes) {
        boundaryIn.deleteNotes(trackId, notes);
    }

    @Override
    public void updateNoteTick(String noteId, int increment) {
        boundaryIn.moveNote(noteId, increment);
    }

    @Override
    public void updateNotePitch(String trackId, String[] ids, int move){
        boundaryIn.moveUpAndDownNotes(trackId, ids, move);
    }

    @Override
    public void duplicateSelection(String trackId, int start, int end) {
        boundaryIn.duplicate(trackId, start, end);
    }

    @Override
    public void playChord(String trackId, int midiCode, int length) {
        noteBoundaryIn.play(trackId, midiCode, length);
    }

    @Override
    public void solo(String trackId, boolean solo) {
        boundaryIn.setSolo(trackId, solo);
    }
}
