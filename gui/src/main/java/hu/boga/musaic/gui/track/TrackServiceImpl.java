package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;

import javax.inject.Inject;

public class TrackServiceImpl implements TrackService, TrackPropertiesBoundaryOut {

    TrackPropertiesBoundaryIn boundaryIn;
    TrackDto dto;

    @Inject
    public TrackServiceImpl(TrackPropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displayTrack(TrackDto trackDto) {
        this.dto = trackDto;
    }

    @Override
    public TrackModell getModell() {
        return new TrackModell(dto);
    }

    @Override
    public void updateVolume(String id, double v) {
        boundaryIn.updateVolume(id, v);
    }

    @Override
    public void updateName(String id, String newName) {
        TrackDto dto = new TrackDto();
        dto.id = id;
        dto.name = newName;
        boundaryIn.updateTrackName(dto);
    }

    @Override
    public void updateChannel(String id, int newValue) {
        boundaryIn.updateTrackChannel(id, newValue);
    }

    @Override
    public void mute(String id, boolean value) {
        boundaryIn.setMuted(id, value);
    }

    @Override
    public void load(String trackId) {
        boundaryIn.load(trackId);
    }
}
