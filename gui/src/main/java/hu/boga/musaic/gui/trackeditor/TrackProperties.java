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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
    public ComboBox cbChannel;
    public Button btnNotes;
    public Button btnDelTrack;
    public CheckBox chxbMute;
    public AnchorPane mainPanel;

    private TrackPropertiesBoundaryIn boundaryIn;
    private TrackDto trackDto;
    private int resolution;
    private int currentChannel;
    private EventBus eventBus;
    private TrackEditorPanel trackEditorPanel;

    private ChangeListener<? super Integer> channelComboListener = (observable, oldValue, newValue) -> channelChanged(trackDto.id, cbChannel.getSelectionModel().getSelectedIndex());
    private ChangeListener<? super Instrument> instrumentComboListener = (observable, oldValue, newValue) -> channelChanged(trackDto.id, cbChannel.getSelectionModel().getSelectedIndex());
    private ChangeListener<? super Boolean> mutedListener = (observable, oldValue, newValue) -> boundaryIn.setMuted(trackDto.id, chxbMute.isSelected());
    private TrackEditor trackEditor;
    private String[] channelToColorMapping;
    private ChangeListener<? super String> trackNameListener = (observable, oldValue, newValue) -> {
        trackDto.name = trackName.getText();
        boundaryIn.updateTrackName(trackDto);
    };
    private EventHandler<ActionEvent> delAction = event -> {
        boundaryIn.removeTrack(trackDto.id);
        this.eventBus.post(new TrackDeletedEvent(trackDto.id));
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

        trackName.textProperty().removeListener(trackNameListener);

        cbChannel.valueProperty().removeListener(channelComboListener);
        chxbMute.selectedProperty().removeListener(mutedListener);
        this.trackDto = trackDto;

        cbChannel.getSelectionModel().select(trackDto.channel);
        trackName.setText(trackDto.name);
        chxbMute.setSelected(trackDto.muted);

        cbChannel.valueProperty().addListener(channelComboListener);
        chxbMute.selectedProperty().addListener(mutedListener);


        try{
            setMainPanelColor(Color.web(channelToColorMapping[trackDto.channel]));
        }catch (Exception e){
            setMainPanelColor(Color.GRAY);
        }
        trackEditor.setTrack(trackDto.id, channelToColorMapping[trackDto.channel]);

        trackName.textProperty().addListener(trackNameListener);
        btnDelTrack.setOnAction(delAction);
        btnNotes.setOnAction(btnNotesOnAction);

    }

    private void setMainPanelColor(Color color) {
        mainPanel.setBackground(new Background(
                new BackgroundFill(
                        color,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    public void initialize() {
        cbChannel.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
    }

    private void channelChanged(final String trackId, final int channel){
        currentChannel = channel;
        boundaryIn.updateTrackChannel(trackId, channel);
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void displayTrack(TrackDto dto) {
        LOG.debug("display track: " + dto.channel);
        this.trackDto = dto;
        updateGui();
    }
}
