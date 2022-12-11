package hu.boga.musaic.gui.trackeditor;

import hu.boga.musaic.gui.track.TrackModell;

public interface TrackEditorService {
    TrackModell getModell();
    void load(String trackId);
}
