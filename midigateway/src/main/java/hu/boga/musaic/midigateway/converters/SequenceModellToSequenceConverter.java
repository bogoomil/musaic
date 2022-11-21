package hu.boga.musaic.midigateway.converters;

import hu.boga.musaic.core.modell.SequenceModell;
import hu.boga.musaic.core.modell.TrackModell;
import hu.boga.musaic.core.modell.events.*;
import hu.boga.musaic.midigateway.utils.MidiUtil;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import hu.boga.musaic.midigateway.utils.TempoUtil;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

public class SequenceModellToSequenceConverter {
    private SequenceModell modell;
    public SequenceModellToSequenceConverter(SequenceModell modell) {
        this.modell = modell;
    }

    public Sequence convert() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(modell.division, modell.resolution);
        modell.tracks.forEach(trackModell -> {
            if(!trackModell.muted){
                convertTracks(sequence, trackModell);
            }
        });
        TempoUtil.addTempoEvents(sequence, (int) modell.tempo);
        return sequence;
    }

    private void convertTracks(Sequence sequence, TrackModell trackModell) {
        Track track = sequence.createTrack();
        convertNotes(trackModell, track);
        convertEvents(trackModell, track);
    }

    private void convertEvents(TrackModell trackModell, Track track) {
        trackModell.eventModells.stream().filter(eventModell -> !(eventModell instanceof NoteModell)).forEach(eventModell -> {
            convertEventModellToMidiEvent(eventModell, track);
        });
    }

    private void convertNotes(TrackModell trackModell, Track track) {
        trackModell.getNotes().forEach(noteModell -> {
            int velocity = (int) (127 * noteModell.velocity);
            NoteUtil.addNote(track, (int) noteModell.tick, noteModell.midiCode, (int) noteModell.length, velocity, trackModell.channel);
        });
    }

    private void convertEventModellToMidiEvent(EventModell eventModell, Track track) {
        MidiEvent event = null;
        if(eventModell instanceof MetaMessageEventModell){
            event = MidiUtil.createMidiEventMetaMessage(eventModell.tick, eventModell.command.getIntValue(), ((MetaMessageEventModell) eventModell).data);
        } else {
            ShortMessageEventModell sme = (ShortMessageEventModell) eventModell;
            event = MidiUtil.createMidiEventShortMessage((int) eventModell.tick, eventModell.command.getIntValue(), sme.channel, sme.data1, sme.data2);
        }
        track.add(event);
    }
}
