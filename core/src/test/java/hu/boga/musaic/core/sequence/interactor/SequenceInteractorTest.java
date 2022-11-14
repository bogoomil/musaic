package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.sequence.interactor.converters.SequenceModellToDtoConverter;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class SequenceInteractorTest {

    public static final String SEQUENCE_ID = "SEQUENCE_ID";
    public static final String PATH = "PATH";

    private SequenceInteractor interactor;
    private ArgumentCaptor<SequenceDto> sequenceDtoArgumentCaptor;
    private ArgumentCaptor<TrackDto> trackDtoArgumentCaptor;
    private ArgumentCaptor<SequenceModell> sequenceModellArgumentCaptor;
    private SequenceBoundaryOut boundaryOut;

    private SequenceGateway gateway;

    private SequenceModell modell;

    @BeforeEach
    void setUp() {
        boundaryOut = Mockito.mock(SequenceBoundaryOut.class);
        gateway = Mockito.mock(SequenceGateway.class);
        sequenceDtoArgumentCaptor = ArgumentCaptor.forClass(SequenceDto.class);
        sequenceModellArgumentCaptor = ArgumentCaptor.forClass(SequenceModell.class);
        trackDtoArgumentCaptor = ArgumentCaptor.forClass(TrackDto.class);
        interactor = new SequenceInteractor(boundaryOut, gateway);
        modell = new SequenceModell();
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);
    }

    @Test
    void createSequence() {
        interactor.create();
        Mockito.verify(boundaryOut).displaySequence(sequenceDtoArgumentCaptor.capture());
        assertNotNull(sequenceDtoArgumentCaptor.getValue());
    }

    @Test
    void load(){
        interactor.load(modell.getId());
        Mockito.verify(boundaryOut, times(1)).displaySequence(sequenceDtoArgumentCaptor.capture());
        assertEquals(modell.getId(), sequenceDtoArgumentCaptor.getValue().id);
    }

    @Test
    void play(){
        interactor.play(SEQUENCE_ID, 0, 1);
        Mockito.verify(gateway).play(Mockito.any(), eq(0L), eq(1L));

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

//    @Test
//    void loadNegativePath(){
//        interactor.create();
//        assertThrows(NoSuchElementException.class, () -> interactor.load("zergefasz"));
//    }

    @Test
    void setTempo(){
        interactor.setTempo(modell.getId(), 100);
        assertEquals(100, modell.tempo);
    }

    @Test
    void save(){
        interactor.save(SEQUENCE_ID, PATH);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(gateway).save(Mockito.any(), captor2.capture());
        assertNotNull(captor2.getValue());
    }

    @Test
    void addTrack(){
        interactor.addTrack(modell.getId());
        Mockito.verify(boundaryOut).displayNewTrack(trackDtoArgumentCaptor.capture());
        assertNotNull(trackDtoArgumentCaptor.getValue().id);
    }

    @Test
    void stop(){
        interactor.stop();
        Mockito.verify(gateway).stop();
    }


    @Test
    void reloadSequence(){
        interactor.create();
        interactor.reloadSequence(modell.getId());
        Mockito.verify(boundaryOut, times(2)).displaySequence(sequenceDtoArgumentCaptor.capture());

        assertEquals(modell.getId(), sequenceDtoArgumentCaptor.getValue().id);
    }

    @Test
    void updateChannelColorMapping(){
        interactor.updateChannelColorMapping(modell.getId(), 10, "LOFOS");
        assertEquals("LOFOS", modell.channelToColorMapping[10]);
        Mockito.verify(boundaryOut).displaySequence(Mockito.any());
    }

    @Test
    void updateChannelToProgramMappings(){
        interactor.updateChannelToProgramMappings(modell.getId(), 10, 100);
        assertEquals(100, modell.getChannelToProgramMappings()[10]);
        Mockito.verify(boundaryOut).displaySequence(Mockito.any());
    }

}