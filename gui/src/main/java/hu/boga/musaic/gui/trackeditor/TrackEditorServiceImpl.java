package hu.boga.musaic.gui.trackeditor;

import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.track.TrackModell;

import javax.inject.Inject;

public class TrackEditorServiceImpl implements TrackEditorService, TrackBoundaryOut {

    TrackModell trackModell;
    TrackBoundaryIn boundaryIn;

    @Inject
    public TrackEditorServiceImpl(TrackBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displayTrack(TrackDto trackDto) {
        this.trackModell = new TrackModell(trackDto);
    }

    @Override
    public TrackModell getModell() {
        return trackModell;
    }
}
