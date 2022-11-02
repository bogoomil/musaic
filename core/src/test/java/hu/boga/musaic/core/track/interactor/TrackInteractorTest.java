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
import hu.boga.musaic.musictheory.enums.ChordType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class TrackInteractorTest {

    public static final String NEW_NAME = "NEW_NAME";
    public static final String TRACK_ID = "TRACK_ID";
    TrackInteractor trackInteractor;
    private TrackGateway gateway;
    private SequenceModell modell;
    private TrackModell trackModell;
    private TrackBoundaryOut boundaryOut;


    @BeforeEach
    void setUp() {

        InMemorySequenceModellStore.clear();

        boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        TrackBoundaryIn boundaryIn = Mockito.mock(TrackBoundaryIn.class);
        gateway = Mockito.mock(TrackGateway.class);

        trackInteractor = new TrackInteractor(gateway, boundaryOut);

        modell = new SequenceModell();
        trackModell = new TrackModell();
        modell.tracks.add(trackModell);

        NoteModell noteModell = new NoteModell(12, 0, 32, 100, 0);
        trackModell.notes.add(noteModell);

        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void updateTrackName() {
        TrackDto dto = new TrackDto();
        dto.id = trackModell.getId();
        dto.name = NEW_NAME;
        trackInteractor.updateTrackName(dto);
        Mockito.verify(gateway).updateTrackName(dto.id, dto.name);

        assertEquals(NEW_NAME, trackModell.name);

    }

    @Test
    void removeTrack(){
        modell.tracks.add(new TrackModell());

        try (MockedStatic<InMemorySequenceModellStore> mockedStatic = Mockito.mockStatic(InMemorySequenceModellStore.class)) {
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceByTrackId(trackModell.getId())).thenReturn(Optional.of(modell));
            mockedStatic.when(() -> InMemorySequenceModellStore.getSequenceById(modell.getId())).thenReturn(modell);
            trackInteractor.removeTrack(trackModell.getId());
            Mockito.verify(gateway).removeTrack(modell.getId(), trackModell.getId());

            assertEquals(1, modell.tracks.size());
        }
    }

    @Test
    void updateTrackProgram(){
        trackInteractor.updateTrackProgram(trackModell.getId(), 3,4);
        Mockito.verify(gateway).updateTrackProgram(trackModell.getId(), 3, 4);

        assertEquals(3, trackModell.program);
        assertEquals(4, trackModell.channel);

    }

    @Test
    void addSingleNote(){
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, 0, null);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).setTrackDto(captor.capture(), eq(modell.resolution));
        assertEquals(2, captor.getValue().notes.size());
        assertEquals(512, captor.getValue().notes.get(1).length);
    }

    @Test
    void addChord(){
        trackInteractor.addChord(trackModell.getId(), 0, 12, 32, 0, ChordType.MAJ);
        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut).setTrackDto(captor.capture(), eq(modell.resolution));
        Mockito.verify(gateway).addNotesToTrack(eq(trackModell.getId()), Mockito.any());
        assertEquals(4, captor.getValue().notes.size());

    }

    @Test
    void deleteNotes(){
        NoteDto noteDto = new NoteDto();
        noteDto.midiCode = 12;
        noteDto.tick = 0;
        NoteDto[] dtos = new NoteDto[1];
        dtos[0] = noteDto;

        trackInteractor.addChord(trackModell.getId(), 345, 23, 512, 0, null);

        trackInteractor.deleteNotes(trackModell.getId(), dtos);

        ArgumentCaptor<TrackDto> captor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(boundaryOut, times(2)).setTrackDto(captor.capture(), eq(modell.resolution));
        Mockito.verify(gateway).deleteNote(trackModell.getId(), 0, 12);

        assertEquals(1, captor.getValue().notes.size());

    }
}