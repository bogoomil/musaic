package hu.boga.musaic.gui.track;

import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.trackeditor.panels.Observable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TrackServiceImpl implements TrackService, TrackBoundaryOut {

    TrackBoundaryIn boundaryIn;
    TrackDto dto;

    private final Map<String, Observable<TrackModell>> observableMap = new HashMap<>();

    @Inject
    public TrackServiceImpl(TrackBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displayTrack(TrackDto trackDto) {
        observableMap.get(trackDto.id).setValue(new TrackModell(trackDto));
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

    @Override
    public void addObservable(Observable<TrackModell> observable) {
        observableMap.put(observable.getName(), observable);
    }
}
