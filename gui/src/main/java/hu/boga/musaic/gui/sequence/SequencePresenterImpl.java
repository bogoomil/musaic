package hu.boga.musaic.gui.sequence;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.sequence.components.ChannelMappingChangeListener;
import hu.boga.musaic.gui.sequence.components.ChannelMappingManager;
import hu.boga.musaic.gui.track.TrackModell;
import hu.boga.musaic.gui.track.TrackPresenter;
import hu.boga.musaic.gui.track.TrackPresenterFactory;
import hu.boga.musaic.gui.track.events.MeasureSelectedEvent;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

public class SequencePresenterImpl implements SequencePresenter, ChannelMappingChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SequencePresenterImpl.class);
    public static final int INITIAL_MEASURE_NUM_VALUE = 10;
    public static final int INITIAL_FOURTH_IN_BAR_VALUE = 4;
    @FXML
    private TextField tempo;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private ScrollBar horizontalScroll;
    @FXML
    private TextField measureNum;
    @FXML
    private TextField fourthInBar;
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
    IntegerProperty fourthInBarIntProp = new SimpleIntegerProperty(INITIAL_FOURTH_IN_BAR_VALUE);
    IntegerProperty measureNumIntProp = new SimpleIntegerProperty(INITIAL_MEASURE_NUM_VALUE);

    private int selectionStart;
    private int selectionEnd;
    private boolean isPlaying;
    private EventBus eventBus = new EventBus();

    @AssistedInject
    public SequencePresenterImpl(SequenceService service,
                                 TrackPresenterFactory trackPresenterFactory,
                                 @Assisted @Nullable String path) {
        this.service = service;
        this.trackPresenterFactory = trackPresenterFactory;
        if(path != null){
            this.path = Optional.of(path);
        }
        this.eventBus.register(this);
        LOG.debug("service: {}, factore: {}", service, trackPresenterFactory);
    }

    private void tempoChanged(String newValue) {
        service.updateTempo(modell.id, Integer.parseInt(newValue));
    }

    public void initialize(){
        initZoomSlider();
        initMeasureNum();
        initFourthInBar();
        playButton.setOnAction(event -> play());
        stopButton.setOnAction(event -> stop());
        tempo.textProperty().addListener((observable, oldValue, newValue) -> tempoChanged(newValue));
        path.ifPresentOrElse(path -> open(path), () -> create());
    }

    private void initZoomSlider() {
        zoomSlider.setMin(1);
        zoomSlider.setValue(10);
    }

    private void play(){
        isPlaying = true;
        service.play(modell.id, selectionStart, selectionEnd);
    }

    private void stop(){
        isPlaying = false;
        service.stop();
    }

    private void initFourthInBar() {
        fourthInBar.setText("" + INITIAL_FOURTH_IN_BAR_VALUE);
        fourthInBar.textProperty().addListener((observable, oldValue, newValue) -> fourthInBarIntProp.setValue(Integer.parseInt(fourthInBar.getText())));
    }

    private void initMeasureNum() {
        measureNum.setText("" + INITIAL_MEASURE_NUM_VALUE);
        measureNum.textProperty().addListener((observable, oldValue, newValue) -> measureNumIntProp.setValue(Integer.parseInt(newValue)));
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
        tempo.setText(modell.tempo + "");
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
        FXMLLoader loader = new FXMLLoader(TrackPresenter.class.getResource("track-view.fxml"));
        loader.setControllerFactory(c -> trackPresenterFactory.create(trackModell.id,
                zoomSlider.valueProperty(),
                horizontalScroll.valueProperty(),
                resolution,
                fourthInBarIntProp,
                measureNumIntProp, eventBus));
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

    @Subscribe
    void handleMeasureSelectedEvent(MeasureSelectedEvent event){
        this.selectionStart = event.getSelectionStart();
        this.selectionEnd = event.getSelectionEnd();
        if(isPlaying){
            stop();
            play();
        }
    }

}
