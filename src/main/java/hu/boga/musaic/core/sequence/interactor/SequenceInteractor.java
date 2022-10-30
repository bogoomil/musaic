package hu.boga.musaic.core.sequence.interactor;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.sequence.boundary.SeqenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.gateway.SequenceModellGateway;
import hu.boga.musaic.core.sequence.interactor.converters.SequenceModellToDtoConverter;

public class SequenceInteractor implements SequenceBoundaryIn {

    private final SeqenceBoundaryOut boundaryOut;
    private final SequenceModellGateway gateway;

    public SequenceInteractor(SeqenceBoundaryOut boundaryOut, SequenceModellGateway gateway) {
        this.boundaryOut = boundaryOut;
        this.gateway = gateway;
    }

    @Override
    public void createSequence() {
        SequenceModell modell = gateway.create();
        boundaryOut.displaySequence(new SequenceModellToDtoConverter(modell).convert());
    }
}
