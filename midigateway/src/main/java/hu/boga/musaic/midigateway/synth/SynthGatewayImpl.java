package hu.boga.musaic.midigateway.synth;

import hu.boga.musaic.core.gateway.synth.SynthGateway;
import hu.boga.musaic.midigateway.Player;

public class SynthGatewayImpl implements SynthGateway {

    @Override
    public void playOneNote(int tempo, int channel, int resolution, int midiCode, int lengthInTicks) {
        Player.playNote(tempo, channel, resolution, midiCode, lengthInTicks);
    }

}
