package hu.boga.musaic.gui.sequencemanager.components.track;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.beans.property.DoubleProperty;
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
    private Pane pane;

    private TrackPropertiesBoundaryIn boundaryIn;
    private TrackDto trackDto;
    private EventBus eventBus;

    private ChangeListener<? super Integer> channelComboListener = (observable, oldValue, newValue) -> channelChanged(trackDto.id, cbChannel.getSelectionModel().getSelectedIndex());
    private ChangeListener<? super Boolean> mutedListener = (observable, oldValue, newValue) -> boundaryIn.setMuted(trackDto.id, chxbMute.isSelected());
    private ChangeListener<? super String> trackNameListener = (observable, oldValue, newValue) -> updateTrackName();
    private EventHandler<ActionEvent> delAction = event -> deleteTrack();
    private EventHandler<ActionEvent> btnNotesOnAction = event -> showNoteManager();
    private String[] colorMapping;
    private EventHandler<ActionEvent> volUpOnAction = event -> boundaryIn.updateVolume(trackDto.id, 0.1);
    private EventHandler<ActionEvent> volDownOnAction = event -> boundaryIn.updateVolume(trackDto.id, -0.1);

    @Inject
    public TrackManager(TrackPropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    public void initialize() {
        volUp.setOnAction(volUpOnAction);
        volDown.setOnAction(volDownOnAction);
        btnDelTrack.setOnAction(delAction);
        btnNotes.setOnAction(btnNotesOnAction);

        cbChannel.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
    }

    @Override
    public void displayTrack(TrackDto dto) {
        this.trackDto = dto;
        updateGui();
    }

    public void initProperties(TrackManagerProperties properties){
        initGridPainter(properties.fourthInmeasure, properties.measureNum, properties.zoom, properties.scroll);
        this.colorMapping = properties.colorMappings;
        this.eventBus = properties.eventBus;
        eventBus.register(this);
    }

    private void initGridPainter(int fourthInmeasure, int measureNum, DoubleProperty zoom, DoubleProperty scroll) {
        this.gridPainter = new GridPainter(fourthInmeasure, measureNum, zoom, scroll);
        gridPainter.setPane(pane);
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
    }

    private void removeListeners() {
        trackName.textProperty().removeListener(trackNameListener);
        cbChannel.valueProperty().removeListener(channelComboListener);
        chxbMute.selectedProperty().removeListener(mutedListener);
    }

    private void updateMainPanelColor(String color) {
        mainPanel.setBackground(new Background(
                new BackgroundFill(
                        Color.web(color),
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
