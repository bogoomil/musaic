package hu.boga.musaic.gui.sequence;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.gui.sequence.components.ChannelMappingChangeListener;
import hu.boga.musaic.gui.sequence.components.ChannelMappingManager;
import hu.boga.musaic.gui.track.events.DuplicateTrackEvent;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.TrackPresenter;
import hu.boga.musaic.gui.track.TrackPresenterFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

public class SequencePresenter implements ChannelMappingChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SequencePresenter.class);

    @FXML
    private Slider scrollSlider;
    @FXML
    private Slider zoomSlider;
    @FXML
    private VBox tracksVBox;
    @FXML
    private AnchorPane channelMappingPane;
    private FileChooser fileChooser = new FileChooser();

    private final SequenceService service;
    private final TrackPresenterFactory trackPresenterFactory;
    private final EventBus eventBus = new EventBus();
    private SequenceModell modell;
    private Optional<String> path = Optional.empty();

    @Inject
    public SequencePresenter(SequenceService service, TrackPresenterFactory trackPresenterFactory) {
        this.service = service;
        this.trackPresenterFactory = trackPresenterFactory;
        this.eventBus.register(this);
    }

    public void open(String path) {
        this.path = Optional.of(path);
        service.open(this.path.get());
        updateGui();
    }

    public void create() {
        service.create();
        updateGui();
    }

    private void updateGui(){
        this.modell = service.getSequence();
        LOG.debug("initializing gui with sequence: {}", modell);
        displayChannelMapping();
        displayTracks();
    }

    private void displayTracks(){
        tracksVBox.getChildren().clear();
        modell.tracks.forEach(this::displayTrack);
    }

    private void displayTrack(TrackModell trackModell)  {
        FXMLLoader loader = new FXMLLoader(TrackPresenter.class.getResource("track-view.fxml"));
        loader.setControllerFactory(c -> trackPresenterFactory.create(trackModell, eventBus, zoomSlider.valueProperty(), scrollSlider.valueProperty()));
        BorderPane trackView = null;
        try {
            trackView = loader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        tracksVBox.getChildren().add(trackView);
    }

    private void displayChannelMapping() {
        channelMappingPane.getChildren().clear();
        ChannelMappingManager channelMappingManager = new ChannelMappingManager(modell);
        channelMappingManager.addChannelMappingChangeListener(this);
        channelMappingPane.getChildren().add(channelMappingManager.create());
    }

    @FXML
    private void saveSequence(ActionEvent actionEvent) {
        path.ifPresentOrElse(p -> service.save(modell.id, p), () -> saveAsSequence(actionEvent));
    }

    @FXML
    private void saveAsSequence(ActionEvent actionEvent) {
        String path = fileChooser.showSaveDialog(null).getAbsolutePath();
        if(path != null){
            this.path = Optional.of(path);
            saveSequence(actionEvent);
        }
    }

    @FXML
    private void addTrack(ActionEvent actionEvent) {
        service.addTrack(modell.id);
        updateGui();
    }

    @Override
    public void mappingChanged(int channel, int program) {
        service.updateChannelMapping(modell.id, channel, program);
    }

    @Subscribe
    private void handleDuplicateTrackEvent(DuplicateTrackEvent event){
        service.duplicateTrack(event.getId());
        updateGui();
    }
}
