package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;

import javax.sound.midi.*;

public class Player {
    private static final Sequencer sequencer;

    static {
        Sequencer sequencer1;
        try {
            sequencer1 = MidiSystem.getSequencer();
            sequencer1.open();
        } catch (MidiUnavailableException e) {
            throw new MusaicException("unable to initialize sequencer: " + e.getMessage(), e);
        }
        sequencer = sequencer1;
    }

    public static void playSequence(Sequence sequence){
        if(sequence == null){
            throw new MusaicException("sequence is null");
        }
        try {
            tryingToPlaySequence(sequence);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to play sequence " + e.getMessage());
        }
    }

    private static void tryingToPlaySequence(Sequence sequence) throws InvalidMidiDataException {
        sequencer.stop();
        sequencer.setLoopCount(0);
        sequencer.setSequence(sequence);
        sequencer.setTempoFactor(1f);
        sequencer.setTickPosition(0);
        sequencer.start();
    }

}