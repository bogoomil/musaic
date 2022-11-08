package hu.boga.musaic.core.modell.events;

import java.util.Arrays;
import java.util.Optional;

public enum CommandEnum {
    SEQUENCE_NUMBER(0x00),
    TEXT(0x01),
    COPYRIGHT(0x02),
    TRACK_NAME(0x03),
    INSTRUMENT_NAME(0x04),
    LYRICS(0x05),
    MARKER(0x06),
    CUE_MARKER(0x07),
    DEVICE_NAME(0x09),
    CHANNEL_PREFIX(0x20),
    MIDI_PORT(0x21),
    END_OF_TRACK(0x2f),
    SET_TEMPO(0x51),
    SMPTE_OFFSET(0x54),
    TIME_SIGNATURE(0x58),
    KEY_SIGNATURE(0x59),
    SEQUENCER_SPECIFIC(0x7f),
    //---
    ACTIVE_SENSIN(254),
    CHANNEL_PRESSURE(208),
    CONTINUE(251),
    CONTROL_CHANGE(176),
    END_OF_EXCLUSIVE(247),
    MIDI_TIME_CODE(241),
    NOTE_OFF(128),
    NOTE_ON(144),
    PITCH_BEND(224),
    POLY_PRESSURE(160),
    PROGRAM_CHANGE(192),
    SONG_POSITION_POINTER(242),
    SONG_SELECT(243),
    START(250),
    STOP(252),
    SYSTEM_RESET(255),
    TIMING_CLOCK(248),
    TUNE_REQUEST(246),
    UNKNOWN(-1);

    CommandEnum(int value) {
        this.value = value;
    }

    int value;

    public static Optional<CommandEnum> byIntValue(int value) {
        return Arrays.stream(values()).filter(commandEnum -> commandEnum.value == value).findFirst();
    }

    public int getIntValue() {
        return value;
    }
}
