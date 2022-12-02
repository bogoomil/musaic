package hu.boga.musaic.gui.sequence;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.sequence.components.ChannelMappingChangeListener;
import hu.boga.musaic.gui.sequence.components.ChannelMappingManager;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.TrackPresenter;
import hu.boga.musaic.gui.track.TrackPresenterFactory;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

public class SequencePresenterImpl implements SequencePresenter, ChannelMappingChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SequencePresenterImpl.class);

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
    private SequenceModell modell;
    private Optional<String> path = Optional.empty();

    @AssistedInject
    public SequencePresenterImpl(SequenceService service,
                                 TrackPresenterFactory trackPresenterFactory,
                                 @Assisted @Nullable String path) {
        this.service = service;
        this.trackPresenterFactory = trackPresenterFactory;
        if(path != null){
            this.path = Optional.of(path);
        }
        LOG.debug("service: {}, factore: {}", service, trackPresenterFactory);
    }

    public void initialize(){
        zoomSlider.setValue(10);
        path.ifPresentOrElse(path -> open(path), () -> create());
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
        HBox box = new HBox();
        box.getChildren().add(createButtons(trackModell.id));
        box.getChildren().add(createTrackView(trackModell));
        tracksVBox.getChildren().add(box);
    }

    private VBox createButtons(String id) {
        VBox btns = new VBox();
        btns.getChildren().add(createDuplicateButton(id));
        btns.getChildren().add(createRemoveButton(id));
        return btns;
    }

    private Button createRemoveButton(String id) {
        Button button = new Button("-");
        button.setOnAction(event -> removeTrack(id));
        return button;
    }

    private Button createDuplicateButton(String id) {
        Button button = new Button("2x");
        button.setOnAction(event -> duplicateTrack(id));
        return button;
    }

    private BorderPane createTrackView(TrackModell trackModell) {

        IntegerProperty resolution = new SimpleIntegerProperty(modell.resolution);
        IntegerProperty fourthInBar = new SimpleIntegerProperty(4);

        FXMLLoader loader = new FXMLLoader(TrackPresenter.class.getResource("track-view.fxml"));
        loader.setControllerFactory(c -> trackPresenterFactory.create(trackModell.id,
                zoomSlider.valueProperty(),
                scrollSlider.valueProperty(),
                resolution, fourthInBar));
        BorderPane trackView = null;
        try {
            trackView = loader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return trackView;
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

    private void duplicateTrack(String trackId){
        service.duplicateTrack(trackId);
        updateGui();
    }

    private void removeTrack(String id) {
        service.removeTrack(id);
        updateGui();
    }

}