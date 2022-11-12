package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackPropertiesBoundaryOut;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.gui.trackeditor.events.TrackDeletedEvent;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.sound.midi.Instrument;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackProperties implements TrackPropertiesBoundaryOut {
    public TextField trackName;
    public ComboBox cbChannel;
    public Button btnNotes;
    public Button btnDelTrack;
    public CheckBox chxbMute;

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

    @Inject
    public TrackProperties(TrackPropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    public void setTrackDto(TrackDto trackDto, TrackEditor trackEditor) {
        this.trackDto = trackDto;
        this.trackEditor = trackEditor;
        updateGui();
    }

    void updateGui(){
        cbChannel.valueProperty().removeListener(channelComboListener);
        chxbMute.selectedProperty().removeListener(mutedListener);
        this.trackDto = trackDto;

        cbChannel.getSelectionModel().select(trackDto.channel);
        trackName.setText(trackDto.name);
        chxbMute.setSelected(trackDto.muted);

        cbChannel.valueProperty().addListener(channelComboListener);
        chxbMute.selectedProperty().addListener(mutedListener);

    }

    public void initialize() {
        cbChannel.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
        trackName.textProperty().addListener((observable, oldValue, newValue) -> {
            trackDto.name = trackName.getText();
            boundaryIn.updateTrackName(trackDto);
        });
        btnDelTrack.setOnAction(event -> {
            boundaryIn.removeTrack(trackDto.id);
            this.eventBus.post(new TrackDeletedEvent(trackDto.id));
        });
        btnNotes.setOnAction(ev -> trackEditor.setTrack(trackDto.id));
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

    }
}
