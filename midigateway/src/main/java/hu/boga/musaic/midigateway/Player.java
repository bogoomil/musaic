package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.midigateway.utils.TempoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

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

//            Arrays.stream(soundbank.getInstruments()).forEach(instrument -> LOG.debug("custom sb instr, {}, prog: {}, bank: {}, patch: {}", instrument.getName(), instrument.getPatch().getProgram(), instrument.getPatch().getBank(), instrument.getPatch()));

            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

            Soundbank defaultSoundbank = synthesizer.getDefaultSoundbank();
            synthesizer.unloadAllInstruments(defaultSoundbank);


//            int bank = 128;
//            Patch[] patches = new Patch[128];
//            IntStream.range(0,128).forEach(i -> {
//                patches[i] = new Patch(bank, i);
//            });
//            synthesizer.loadInstruments(soundbank, patches);

            synthesizer.loadAllInstruments(soundbank);

            Arrays.stream(synthesizer.getLoadedInstruments()).forEach(instrument -> {
                LOG.debug("available bank, {}, prog: {}, instr: {}", instrument.getPatch().getBank(), instrument.getPatch().getProgram(), instrument.getName());
            });


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
        LOG.debug("start playback, tempo: {}", TempoUtil.getTempo(sequence));

        if (sequence == null) {
            throw new MusaicException("sequence is null");
        }
        try {
            tryingToPlaySequence(sequence, fromTick, toTick);
        } catch (InvalidMidiDataException e) {
            throw new MusaicException("unable to play sequence " + e.getMessage());
        }
    }

    private static void tryingToPlaySequence(Sequence sequence, long fromTick, long toTick) throws InvalidMidiDataException {
        sequencer.stop();
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        sequencer.setSequence(sequence);
        sequencer.setTempoFactor(1f);
        sequencer.setTickPosition(fromTick);
        sequencer.setLoopStartPoint(fromTick);
        sequencer.setLoopEndPoint(toTick);
        sequencer.start();
    }

    public static void stopPlayback() {
        sequencer.stop();
    }


    public static void playNote(int tempo, int channel, int resolution, int midiCode, int lengthInTicks, int instrument) {
        LOG.debug("channel: {}, instr num: {}, instr name: {}, bank: {}, sb: {}",channel, instrument);

        Arrays.stream(synth.getLoadedInstruments()).filter(instrument2 -> instrument2.getPatch().getProgram() == instrument).findAny().ifPresent(instrument2 -> {
            LOG.debug("instr name: {}, bank: {}, sb: {}",instrument2.getName(), instrument2.getPatch().getBank(), instrument2.getSoundbank().getName());

        });

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
}
