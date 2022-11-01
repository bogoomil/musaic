package hu.boga.musaic.core.gateway;

import hu.boga.musaic.core.modell.SequenceModell;

public interface MidiGateway {

    void initMidiSequence(SequenceModell modell);

    SequenceModell open(String path);

    void play(String sequenceId);

    void updateTempo(SequenceModell modell);

    void save(String sequenceId, String path);
}
