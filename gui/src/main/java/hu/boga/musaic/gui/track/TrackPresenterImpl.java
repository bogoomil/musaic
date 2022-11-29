package hu.boga.musaic.gui.track;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.track.events.DuplicateTrackEvent;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackPresenterImpl implements TrackPresenter{

    private static final Logger LOG = LoggerFactory.getLogger(TrackPresenterImpl.class);

    private final TrackService trackService;
    private final TrackModell trackModell;
    private final EventBus eventBus;

    @AssistedInject
    public TrackPresenterImpl(TrackService trackService,
                              @Assisted TrackModell trackModell,
                              @Assisted EventBus eventBus,
                              @Assisted("zoom")DoubleProperty zoom,
                              @Assisted("scroll") DoubleProperty scroll) {
        this.trackService = trackService;
        this.trackModell = trackModell;
        this.eventBus = eventBus;
        zoom.addListener((observable, oldValue, newValue) -> updateZoom(newValue));
        scroll.addListener((observable, oldValue, newValue) -> updateScroll(newValue));
        LOG.debug("track presenter created with: {}, service: {}, eventBus: {}", trackModell, trackService, eventBus);
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

    @FXML
    private void onDuplicateTrackClicked(ActionEvent actionEvent) {
        LOG.debug("duplicating track...{}", trackModell.id);
        eventBus.post(new DuplicateTrackEvent(trackModell.id));
    }
}
