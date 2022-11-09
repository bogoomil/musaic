package hu.boga.musaic.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.musaic.core.sequence.boundary.dtos.NoteDto;
import hu.boga.musaic.core.track.boundary.dtos.TrackDto;
import hu.boga.musaic.core.track.boundary.TrackBoundaryIn;
import hu.boga.musaic.core.track.boundary.TrackBoundaryOut;
import hu.boga.musaic.gui.controls.InstrumentCombo;
import hu.boga.musaic.gui.trackeditor.events.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.midi.Instrument;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditor implements TrackBoundaryOut, NoteChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditor.class);

    @FXML
    public TitledPane titledPane;
    @FXML
    public TrackEditorPanel trackEditorPanel;
    @FXML
    public Slider zoomSlider;
    @FXML
    public TextField trackName;
    @FXML
    public Label zoomLabel;
    public CheckBox cbMuted;
    @FXML
    ComboBox<Integer> channelCombo;

    TrackBoundaryIn trackBoundaryIn;

    private EventBus eventBus;
    private TrackDto trackDto;
    private int currentChannel;

    ChangeListener<? super Integer> channelComboListener = (observable, oldValue, newValue) -> {
        tracksChnnelchanged(trackDto.id, channelCombo.getSelectionModel().getSelectedIndex());
    };

    ChangeListener<? super Instrument> instrumentComboListener = (observable, oldValue, newValue) -> {
        tracksChnnelchanged(trackDto.id, channelCombo.getSelectionModel().getSelectedIndex());
    };

    ChangeListener<? super Boolean> mutedListener = (observable, oldValue, newValue) -> {
        trackBoundaryIn.setMuted(trackDto.id, cbMuted.isSelected());
    };

    @Inject
    public TrackEditor(TrackBoundaryIn trackBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
    }

    public void initialize() {
        channelCombo.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));

        zoomSlider.setMin(10);
        zoomSlider.setMax(400);
        zoomSlider.adjustValue(100);
        zoomLabel.setText("Zoom: 100%");
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                float zoomFactor = newValue.floatValue() / 100;
                trackEditorPanel.setZoomFactor(zoomFactor);
                trackEditorPanel.paintNotes();
                zoomLabel.setText("Zoom: " + newValue.intValue() + "%");

            }
        });
        trackName.textProperty().addListener((observable, oldValue, newValue) -> {
            trackDto.name = trackName.getText();
            trackBoundaryIn.updateTrackName(trackDto);
        });
        trackEditorPanel.setNoteChangeListener(this);
    }


    public void removeTrack(ActionEvent actionEvent) {
        trackBoundaryIn.removeTrack(trackDto.id);
        this.eventBus.post(new TrackDeletedEvent(trackDto.id));
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void setTrackDto(TrackDto trackDto, int resolution) {

        channelCombo.valueProperty().removeListener(channelComboListener);
        cbMuted.selectedProperty().removeListener(mutedListener);
        this.trackDto = trackDto;

        titledPane.setText("ch: " + trackDto.channel + " notes: " + trackDto.notes.size() + " (" + trackDto.id + ")");
        channelCombo.getSelectionModel().select(trackDto.channel);
        trackName.setText(trackDto.name);

        trackEditorPanel.setResolution(resolution);
        trackEditorPanel.setNotes(trackDto.notes);
        trackEditorPanel.paintNotes();
        cbMuted.setSelected(trackDto.muted);

        channelCombo.valueProperty().addListener(channelComboListener);
        cbMuted.selectedProperty().addListener(mutedListener);


    }

    @Override
    public void onAddChordEvent(AddChordEvent event) {
        trackBoundaryIn.addChord(trackDto.id, event.getTick(), event.getPitch(), event.getLength(), currentChannel, event.getChordType());
    }

    @Override
    public void onDeleteNoteEvent(DeleteNoteEvent... events) {
        List<NoteDto> dtos = Arrays.stream(events).map(event -> convertDeleteEventToNoteDto(event)).collect(Collectors.toList());
        trackBoundaryIn.deleteNotes(trackDto.id, dtos.toArray(NoteDto[]::new));

    }

    @Override
    public void onNoteMoved(NoteMovedEvent... events) {
        Arrays.stream(events).forEach(event -> {
            trackBoundaryIn.moveNote(event.getId(), event.getNewTick());
        });
        trackBoundaryIn.showTrack(trackDto.id);
    }


    private NoteDto convertDeleteEventToNoteDto(DeleteNoteEvent event){
        NoteDto dto = new NoteDto();
        dto.id = event.getNoteId();
        return dto;
    }

    @Subscribe
    private void handleRootChangedEvent(RootChangedEvent event) {
        trackEditorPanel.setCurrentRoot(event.getNoteName());
    }

    @Subscribe
    private void handleModeChangedEvent(ModeChangedEvent event) {
        trackEditorPanel.setCurrentTone(event.getTone());
    }

    public void tracksChnnelchanged(final String trackId, final int channel){
        currentChannel = channel;
        trackBoundaryIn.updateTrackChannel(trackId, channel);
    }
}
