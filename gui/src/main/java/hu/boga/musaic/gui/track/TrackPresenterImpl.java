package hu.boga.musaic.gui.track;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.beans.property.DoubleProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackPresenterImpl implements TrackPresenter{

    private static final Logger LOG = LoggerFactory.getLogger(TrackPresenterImpl.class);

    private final TrackService trackService;
    private final TrackModell trackModell;

    @AssistedInject
    public TrackPresenterImpl(TrackService trackService,
                              @Assisted TrackModell trackModell,
                              @Assisted("zoom")DoubleProperty zoom,
                              @Assisted("scroll") DoubleProperty scroll) {
        this.trackService = trackService;
        this.trackModell = trackModell;
        zoom.addListener((observable, oldValue, newValue) -> updateZoom(newValue));
        scroll.addListener((observable, oldValue, newValue) -> updateScroll(newValue));
        LOG.debug("track presenter created with: {}, service: {}", trackModell.id, trackService);
    }

    private void updateScroll(Number newValue) {
        LOG.debug("scroll: {}", newValue);
    }

    private void updateZoom(Number newValue) {
        LOG.debug("zoom: {}", newValue);
    }

    public void initialize(){
        LOG.debug("initializing: {}", trackModell.id);
    }

}
