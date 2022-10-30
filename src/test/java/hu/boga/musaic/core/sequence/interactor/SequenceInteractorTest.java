package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.SeqenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.sequence.gateway.SequenceModellGateway;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class SequenceInteractorTest {

    private SequenceInteractor interactor;
    private ArgumentCaptor<SequenceDto> captor;
    private SeqenceBoundaryOut boundaryOut;
    private SequenceModellGateway gateway;

    @BeforeEach
    void setUp() {
        boundaryOut = Mockito.mock(SeqenceBoundaryOut.class);
        gateway = Mockito.mock(SequenceModellGateway.class);
        Mockito.when(gateway.create()).thenReturn(new EasyRandom().nextObject(SequenceModell.class));
        captor = ArgumentCaptor.forClass(SequenceDto.class);

        interactor = new SequenceInteractor(boundaryOut, gateway);
    }

    @Test
    void createSequence() {
        interactor.createSequence();
        Mockito.verify(boundaryOut).displaySequence(captor.capture());
        assertNotNull(captor.getValue());
    }
}