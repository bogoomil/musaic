package hu.boga.musaic.gui.track;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import hu.boga.musaic.gui.sequence.SequenceModell;
import hu.boga.musaic.gui.track.panels.CursorPanel;
import hu.boga.musaic.gui.track.panels.GridPanel;
import hu.boga.musaic.gui.track.panels.NotesPanel;
import hu.boga.musaic.gui.track.panels.SelectionPanel;
import hu.boga.musaic.gui.trackeditor.TrackEditorPresenterFactory;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackPresenterImpl implements TrackPresenter{

    private static final Logger LOG = LoggerFactory.getLogger(TrackPresenterImpl.class);

    @FXML
    private Group panelGroup;
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
    }

    private void updateScroll(Number newValue) {
        LOG.debug("scroll: {}", newValue);
    }

    private void updateZoom(Number newValue) {
        LOG.debug("zoom: {}", newValue);
    }

    public void initialize(){
        cbChannel.getItems().addAll(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);
        removeListeners();
        trackService.load(trackId);
        updateGui();
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

    private void updateGui(){
        this.trackModell = trackService.getModell();
        updateMainPanelColor(SequenceModell.COLOR_MAPPING[trackModell.channel]);
        trackName.setText(trackModell.name);
        cbChannel.getSelectionModel().select(trackModell.channel);
        chxbMute.setSelected(trackModell.muted);

        GridPanel gridPanel = new GridPanel(zoom, scroll, resolution, fourthInBar, measureNum);
        panelGroup.getChildren().add(gridPanel);
        NotesPanel notesPanel = new NotesPanel(zoom, scroll, resolution, fourthInBar, measureNum, trackModell);
        panelGroup.getChildren().add(notesPanel);
        CursorPanel cursorPanel = new CursorPanel(zoom, scroll, resolution, fourthInBar, measureNum, trackModell);
        panelGroup.getChildren().add(cursorPanel);
        SelectionPanel selectionPanel = new SelectionPanel(zoom, scroll, resolution, fourthInBar, measureNum, trackModell, eventBus, trackEditorPresenterFactory);
        panelGroup.getChildren().add(selectionPanel);
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
        LOG.debug("channel changed: {}", newValue);
        trackService.updateChannel(trackModell.id, newValue);
        updateGui();
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
}
