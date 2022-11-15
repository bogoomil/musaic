package hu.boga.musaic.core.note;

import hu.boga.musaic.core.InMemorySequenceModellStore;
import hu.boga.musaic.core.gateway.synth.SynthGateway;
import hu.boga.musaic.core.modell.TrackModell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class NoteInteractor implements NoteBoundaryIn{
    SynthGateway synthGateway;
    private static final Logger LOG = LoggerFactory.getLogger(NoteInteractor.class);

    @Inject
    public NoteInteractor(SynthGateway synthGateway) {
        this.synthGateway = synthGateway;
    }

    @Override
    public void play(String trackId, int midiCode, int lengthInTicks){
        InMemorySequenceModellStore.getSequenceByTrackId(trackId).ifPresent(sequenceModell -> {
            int tempo = (int) sequenceModell.tempo;
            int resolution = sequenceModell.resolution;
            TrackModell trackModell = sequenceModell.getTrackById(trackId).get();
            int channel = trackModell.channel;
            this.synthGateway.playOneNote(tempo, channel, resolution, midiCode, lengthInTicks, sequenceModell.getChannelToProgramMappings()[channel]);
        });
    }

    @Override
    public void setNoteVolume(String noteId, double volume) {
        InMemorySequenceModellStore.getTrackByNoteId(noteId).ifPresent(trackModell -> {
            trackModell
                    .getNotes().stream()
                    .filter(noteModell -> noteId.equals(noteModell.getId()))
                    .findFirst()
                    .ifPresent(noteModell -> {
                        noteModell.velocity = volume;
                        LOG.debug("setting note {}, volume to: {}", noteId, volume);
                    });
        });
    }
}
