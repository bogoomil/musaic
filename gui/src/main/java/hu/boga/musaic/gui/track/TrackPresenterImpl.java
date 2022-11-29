package hu.boga.musaic.gui.track;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.track.events.DuplicateTrackEvent;
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
    public TrackPresenterImpl(TrackService trackService, @Assisted TrackModell trackModell, @Assisted EventBus eventBus) {
        this.trackService = trackService;
        this.trackModell = trackModell;
        this.eventBus = eventBus;
        LOG.debug("track presenter created with: {}, service: {}, eventBus: {}", trackModell, trackService, eventBus);
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
