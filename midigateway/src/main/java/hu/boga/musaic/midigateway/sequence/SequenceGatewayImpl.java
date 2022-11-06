package hu.boga.musaic.midigateway.sequence;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.midigateway.Player;
import hu.boga.musaic.midigateway.Saver;
import hu.boga.musaic.midigateway.converters.SequenceModellToSequenceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class SequenceGatewayImpl implements SequenceGateway {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceGatewayImpl.class);

    @Override
    public SequenceModell open(String path) {
        return new SequenceReader().open(path);
    }

    @Override
    public void save(SequenceModell modell, String path) {
        Sequence sequence = null;
        try {
            sequence = new SequenceModellToSequenceConverter(modell).convert();
            Saver.save(sequence, path);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to save modell: " + e.getMessage(), e);
        }
    }

    @Override
    public void play(SequenceModell modell) {
        LOG.debug("start playback");
        Sequence sequence = null;
        try {
            sequence = new SequenceModellToSequenceConverter(modell).convert();
            Player.playSequence(sequence);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to play modell: " + e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        Player.stopPlayback();
    }
}
