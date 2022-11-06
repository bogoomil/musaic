package hu.boga.musaic.midigateway.sequence;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.midigateway.Saver;
import hu.boga.musaic.midigateway.converters.SequenceModellToSequenceConverter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class SequenceWriter {

    public Sequence write(SequenceModell modell, String path) throws InvalidMidiDataException {
        return new SequenceModellToSequenceConverter(modell).convert();
    }
}
