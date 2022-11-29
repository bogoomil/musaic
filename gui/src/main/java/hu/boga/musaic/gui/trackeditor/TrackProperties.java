package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.midi.Instrument;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackProperties implements TrackPropertiesBoundaryOut {

    private static final Logger LOG = LoggerFactory.getLogger(TrackProperties.class);

    public TextField trackName;
    public ComboBox<Integer> cbChannel;
    public Button btnNotes;
    public Button btnDelTrack;
    public CheckBox chxbMute;
    public AnchorPane mainPanel;
    public Button volUp;
    public Button volDown;
    public Canvas canvas;
    public Pane pane;

    private TrackPropertiesBoundaryIn boundaryIn;
    private TrackDto trackDto;
    private EventBus eventBus;

    private ChangeListener<? super Integer> channelComboListener = (observable, oldValue, newValue) -> channelChanged(trackDto.id, cbChannel.getSelectionModel().getSelectedIndex());
    private ChangeListener<? super Boolean> mutedListener = (observable, oldValue, newValue) -> boundaryIn.setMuted(trackDto.id, chxbMute.isSelected());
    private TrackEditor trackEditor;
    private String[] channelToColorMapping;
    private ChangeListener<? super String> trackNameListener = (observable, oldValue, newValue) -> {
        trackDto.name = trackName.getText();
        boundaryIn.updateTrackName(trackDto);
    };
    private EventHandler<ActionEvent> delAction = event -> {
//        boundaryIn.removeTrack(trackDto.id);
//        this.eventBus.post(new TrackDeletedEvent(trackDto.id));
    };
    private EventHandler<ActionEvent> btnNotesOnAction = ev -> trackEditor.setTrack(trackDto.id, channelToColorMapping[trackDto.channel]);

    @Inject
    public TrackProperties(TrackPropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    public void setTrackDto(TrackDto trackDto, TrackEditor trackEditor, String[] channelToColorMapping) {
        this.trackDto = trackDto;
        this.trackEditor = trackEditor;
        this.channelToColorMapping = channelToColorMapping;
        updateGui();
    }

    void updateGui(){
        removeListeners();
        setProperties();
        updateMainPanelColor();
        trackEditor.setTrack(trackDto.id, channelToColorMapping[trackDto.channel]);
        initListeners();
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

    private void updateMainPanelColor() {
        try{
            setMainPanelColor(Color.web(channelToColorMapping[trackDto.channel]));
        }catch (Exception e){
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

    public void initialize() {
        volUp.setOnAction(event -> boundaryIn.updateVolume(trackDto.id, 0.1));
        volDown.setOnAction(event -> boundaryIn.updateVolume(trackDto.id, -0.1));
        cbChannel.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
    }

    private void channelChanged(final String trackId, final int channel){
        boundaryIn.updateTrackChannel(trackId, channel);
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void displayTrack(TrackDto dto) {
        LOG.debug("display track: {}", dto.channel);
        this.trackDto = dto;
        updateGui();
    }

    public void onDuplicateTrackClicked() {
        eventBus.post(new DuplicateTrackEvent(trackDto.id));
    }

    public static class DuplicateTrackEvent{
        String trackId;

        public DuplicateTrackEvent(String trackId) {
            this.trackId = trackId;
        }

        public String getTrackId() {
            return trackId;
        }
    }
}
