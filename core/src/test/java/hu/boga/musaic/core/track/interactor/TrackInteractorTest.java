package hu.boga.musaic.core.track.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.modell.events.NoteModell;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.musictheory.enums.ChordType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class TrackInteractorTest {

    public static final String NEW_NAME = "NEW_NAME";
    public static final String TRACK_ID = "TRACK_ID";
    TrackInteractor trackInteractor;
    private SequenceModell modell;
    private TrackModell trackModell;
    private TrackBoundaryOut boundaryOut;
    private NoteModell noteModell;


    @BeforeEach
    void setUp() {

        InMemorySequenceModellStore.clear();

        boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        TrackBoundaryIn boundaryIn = Mockito.mock(TrackBoundaryIn.class);

        trackInteractor = new TrackInteractor(boundaryOut);

        modell = new SequenceModell();
        trackModell = new TrackModell();
        modell.tracks.add(trackModell);

        noteModell = new NoteModell(12, 1, 32, 1, 0);
        trackModell.eventModells.add(noteModell);

        noteModell = new NoteModell(12, 600, 32, 1, 0);
        trackModell.eventModells.add(noteModell);

        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void addSingleNote() {
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, null);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).displayTrack(captor.capture());
        assertEquals(3, captor.getValue().notes.size());
        assertEquals(32, captor.getValue().notes.get(1).length);
    }

    @Test
    void addChord() {
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, ChordType.MAJ);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).displayTrack(captor.capture());
        assertEquals(5, captor.getValue().notes.size());
        assertEquals(512, captor.getValue().notes.get(2).length);

    }

    @Test
    void deleteNotes() {
        trackInteractor.addChord(trackModell.getId(), 345, 23, 512, null);

        NoteDto[] dtos = getNoteDtos(trackModell.eventModells.get(0).getId());

        trackInteractor.deleteNotes(trackModell.getId(), dtos);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut, times(2)).displayTrack(captor.capture());
        assertEquals(2, captor.getValue().notes.size());
    }

    private NoteDto[] getNoteDtos(String noteId) {
        NoteDto noteDto = new NoteDto();
        noteDto.midiCode = 12;
        noteDto.tick = 0;
        noteDto.id = noteId;
        NoteDto[] dtos = new NoteDto[1];
        dtos[0] = noteDto;
        return dtos;
    }

    @Test
    void movNote() {
        trackInteractor.moveNote(noteModell.getId(), 100);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
        assertEquals(100, noteModell.tick);
    }

    @Test
    void showTrack(){
        trackInteractor.showTrack(trackModell.getId());
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
    }

//    @Test
//    void duplicate(){
//        trackInteractor.duplicate(trackModell.getId(), trackModell.getNotesBetween() 1, 512);
//        assertEquals(3, trackModell.getNotes().size());
//        assertEquals(1, trackModell.getNotes().get(0).tick);
//        assertEquals(600, trackModell.getNotes().get(1).tick);
//        assertEquals(512, trackModell.getNotes().get(2).tick);
//        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
//    }

    @Test
    void moveUpAndDownNotes(){
        trackInteractor.moveUpAndDownNotes(trackModell.getId(), new String[]{noteModell.getId()}, 2);
        assertEquals(14, noteModell.midiCode);
        Mockito.verify(boundaryOut).displayTrack(Mockito.any());
    }
}