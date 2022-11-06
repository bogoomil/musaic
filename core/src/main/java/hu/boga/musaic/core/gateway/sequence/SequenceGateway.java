package hu.boga.musaic.core.gateway.sequence;

import hu.boga.musaic.core.modell.SequenceModell;

public interface SequenceGateway {
    SequenceModell open(String path);

    void save(SequenceModell modell, String path);

    void play(SequenceModell modell);

    void stop();
}
