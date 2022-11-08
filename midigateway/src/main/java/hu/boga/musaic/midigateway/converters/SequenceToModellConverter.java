package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.midigateway.utils.TempoUtil;

import javax.sound.midi.Sequence;

public class SequenceToModellConverter {

    private Sequence sequence;

    public SequenceToModellConverter(Sequence sequence) {
        this.sequence = sequence;
    }

    public SequenceModell convert() {
        SequenceModell model = new SequenceModell();
        model.division = sequence.getDivisionType();
        model.resolution = sequence.getResolution();
        model.tempo = TempoUtil.getTempo(sequence);
        return model;
    }
}
