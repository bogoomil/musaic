package hu.boga.musaic.gateway;

import hu.boga.musaic.core.modell.SequenceModell;

public interface MidiGateway {

    void initMidiSequence(SequenceModell modell);

    SequenceModell open(String path);

    void play(String sequenceId);
}
