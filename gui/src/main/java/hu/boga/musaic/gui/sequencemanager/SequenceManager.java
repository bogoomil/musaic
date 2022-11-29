package hu.boga.musaic.gui.sequencemanager;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.GuiceModule;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.musaic.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.musaic.core.sequence.boundary.dtos.SequenceDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.sequencemanager.components.ChannelMappingManager;
import hu.boga.musaic.gui.sequencemanager.components.track.*;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class SequenceManager implements SequenceBoundaryOut {
    private static final Logger LOG = LoggerFactory.getLogger(SequenceManager.class);

    @FXML
    private MenuBar menu;
    @FXML
    private AnchorPane channelMappingPane;
    @FXML
    private VBox tracksVBox;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Slider scrollSlider;

    private String filePath;
    private SequenceDto dto;
    private boolean initialized;
    SequenceBoundaryIn boundaryIn;
    private FileChooser fileChooser = new FileChooser();
    private EventBus eventBus = new EventBus();

    @Inject
    public SequenceManager(SequenceBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
        eventBus.register(this);
    }

    @Override
    public void displaySequence(SequenceDto dto) {
        LOG.debug("sequence id: {}", dto.id);
        this.dto = dto;
        updateGui();
    }

    private void updateGui() {
        setTitle(dto.name);
        displayChannelMapping();
        displayTracks();
    }

    private void displayChannelMapping() {
        ChannelMappingManager channelMappingManager = new ChannelMappingManager(boundaryIn, dto);
        channelMappingPane.getChildren().add(channelMappingManager.create());
    }

    private void displayTracks() {
        tracksVBox.getChildren().clear();
        dto.tracks.forEach(this::displayTrack);
    }

    private void displayTrack(TrackDto trackDto) {
        FXMLLoader loader = new FXMLLoader(TrackManager.class.getResource("track-manager.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        try {
            tryCreatingNewTrackPaneld(trackDto, loader);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void tryCreatingNewTrackPaneld(TrackDto trackDto, FXMLLoader loader) throws IOException {
        BorderPane pane = loader.load();
        TrackManager trackManager = loader.getController();
        trackManager.initProperties(createTrackManagerProperties());
        trackManager.displayTrack(trackDto);
        tracksVBox.getChildren().add(pane);
    }

    private TrackManagerProperties createTrackManagerProperties() {
        TrackManagerProperties properties = new TrackManagerProperties.Builder()
                .withEventBus(eventBus)
                .withFourthInmeasure(4)
                .withMeasureNum(100)
                .withResolution(dto.resolution)
                .withZoom(zoomSlider.valueProperty())
                .withScroll(scrollSlider.valueProperty())
                .build();
        return properties;
    }

    private void setTitle(String title) {
        if (initialized) {
            Scene scene = menu.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle(dto.name != null ? dto.name : dto.id);
        }
    }

    public void initialize() {
        LOG.debug("initializing sequence manager....");
        boundaryIn.create();

        zoomSlider.setMin(1);
        zoomSlider.setMax(10);
        zoomSlider.setValue(1);

        scrollSlider.setMin(0);
        scrollSlider.setMax(100);
        scrollSlider.setValue(0);

        initialized = true;
    }

    @FXML
    private void openSequence(ActionEvent actionEvent) {
        boundaryIn.open(fileChooser.showOpenDialog(null).getAbsolutePath());
    }

    @FXML
    private void saveSequence(ActionEvent actionEvent) {
        if (filePath != null) {
            boundaryIn.save(dto.id, filePath);
        } else {
            saveAsSequence(actionEvent);
        }
    }

    @FXML
    private void saveAsSequence(ActionEvent actionEvent) {
        boundaryIn.save(dto == null ? null : dto.id, fileChooser.showSaveDialog(null).getAbsolutePath());
    }

    @FXML
    private void addTrack(ActionEvent actionEvent) {
        boundaryIn.addTrack(dto.id);
    }

    @Subscribe
    public void onTrackDeletedEvent(TrackDeletedEvent event) {
        boundaryIn.load(dto.id);
    }

    @Subscribe
    private void onDuplicateTrack(DuplicateTrackEvent event) {
        boundaryIn.duplicateTrack(event.getTrackId());
    }

}
