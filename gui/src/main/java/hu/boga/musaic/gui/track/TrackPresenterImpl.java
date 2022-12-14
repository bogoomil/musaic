package hu.boga.musaic.gui.track;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.constants.GuiConstants;
import hu.boga.musaic.gui.sequence.SequenceModell;
import hu.boga.musaic.gui.track.events.TrackEditingFinishedEvent;
import hu.boga.musaic.gui.track.events.TrackModellChangedEvent;
import hu.boga.musaic.gui.track.events.TrackSelectionChangedEvent;
import hu.boga.musaic.gui.track.panels.CursorPanel;
import hu.boga.musaic.gui.track.panels.GridPanel;
import hu.boga.musaic.gui.track.panels.NotesPanel;
import hu.boga.musaic.gui.track.panels.SelectionPanel;
import hu.boga.musaic.gui.trackeditor.TrackEditorPresenterFactory;
import hu.boga.musaic.gui.logic.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;

public class TrackPresenterImpl implements TrackPresenter{

    private static final Logger LOG = LoggerFactory.getLogger(TrackPresenterImpl.class);
    @FXML
    private CheckBox chxbSolo;
    @FXML
    private AnchorPane panelGroupAnchor;
    @FXML
    private BorderPane mainPanel;
    @FXML
    private CheckBox chxbMute;
    @FXML
    private TextField trackName;
    @FXML
    private ComboBox cbChannel;

    private final String trackId;
    private TrackModell trackModell;
    private final TrackService trackService;
    final DoubleProperty zoom, scroll;
    final IntegerProperty resolution, fourthInBar, measureNum;
    final EventBus eventBus;
    private ChangeListener channelListener = (observable, oldValue, newValue) -> onChannelChanged(Integer.parseInt("" + newValue));
    private ChangeListener<String> nameChangeListener = (observable, oldValue, newValue) -> onNameChanged(newValue);
    private final TrackEditorPresenterFactory trackEditorPresenterFactory;
    private final Observable<TrackModell> trackModellObservable;
    private int selectionStart;
    private int selectionEnd;

    @AssistedInject
    public TrackPresenterImpl(TrackService trackService,
                              TrackEditorPresenterFactory trackEditorPresenterFactory,
                              @Assisted String trackId,
                              @Assisted("zoom")DoubleProperty zoom,
                              @Assisted("scroll") DoubleProperty scroll,
                              @Assisted("resolution")IntegerProperty resolution,
                              @Assisted("fourthInBar") IntegerProperty fourthInBar,
                              @Assisted("measureNum") IntegerProperty measureNum,
                              @Assisted("eventBus")EventBus eventBus
                              ) {
        this.trackService = trackService;
        this.trackId = trackId;
        this.zoom = zoom;
        this.scroll = scroll;
        this.resolution = resolution;
        this.fourthInBar = fourthInBar;
        this.measureNum = measureNum;
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.trackEditorPresenterFactory = trackEditorPresenterFactory;

        trackModellObservable = new Observable<>(trackId);
        trackService.addObservable(trackModellObservable);
        trackModellObservable.addPropertyChangeListener(propertyChangeEvent -> trackModellChanged(propertyChangeEvent));
    }

    private void trackModellChanged(PropertyChangeEvent propertyChangeEvent) {
        this.trackModell = (TrackModell) propertyChangeEvent.getNewValue();
        updateMainPanelColor(SequenceModell.COLOR_MAPPING[trackModell.channel]);
        trackName.setText(trackModell.name);
        cbChannel.getSelectionModel().select(trackModell.channel);
        chxbMute.setSelected(trackModell.muted);
        eventBus.post(new TrackModellChangedEvent());

    }

    private void updateScroll(Number newValue) {
        LOG.debug("scroll: {}", newValue);
    }

    private void updateZoom(Number newValue) {
        LOG.debug("zoom: {}", newValue);
    }

    public void initialize(){
        panelGroupAnchor.getChildren().add(initPanels());
        cbChannel.getItems().addAll(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);
        removeListeners();
        trackService.load(trackId);
        initListeners();
    }

    private void removeListeners(){
        cbChannel.valueProperty().removeListener(channelListener);
        trackName.textProperty().removeListener(nameChangeListener);
    }

    private void initListeners(){
        cbChannel.valueProperty().addListener(channelListener);
        trackName.textProperty().addListener(nameChangeListener);

    }

    private Group initPanels() {
        Group panelGroup = new Group();
        panelGroup.getChildren().add(new GridPanel(GuiConstants.TRACK_HEIGHT, zoom, scroll, resolution, fourthInBar, measureNum));
        panelGroup.getChildren().add(new NotesPanel(GuiConstants.TRACK_HEIGHT, zoom, scroll, resolution, fourthInBar, measureNum, trackModellObservable));
//        panelGroup.getChildren().add(new CursorPanel(GuiConstants.TRACK_HEIGHT, zoom, scroll, resolution, fourthInBar, measureNum, trackModell));
        panelGroup.getChildren().add(new SelectionPanel(GuiConstants.TRACK_HEIGHT, zoom, scroll, resolution, fourthInBar, measureNum, trackModellObservable, eventBus, trackEditorPresenterFactory));
        return panelGroup;
    }

    private void updateMainPanelColor(String color) {
        mainPanel.setBackground(new Background(
                new BackgroundFill(
                        Color.web(color),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }


    @FXML
    private void onVolUp(ActionEvent actionEvent) {
        LOG.debug("vol up");
        trackService.updateVolume(trackModell.id, 0.1);
    }

    @FXML
    private void onNameChanged(String newName) {
        LOG.debug("name changed: {}", newName);
        trackService.updateName(trackModell.id, newName);
    }

    @FXML
    private void onChannelChanged(int newValue) {
        trackService.updateChannel(trackModell.id, newValue);
    }

    @FXML
    private void onVolDown(ActionEvent actionEvent) {
        LOG.debug("vol down");
        trackService.updateVolume(trackModell.id, -0.1);
    }

    @FXML
    private void onMute(ActionEvent actionEvent) {
        LOG.debug("mute");
        boolean muted = chxbMute.isSelected();
        trackService.mute(trackModell.id, muted);
    }

    @Subscribe
    private void handleTrackEditingFnishedEvent(TrackEditingFinishedEvent event){
        if(trackModell.id.equals(event.getTrackId())){
            LOG.debug("track editing finished: {}", event.getTrackId());
            trackService.load(event.getTrackId());
        }
    }

    @Subscribe
    void handleMeasureSelectedEvent(TrackSelectionChangedEvent event){
        this.selectionStart = event.getSelectionStart();
        this.selectionEnd = event.getSelectionEnd();
    }

    public void duplicateSelection(ActionEvent actionEvent) {
        trackService.duplicateSelection(trackModell.id, selectionStart, selectionEnd);
    }

    public void onSolo(ActionEvent actionEvent) {
        trackService.solo(trackModell.id, chxbSolo.isSelected());
    }
}
