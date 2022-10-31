package hu.boga.musaic.midigateway.utils;

import hu.boga.musaic.core.exceptions.MusaicException;
import hu.boga.musaic.midigateway.MidiConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrackUtilTest extends MidiUtilBaseTest {

    public static final String TRACK_NAME = "zergefos";

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        super.setUp();
        TrackUtil.updateTrackName(track, TRACK_NAME);
        TrackUtil.addProgramChangeEvent(track, 10, 20, 0);
    }

    @Test
    void getTrackName() {
        assertEquals(TRACK_NAME, TrackUtil.getTrackName(track).get());
        MidiUtil.removeEvents(track, MidiUtil.getMetaEventsByType(track, MidiConstants.METAMESSAGE_SET_NAME));
        assertEquals(Optional.empty(), TrackUtil.getTrackName(track));
    }

    @Test
    void updateTrackName() {
        TrackUtil.updateTrackName(track, "ujnev");
        assertEquals("ujnev", TrackUtil.getTrackName(track).get());
    }

    @Test
    void getChannel() {
        assertEquals(10, TrackUtil.getChannel(track).get());
        MidiUtil.removeEvents(track, MidiUtil.getMidiEventsByCommand(track, ShortMessage.PROGRAM_CHANGE));
        assertEquals(Optional.empty(), TrackUtil.getChannel(track));
    }

    @Test
    void getChannelNegativePath() throws InvalidMidiDataException {
        MidiUtil.addShortMessage(track, 23, ShortMessage.PROGRAM_CHANGE, 1, 1, 0);
        assertThrows(MusaicException.class, () -> TrackUtil.getChannel(track));
    }

    @Test
    void getProgram() {
        assertEquals(20, TrackUtil.getProgram(track).get());
        MidiUtil.removeEvents(track, MidiUtil.getMidiEventsByCommand(track, ShortMessage.PROGRAM_CHANGE));
        assertEquals(Optional.empty(), TrackUtil.getProgram(track));
    }

    @Test
    void getProgramNegativPath() throws InvalidMidiDataException {
        MidiUtil.addShortMessage(track, 23, ShortMessage.PROGRAM_CHANGE, 1, 1, 0);
        assertThrows(MusaicException.class, () -> TrackUtil.getProgram(track));
    }
}