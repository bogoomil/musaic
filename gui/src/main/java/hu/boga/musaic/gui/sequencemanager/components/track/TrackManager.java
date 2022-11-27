package hu.boga.musaic.gui.sequencemanager.components.track;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackManager implements TrackPropertiesBoundaryOut {

    public GridPainter gridPainter;

    private static final Logger LOG = LoggerFactory.getLogger(TrackManager.class);
    @FXML
    private TextField trackName;
    @FXML
    private ComboBox<Integer> cbChannel;
    @FXML
    private Button btnNotes;
    @FXML
    private Button btnDelTrack;
    @FXML
    private CheckBox chxbMute;
    @FXML
    private BorderPane mainPanel;
    @FXML
    private Button volUp;
    @FXML
    private Button volDown;
    @FXML
    private Canvas canvas;
    @FXML
    private Pane pane;

    private TrackPropertiesBoundaryIn boundaryIn;
    private TrackDto trackDto;
    private EventBus eventBus;

    private ChangeListener<? super Integer> channelComboListener = (observable, oldValue, newValue) -> channelChanged(trackDto.id, cbChannel.getSelectionModel().getSelectedIndex());
    private ChangeListener<? super Boolean> mutedListener = (observable, oldValue, newValue) -> boundaryIn.setMuted(trackDto.id, chxbMute.isSelected());
    private ChangeListener<? super String> trackNameListener = (observable, oldValue, newValue) -> updateTrackName();
    private EventHandler<ActionEvent> delAction = event -> deleteTrack();
    private EventHandler<ActionEvent> btnNotesOnAction = ev -> showNoteManager();
    private String[] colorMapping;

    @Inject
    public TrackManager(TrackPropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    public void initialize() {
        volUp.setOnAction(event -> boundaryIn.updateVolume(trackDto.id, 0.1));
        volDown.setOnAction(event -> boundaryIn.updateVolume(trackDto.id, -0.1));
        cbChannel.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
    }

    @Override
    public void displayTrack(TrackDto dto) {
        this.trackDto = dto;
        updateGui();
    }

    public void setColor(String[] color) {
        this.colorMapping = color;
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public void setGridPainter(GridPainter gridPainter) {
        this.gridPainter = gridPainter;
        this.gridPainter.setCanvas(canvas);
    }

    private void updateTrackName() {
        trackDto.name = trackName.getText();
        boundaryIn.updateTrackName(trackDto);
    }

    private void deleteTrack() {
        boundaryIn.removeTrack(trackDto.id);
        this.eventBus.post(new TrackDeletedEvent(trackDto.id));
    }

    private void showNoteManager() {
        LOG.error("not implemented yet...");
    }

    private void updateGui() {
        removeListeners();
        setProperties();
        initListeners();

        updateMainPanelColor(colorMapping[trackDto.channel]);
        gridPainter.paintGrid();
    }

    private void setProperties() {
        cbChannel.getSelectionModel().select(trackDto.channel);
        trackName.setText(trackDto.name);
        chxbMute.setSelected(trackDto.muted);
    }

    private void initListeners() {
        cbChannel.valueProperty().addListener(channelComboListener);
        chxbMute.selectedProperty().addListener(mutedListener);
        trackName.textProperty().addListener(trackNameListener);
        btnDelTrack.setOnAction(delAction);
        btnNotes.setOnAction(btnNotesOnAction);
    }

    private void updateMainPanelColor(String color) {
        try {
            setMainPanelColor(Color.web(color));
        } catch (Exception e) {
            setMainPanelColor(Color.GRAY);
        }
    }

    private void removeListeners() {
        trackName.textProperty().removeListener(trackNameListener);
        cbChannel.valueProperty().removeListener(channelComboListener);
        chxbMute.selectedProperty().removeListener(mutedListener);
    }

    private void setMainPanelColor(Color color) {
        mainPanel.setBackground(new Background(
                new BackgroundFill(
                        color,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    private void channelChanged(final String trackId, final int channel) {
        boundaryIn.updateTrackChannel(trackId, channel);
    }

    @FXML
    private void onDuplicateTrackClicked() {
        eventBus.post(new DuplicateTrackEvent(trackDto.id));
    }

}
