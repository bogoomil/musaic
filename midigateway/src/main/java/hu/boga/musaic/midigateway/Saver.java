package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.exceptions.MusaicException;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;

public class Saver {
    public static void save(Sequence sequence, String path) {
        File file = new File(path);
        try {
            MidiSystem.write(sequence, 1, file);
        } catch (Exception e) {
            throw new MusaicException("Saving " + path + " failed: " + e.getMessage());
        }
    }
}
