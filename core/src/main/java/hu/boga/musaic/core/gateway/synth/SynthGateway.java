package hu.boga.musaic.core.gateway.synth;

public interface SynthGateway {
    void playOneNote(int tempo, int channel, int resolution, int midiCode, int lengthInTicks);
}
