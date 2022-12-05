package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.controls.ModeCombo;
import hu.boga.musaic.gui.controls.NoteNameCombo;
import hu.boga.musaic.gui.track.TrackModell;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackEditorPresenterImpl implements TrackEditorPresenter{
    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPresenterImpl.class);
    @FXML
    private Button btnDuplicateLoop;
    @FXML
    private ModeCombo modeCombo;
    @FXML
    private NoteNameCombo rootCombo;
    @FXML
    private Button btnClearMode;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label zoomLabel;
    @FXML
    private Button btnMoveUp;
    @FXML
    private Button btnMoveDown;

    private final TrackEditorService service;
    private final String trackId;
    private final EventBus eventBus;
    private final IntegerProperty resolution, fourthInBar, measureNum;

    @AssistedInject
    public TrackEditorPresenterImpl(TrackEditorService service, @Assisted String trackId,
                                    @Assisted("resolution") IntegerProperty resolution,
                                    @Assisted("fourthInBar") IntegerProperty fourthInBar,
                                    @Assisted("measureNum") IntegerProperty measureNum,
                                    @Assisted("eventBus") EventBus eventBus) {
        this.service = service;
        this.trackId = trackId;
        this.eventBus = eventBus;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.measureNum = measureNum;

        LOG.debug("service: {}, modell: {}, measure: {}", service, trackId, measureNum.intValue());
    }
}
