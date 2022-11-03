package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.core.modell.NoteModell;
import hu.boga.musaic.midigateway.utils.NoteUtil;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.Track;
import java.util.List;

public class TrackGatewayImpl implements TrackGateway {

    private static final Logger LOG = LoggerFactory.getLogger(TrackGatewayImpl.class);

    @Override
    public void updateTrackName(String trackId, String newName) {
        LOG.debug("updating track : {}, name: {}", trackId, newName);
        Track track = InMemorySequenceStore.TRACK_MAP.get(trackId);
        TrackUtil.updateTrackName(track, newName);
    }

    @Override
    public void removeTrack(String sequenceId, String trackId) {
        LOG.debug("removing track: {} from sequence: {}", trackId, sequenceId);
        InMemorySequenceStore.SEQUENCE_MAP.get(sequenceId).deleteTrack(InMemorySequenceStore.TRACK_MAP.get(trackId));
    }

    @Override
    public void updateTrackProgram(String trackId, int program, int channel) {
        LOG.debug("updating track: {}, channel: {}, program: {}", trackId, channel, program);
        Track track = InMemorySequenceStore.TRACK_MAP.get(trackId);
        TrackUtil.addProgramChangeEvent(track, channel, program, 0);
    }

    @Override
    public void addNotesToTrack(String trackId, List<NoteModell> notesToAdd) {
        Track track = InMemorySequenceStore.TRACK_MAP.get(trackId);
        notesToAdd.forEach(note -> {
            NoteUtil.addNote(track, (int) note.tick, note.midiCode, (int) note.length, note.velocity, note.channel);
        });
    }

    @Override
    public void moveNote(String trackId, int tick, int pitch, int newTick){
        Track track = InMemorySequenceStore.TRACK_MAP.get(trackId);
        NoteUtil.moveNote(track, tick, pitch, newTick);
    }

    @Override
    public void deleteNote(String trackId, long tick, int midiCode) {
        Track track = InMemorySequenceStore.TRACK_MAP.get(trackId);
        NoteUtil.deleteNote(track, (int) tick, midiCode);
    }
}
