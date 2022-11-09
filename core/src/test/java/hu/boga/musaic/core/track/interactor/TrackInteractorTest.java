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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;

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

        noteModell = new NoteModell(12, 0, 32, 100, 0);
        trackModell.eventModells.add(noteModell);

        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void updateTrackName() {
        TrackDto dto = new TrackDto();
        dto.id = trackModell.getId();
        dto.name = NEW_NAME;
        trackInteractor.updateTrackName(dto);
        Mockito.verify(boundaryOut).setTrackDto(Mockito.any(), eq(modell.resolution));

        assertEquals(NEW_NAME, trackModell.getName());

    }

    @Test
    void removeTrack() {
        modell.tracks.add(new TrackModell());

        try (MockedStatic<InMemorySequenceModellStore> mockedStatic = Mockito.mockStatic(InMemorySequenceModellStore.class)) {
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceByTrackId(trackModell.getId())).thenReturn(Optional.of(modell));
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceById(modell.getId())).thenReturn(modell);
            trackInteractor.removeTrack(trackModell.getId());

            assertEquals(1, modell.tracks.size());
        }
    }

    @Test
    void updateTrackhannel() {
        trackInteractor.updateTrackChannel(trackModell.getId(), 4);
        Mockito.verify(boundaryOut).setTrackDto(Mockito.any(), eq(modell.resolution));
        assertEquals(4, trackModell.channel);
        assertEquals(4, noteModell.channel);

    }

    @Test
    void addSingleNote() {
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, 0, null);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).setTrackDto(captor.capture(), eq(modell.resolution));
        assertEquals(2, captor.getValue().notes.size());
        assertEquals(512, captor.getValue().notes.get(1).length);
    }

    @Test
    void addChord() {
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, 0, ChordType.MAJ);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).setTrackDto(captor.capture(), eq(modell.resolution));
        assertEquals(4, captor.getValue().notes.size());

    }

    @Test
    void deleteNotes() {
        trackInteractor.addChord(trackModell.getId(), 345, 23, 512, 0, null);

        NoteDto[] dtos = getNoteDtos(trackModell.eventModells.get(0).getId());

        trackInteractor.deleteNotes(trackModell.getId(), dtos);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut, times(2)).setTrackDto(captor.capture(), eq(modell.resolution));
        assertEquals(1, captor.getValue().notes.size());
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
        Mockito.verify(boundaryOut).setTrackDto(Mockito.any(), eq(modell.resolution));
        assertEquals(100, noteModell.tick);
    }

    @Test
    void showTrack(){
        trackInteractor.showTrack(trackModell.getId());
        Mockito.verify(boundaryOut).setTrackDto(Mockito.any(), eq(modell.resolution));
    }

    @Test
    void setMuted(){
        trackInteractor.setMuted(trackModell.getId(), true);
        assertTrue(trackModell.muted);
    }
}