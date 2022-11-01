package hu.boga.musaic.midigateway;

import hu.boga.musaic.core.gateway.TrackGateway;
import hu.boga.musaic.midigateway.utils.TrackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.Track;

public class TrackGatewayImpl implements TrackGateway {

    private static final Logger LOG = LoggerFactory.getLogger(TrackGatewayImpl.class);

    @Override
    public void updateTrackName(String trackId, String newName) {
        LOG.debug("updating track : {}, name: {}", trackId, newName);
        Track track = MidiGatewayImpl.TRACK_MAP.get(trackId);
        TrackUtil.updateTrackName(track, newName);
    }
}
