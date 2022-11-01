package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.gateway.MidiGateway;
import hu.boga.musaic.core.sequence.boundary.dtos.TrackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class SequenceInteractorTest {

    public static final String SEQUENCE_ID = "SEQUENCE_ID";
    public static final String PATH = "PATH";

    private SequenceInteractor interactor;
    private ArgumentCaptor<SequenceDto> sequenceDtoArgumentCaptor;
    private ArgumentCaptor<TrackDto> trackDtoArgumentCaptor;
    private ArgumentCaptor<SequenceModell> sequenceModellArgumentCaptor;
    private SequenceBoundaryOut boundaryOut;

    private MidiGateway gateway;

    @BeforeEach
    void setUp() {
        boundaryOut = Mockito.mock(SequenceBoundaryOut.class);
        gateway = Mockito.mock(MidiGateway.class);
        sequenceDtoArgumentCaptor = ArgumentCaptor.forClass(SequenceDto.class);
        sequenceModellArgumentCaptor = ArgumentCaptor.forClass(SequenceModell.class);
        trackDtoArgumentCaptor = ArgumentCaptor.forClass(TrackDto.class);
        interactor = new SequenceInteractor(boundaryOut, gateway);
    }

    @Test
    void createSequence() {
        interactor.create();
        Mockito.verify(gateway).initMidiSequence(sequenceModellArgumentCaptor.capture());
        Mockito.verify(boundaryOut).displaySequence(sequenceDtoArgumentCaptor.capture());
        assertNotNull(sequenceModellArgumentCaptor.getValue());
        assertNotNull(sequenceDtoArgumentCaptor.getValue());
    }

    @Test
    void load(){
        interactor.create();
        Mockito.verify(gateway).initMidiSequence(sequenceModellArgumentCaptor.capture());
        assertNotNull(sequenceModellArgumentCaptor.getValue());
        SequenceModell createdModell = sequenceModellArgumentCaptor.getValue();

        interactor.load(createdModell.getId());
        Mockito.verify(boundaryOut, times(2)).displaySequence(sequenceDtoArgumentCaptor.capture());

        assertEquals(createdModell.getId(), sequenceDtoArgumentCaptor.getValue().id);
    }

    @Test
    void play(){
        interactor.play(SEQUENCE_ID);
        ArgumentCaptor<String> sequenceIdCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(gateway).play(sequenceIdCaptor.capture());

        assertNotNull(sequenceIdCaptor.getValue());
        assertEquals(SEQUENCE_ID, sequenceIdCaptor.getValue());
    }

    @Test
    void open(){
        Mockito.when(gateway.open(PATH)).thenReturn(new SequenceModell());
        interactor.open(PATH);
        ArgumentCaptor<String> sequenceIdCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(gateway).open(sequenceIdCaptor.capture());
        assertNotNull(sequenceIdCaptor.getValue());
        assertEquals(PATH, sequenceIdCaptor.getValue());
        Mockito.verify(boundaryOut, times(1)).displaySequence(sequenceDtoArgumentCaptor.capture());
        assertNotNull(sequenceDtoArgumentCaptor.getValue());

    }

    @Test
    void loadNegativePath(){
        interactor.create();
        assertThrows(NoSuchElementException.class, () -> interactor.load("zergefasz"));
    }

    @Test
    void setTempo(){
        interactor.create();
        Mockito.verify(gateway).initMidiSequence(sequenceModellArgumentCaptor.capture());
        SequenceModell modell = sequenceModellArgumentCaptor.getValue();
        interactor.setTempo(modell.getId(), 100);
        Mockito.verify(gateway).updateTempo(Mockito.any());

        assertNotNull(sequenceModellArgumentCaptor.getValue());
    }
    @Test
    void save(){
        interactor.save(SEQUENCE_ID, PATH);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(gateway).save(captor1.capture(), captor2.capture());

        assertNotNull(captor1.getValue());
        assertNotNull(captor2.getValue());
    }

    @Test
    void addTrack(){
        interactor.create();
        Mockito.verify(gateway).initMidiSequence(sequenceModellArgumentCaptor.capture());
        SequenceModell modell = sequenceModellArgumentCaptor.getValue();

        interactor.addTrack(modell.getId());

        Mockito.verify(gateway).addTrack(sequenceModellArgumentCaptor.capture());
        Mockito.verify(boundaryOut).displayNewTrack(trackDtoArgumentCaptor.capture());
        assertNotNull(sequenceModellArgumentCaptor.getValue());
        assertNotNull(trackDtoArgumentCaptor.getValue());
    }
}