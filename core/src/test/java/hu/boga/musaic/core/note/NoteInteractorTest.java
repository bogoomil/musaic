package hu.boga.musaic.core.note;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.gateway.synth.SynthGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.NoteModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class NoteInteractorTest {

    NoteInteractor noteInteractor;
    SynthGateway synthGateway;
    private SequenceModell modell;

    @BeforeEach
    void setUp() {
        synthGateway = Mockito.mock(SynthGateway.class);
        noteInteractor = new NoteInteractor(synthGateway);

        modell = new SequenceModell();
        modell.tracks.add(new TrackModell());
        InMemorySequenceModellStore.SEQUENCE_MODELS.put(modell.getId(), modell);

    }

    @Test
    void play() {
        noteInteractor.play(modell.tracks.get(0).getId(), 12, 128);
        Mockito.verify(synthGateway).playOneNote((int) modell.tempo, modell.tracks.get(0).channel, modell.resolution, 12, 128, 0);
    }
}