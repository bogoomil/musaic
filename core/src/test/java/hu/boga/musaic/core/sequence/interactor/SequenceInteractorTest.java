package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.gateway.MidiGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class SequenceInteractorTest {

    private SequenceInteractor interactor;
    private ArgumentCaptor<SequenceDto> sequenceDtoArgumentCaptor;
    private ArgumentCaptor<SequenceModell> sequenceModellArgumentCaptor;
    private SequenceBoundaryOut boundaryOut;
    private MidiGateway gateway;

    @BeforeEach
    void setUp() {
        boundaryOut = Mockito.mock(SequenceBoundaryOut.class);
        gateway = Mockito.mock(MidiGateway.class);
        sequenceDtoArgumentCaptor = ArgumentCaptor.forClass(SequenceDto.class);
        sequenceModellArgumentCaptor = ArgumentCaptor.forClass(SequenceModell.class);
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
    void loadNegativePath(){
        interactor.create();
        assertThrows(NoSuchElementException.class, () -> interactor.load("zergefasz"));
    }
}