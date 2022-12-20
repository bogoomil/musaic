package hu.boga.musaic.midigateway;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.events.EventSystem;
import hu.boga.musaic.core.events.TickEvent;
import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.events.CommandEnum;
import hu.boga.musaic.core.modell.events.MetaMessageEventModell;
import hu.boga.musaic.midigateway.utils.MidiUtil;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Player {
    private static final Logger LOG = LoggerFactory.getLogger(Player.class);
    public static final Sequencer sequencer;
    public static final Synthesizer synth;
    private static final String SF_PATH = "/home/kunb/Zenék/soundfonts/The Fixed Jummbox Soundfont8.sf2";

    static {
        synth = initSynth();
        sequencer = initSequencer();
    }

    private static Synthesizer initSynth() {
        Synthesizer synthesizer;
        try {
            File file = new File(SF_PATH);
            Soundbank soundbank = MidiSystem.getSoundbank(file);
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            Soundbank defaultSoundbank = synthesizer.getDefaultSoundbank();
            synthesizer.unloadAllInstruments(defaultSoundbank);
            synthesizer.loadAllInstruments(soundbank);
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            throw new MusaicException("unable to initialize synth: " + e.getMessage(), e);
        }
        return synthesizer;
    }

    private static Sequencer initSequencer() {
        Sequencer sequencer1;
        try {
            sequencer1 = MidiSystem.getSequencer(false);
            sequencer1.open();

            sequencer1.getTransmitter().setReceiver(synth.getReceiver());
        } catch (MidiUnavailableException e) {
            throw new MusaicException("unable to initialize sequencer: " + e.getMessage(), e);
        }
        return sequencer1;
    }

    public static void playSequence(Sequence sequence, long fromTick, long toTick) {
        if (sequence == null) {
            throw new MusaicException("sequence is null");
        }
        addCues(sequence);
        try {
            tryingToPlaySequence(sequence, fromTick, toTick);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to play sequence " + e.getMessage());
        }
    }

    private static void addCues(Sequence sequence) {
        long tickLength = sequence.getTickLength() + 10;
        for (int i = 0; i < tickLength; i += 10) {
            String tickString = Integer.toString(i);
            MetaMessageEventModell mm = new MetaMessageEventModell(i, tickString.getBytes(StandardCharsets.UTF_8), CommandEnum.CUE_MARKER);
            MidiEvent even = MidiUtil.createMidiEventMetaMessage(i, mm.command.getIntValue(), mm.data);
            sequence.getTracks()[0].add(even);
        }
    }

    private static void tryingToPlaySequence(Sequence sequence, long fromTick, long toTick) throws InvalidMidiDataException {
        resetSequencer();
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        sequencer.setSequence(sequence);
        sequencer.setTempoFactor(1f);
        sequencer.setTickPosition(fromTick);
        sequencer.setLoopEndPoint(toTick);
        sequencer.setLoopStartPoint(fromTick);
        sequencer.start();
    }

    private static void resetSequencer() {
        sequencer.stop();
        sequencer.setLoopStartPoint(0);
        sequencer.setLoopEndPoint(0);
    }

    public static void stopPlayback() {
        sequencer.stop();
    }

    public static void playNote(int tempo, int channel, int resolution, int midiCode, int lengthInTicks, int instrument) {
        int length = (int) getNoteLenghtInMs(lengthInTicks, tempo, resolution);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MidiChannel midiChannel = synth.getChannels()[channel];
                midiChannel.programChange(instrument);
                midiChannel.noteOn(midiCode, 200);
                sleep(length);
                midiChannel.noteOff(midiCode);
            }
        });
        thread.start();
    }

    private static void sleep(int length) {
        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static double getNoteLenghtInMs(int tickCount, int tempo, int resolution) {
        return getTickLengthInMillis(tempo, resolution) * tickCount;
    }

    public static double getTickLengthInMillis(int tempo, int resolution) {
        double msInNegyed = 60000d / tempo; // 120-as tempo esetén 500 ms egy negyed hang hossza
        double measureLengthInMs = msInNegyed * 4; // ütem hossza 120-as temponál
        double tickLengthInMs = measureLengthInMs / resolution * 4;
        return tickLengthInMs;
    }

    public static void removeMetaEventListener(MetaEventListener listener) {
        sequencer.removeMetaEventListener(listener);
    }

    public static MetaEventListener createMetaEventListener(SequenceModell modell) {
        MetaEventListener listener = metaMessage -> processMetaEvent(metaMessage, modell);
        sequencer.addMetaEventListener(listener);
        return listener;
    }

    private static void processMetaEvent(MetaMessage metaMessage, SequenceModell modell) {
        if (metaMessage.getType() == CommandEnum.CUE_MARKER.getIntValue()) {
            int tick = Integer.parseInt(new String(metaMessage.getData(), StandardCharsets.UTF_8));
            EventSystem.EVENT_BUS.post(new TickEvent(modell.getId(), tick));
        }
    }

    private void loadInstrumentByPach() {


//            int bank = 128;
//            Patch[] patches = new Patch[128];
//            IntStream.range(0,128).forEach(i -> {
//                patches[i] = new Patch(bank, i);
//            });
//            synthesizer.loadInstruments(soundbank, patches);

    }


}
