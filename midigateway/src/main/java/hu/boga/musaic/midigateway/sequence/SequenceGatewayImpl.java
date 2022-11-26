package hu.boga.musaic.midigateway.sequence;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.events.TickEvent;
import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.gateway.sequence.SequenceGateway;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.midigateway.Player;
import hu.boga.musaic.midigateway.Saver;
import hu.boga.musaic.midigateway.converters.SequenceModellToSequenceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequence;
import java.nio.charset.StandardCharsets;

public class SequenceGatewayImpl implements SequenceGateway {
    private EventBus eventBus;
    private static final Logger LOG = LoggerFactory.getLogger(SequenceGatewayImpl.class);

    MetaEventListener metaEventListener;

    @Inject
    public SequenceGatewayImpl(EventBus eventBus){
        this.eventBus = eventBus;
    }

    @Override
    public SequenceModell open(String path) {
        return new SequenceReader().open(path);
    }

    @Override
    public void save(SequenceModell modell, String path) {
        Sequence sequence = null;
        try {
            sequence = new SequenceModellToSequenceConverter(modell).convert();
            modell.name = Saver.save(sequence, path);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to save modell: " + e.getMessage(), e);
        }
    }

    @Override
    public void play(SequenceModell modell, long fromTick, long toTick) {
        removeMetaEventListener(metaEventListener);
        createMetaEventListener(modell);
        LOG.debug("start playback");
        Sequence sequence = null;
        try {
            sequence = new SequenceModellToSequenceConverter(modell).convert();
            Player.playSequence(sequence, fromTick, toTick);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to play modell: " + e.getMessage(), e);
        }
    }

    protected void createMetaEventListener(SequenceModell modell) {
        Player.createMetaEventListener(modell, eventBus);
    }

    protected void removeMetaEventListener(MetaEventListener listener) {
        Player.removeMetaEventListener(listener);
    }

    @Override
    public void stop() {
        Player.stopPlayback();
    }
}
